package com.konloch.irc;

import com.konloch.disklib.DiskWriter;
import com.konloch.dsl.DSL;
import com.konloch.dsl.runtime.DSLRuntimeCommand;
import com.konloch.irc.extension.events.EventManager;
import com.konloch.irc.extension.events.listeners.IRCdListener;
import com.konloch.irc.extension.events.listeners.IRCdUserAdapter;
import com.konloch.irc.extension.plugins.NickServ;
import com.konloch.irc.extension.plugins.ResourceLimiter;
import com.konloch.irc.protocol.decoder.IRCProtocolDecoder;
import com.konloch.irc.server.channel.Channel;
import com.konloch.irc.server.client.User;
import com.konloch.irc.server.client.UserBuffer;
import com.konloch.irc.server.command.CLI;
import com.konloch.irc.server.config.IRCdConfigDSL;
import com.konloch.irc.server.util.DumpResource;
import com.konloch.socket.SocketClient;
import com.konloch.socket.SocketServer;
import com.konloch.taskmanager.TaskManager;
import com.konloch.util.FastStringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Konloch
 * @since 2/28/2023
 */
public class OpenIRCd
{
	private final SocketServer server;
	private final IRCProtocolDecoder decoder = new IRCProtocolDecoder();
	private final HashMap<String, Channel> channels = new HashMap<>();
	private final HashMap<Long, User> connected = new HashMap<>();
	private final HashMap<String, AtomicLong> connectedMap = new HashMap<>();
	private final DSL configParser;
	private final HashMap<String, DSLRuntimeCommand> config;
	private final EventManager events = new EventManager();
	private final TaskManager taskManager = new TaskManager();
	private final CLI cli = new CLI();
	private int keepAlive;
	
	public static void main(String[] args) throws IOException, URISyntaxException
	{
		//create new ircd instance
		OpenIRCd ircd = new OpenIRCd(new File("./config.ini"));
		
		//execute CLI and exit
		if(args != null && args.length != 0)
		{
			ircd.cli.execute(args);
			return;
		}
		
		//start the ircd
		ircd.start();
		
		//alert hostname look-ups are disabled
		ircd.getEvents().getUserEvents().add(new IRCdUserAdapter()
		{
			@Override
			public void onConnect(User user)
			{
				user.getEncoder().sendNotice("** This server is running an experimental IRCd");
				user.getEncoder().sendNotice("** Expect & report bugs / lack of functionality");
			}
		});
		
		//announce that we're online
		System.out.println(ircd.getIRCdVersionString() + " online and running on port " + ircd.getServer().getPort());
		System.out.println();
		
		//handle CLI while the application is running
		System.out.println("Type any command below ('help' to get started):");
		System.out.println();
		Scanner sc = new Scanner(System.in);
		while(true)
		{
			ircd.cli.execute(FastStringUtils.parseArguments(sc.nextLine()));
		}
	}
	
	public OpenIRCd(File configFile) throws IOException, URISyntaxException
	{
		//drop the default config
		if(!configFile.exists())
			DumpResource.dump("/config.ini", configFile);
		
		//create a config parser
		configParser = new IRCdConfigDSL();
		
		//parse the config file
		configParser.parse(configFile);
		
		//return the parsed config results
		config = configParser.getRuntime().getCommands();
		
		final int maximumSimultaneousConnections = fromConfigInt("limitMaxSimultaneousConnections");
		
		//copy the config variables
		keepAlive = fromConfigInt("keepAlive");
		
		//drop the default MOTD
		if(!getMOTDFile().exists())
			DumpResource.dump("/MOTD.txt", getMOTDFile());
		
		//TODO plugins should be loaded here
		
		//on boot event
		events.getIrcEvents().forEach(IRCdListener::onIRCBoot);
		
		//create a new socket server
		server = new SocketServer(fromConfigInt("port"), fromConfigInt("threads"),
		
		//setup the request filter
		client ->
		{
			//only allow X simultaneous connections
			if(connectedMap.containsKey(client.getRemoteAddress()))
				return connectedMap.get(client.getRemoteAddress()).incrementAndGet() <= maximumSimultaneousConnections;
			else
			{
				//no other simultaneous connections
				connectedMap.put(client.getRemoteAddress(), new AtomicLong(1));
				return true;
			}
		},
		
		//process the client IO
		client ->
		{
			final User user = getUser(client);
			final UserBuffer buffer = user.getBuffer();
			
			//keep alive to prevent the client from timing out
			user.keepAlive();
			
			//writing is handled here
			if(buffer.outputBufferHasData)
			{
				//we do a synchronized call to ensure no data gets malformed
				synchronized (buffer.outputBuffer)
				{
					client.write(buffer.outputBuffer.toByteArray());
					buffer.outputBuffer.reset();
					buffer.outputBufferHasData = false;
				}
			}
			
			switch(client.getState())
			{
				//signal we want to start reading into the buffer
				case 0:
					//signal that we want to start reading and to fill up the buffer
					client.setInputRead(true);
					
					//advance to stage 1
					client.setState(1);
					break;
					
				//wait until the stream has signalled the buffer has reached the end
				case 1:
					//when the buffer is full advance to stage 2
					if(!client.isInputRead())
						client.setState(2);
					break;
					
				//read the buffer and look for EOF, if we haven't reached it yet, go back to state 0
				case 2:
					//get the bytes written
					byte[] bytes = client.getInputBuffer().toByteArray();
					
					//reset the input buffer
					client.getInputBuffer().reset();
					
					//write to the buffer
					try
					{
						buffer.writeInput(bytes);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					
					if(buffer.hasReachedEOL)
						client.setState(3);
					else
						client.setState(0);
				
					break;
					
				case 3:
					//decode the message
					try
					{
						//TODO move from regex split
						String[] msg = new String(buffer.inputBuffer.toByteArray(), StandardCharsets.UTF_8).split("\\r?\\n");
						
						for(String s : msg)
							decoder.decodeMessage(user, s);
					}
					catch (Throwable e)
					{
						e.printStackTrace();
					}
					
					buffer.hasReachedEOL = false;
					buffer.inputBuffer.reset();
					
					client.setState(0);
					break;
			}
		},
		
		//on client disconnect remove the cached data
		client ->
		{
			final User user = getUser(client);
			
			try
			{
				user.onDisconnect();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			connected.remove(client.getUID());
			
			if(connectedMap.get(client.getRemoteAddress()).decrementAndGet() <= 0)
				connectedMap.remove(client.getRemoteAddress());
		});
		
		//set the serer timeout
		server.setTimeout(fromConfigInt("timeout"));
		
		
		//install CLI commands
		cli.load();
		
		//install spam-filter extension
		if(isResourceLimiterEnabled())
			new ResourceLimiter().install(this);
		
		//install nickServ extension
		if(isNickServEnabled())
			new NickServ().install(this);
		
		//shutdown hook to fire irc stop events
		Runtime.getRuntime().addShutdownHook(new Thread(()-> events.getIrcEvents().forEach(IRCdListener::onIRCStop)));
	}
	
	public Collection<User> getUsers()
	{
		return connected.values();
	}
	
	public User getUser(SocketClient client)
	{
		if(!connected.containsKey(client.getUID()))
			connected.put(client.getUID(), new User(this, client));
		
		return connected.get(client.getUID());
	}
	
	public User getUser(String nick)
	{
		Collection<User> users = getUsers();
		for(User user : users)
		{
			if(user.getNick().equals(nick))
				return user;
		}
		
		return null;
	}
	
	public void start()
	{
		server.start();
		taskManager.start();
	}
	
	public void stop()
	{
		server.stopSocketServer();
	}
	
	public int getKeepAlive()
	{
		return keepAlive;
	}
	
	public SocketServer getServer()
	{
		return server;
	}
	
	public HashMap<String, Channel> getChannels()
	{
		return channels;
	}
	
	public HashMap<Long, User> getConnected()
	{
		return connected;
	}
	
	public HashMap<String, AtomicLong> getConnectedMap()
	{
		return connectedMap;
	}
	
	public HashMap<String, DSLRuntimeCommand> getConfig()
	{
		return config;
	}
	
	public EventManager getEvents()
	{
		return events;
	}
	
	public TaskManager getTaskManager()
	{
		return taskManager;
	}
	
	public CLI getCli()
	{
		return cli;
	}
	
	public String getIRCdName()
	{
		return fromConfig("IRCdName");
	}
	
	public String getIRCdVersion()
	{
		return "0.1.0";
	}
	
	public String getIRCdVersionString()
	{
		return getIRCdName() + "-v" + getIRCdVersion();
	}
	
	public String getHost()
	{
		return fromConfig("host");
	}
	
	public String getName()
	{
		return fromConfig("name");
	}
	
	public String getWelcomeMessage()
	{
		return fromConfig("welcome");
	}
	
	public String getCreated()
	{
		return fromConfig("created");
	}
	
	public File getMOTDFile()
	{
		return new File(fromConfig("motd"));
	}
	
	public boolean isResourceLimiterEnabled()
	{
		return fromConfigBoolean("resourceLimiter");
	}
	
	public boolean isNickServEnabled()
	{
		return fromConfigBoolean("nickServ");
	}
	
	public boolean isVerbose()
	{
		return fromConfigBoolean("verbose");
	}
	
	public String fromConfig(String variableName)
	{
		String value = config.get(variableName).getVariableValue(configParser.getRuntime());
		if(value == null)
			return "";
		
		return value;
	}
	
	public boolean fromConfigBoolean(String variableName)
	{
		String value = fromConfig(variableName);
		if(FastStringUtils.isBoolean(value))
			return Boolean.parseBoolean(value);
		
		return false;
	}
	
	public int fromConfigInt(String variableName)
	{
		String value = fromConfig(variableName);
		if(FastStringUtils.isInteger(value))
			return Integer.parseInt(value);
		
		return 0;
	}
}
