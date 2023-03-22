package com.konloch.irc;

import com.konloch.dsl.DSL;
import com.konloch.dsl.runtime.DSLRuntimeCommand;
import com.konloch.irc.extension.cli.*;
import com.konloch.irc.extension.events.EventManager;
import com.konloch.irc.extension.events.listeners.IRCdListener;
import com.konloch.irc.extension.plugins.ConnectionNotice;
import com.konloch.irc.extension.plugins.NickServ;
import com.konloch.irc.extension.plugins.ResourceLimiter;
import com.konloch.irc.protocol.decoder.IRCProtocolDecoder;
import com.konloch.irc.server.channel.Channel;
import com.konloch.irc.server.client.User;
import com.konloch.irc.server.client.UserBuffer;
import com.konloch.irc.server.util.cli.CLI;
import com.konloch.irc.server.data.config.IRCdConfigDSL;
import com.konloch.irc.server.data.IRCdDB;
import com.konloch.irc.server.data.translation.Language;
import com.konloch.irc.server.util.DumpResource;
import com.konloch.irc.server.util.ReadResource;
import com.konloch.socket.SocketClient;
import com.konloch.socket.SocketServer;
import com.konloch.taskmanager.TaskManager;
import com.konloch.util.FastStringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Konloch
 * @since 2/28/2023
 */
public class OpenIRCd
{
	private final SocketServer server;
	private final IRCProtocolDecoder decoder;
	private final IRCdDB db;
	private final DSL configParser;
	private final HashMap<String, DSLRuntimeCommand> config;
	private final EventManager events;
	private final TaskManager taskManager;
	private final CLI cli;
	private final int keepAlive;
	private boolean running = true;
	
	public static void main(String[] args) throws IOException, URISyntaxException
	{
		//create new ircd instance
		OpenIRCd irc = new OpenIRCd(new File("./config.ini"));
		
		//execute CLI and exit
		if(args != null && args.length != 0)
		{
			irc.cli.execute(args);
			return;
		}
		
		//start the ircd
		irc.start();
		
		//launch the CLI portion of the IRC server
		irc.CLI();
	}
	
	public OpenIRCd(File configFile) throws IOException, URISyntaxException
	{
		//create the decoder instance
		decoder = new IRCProtocolDecoder();
		
		//create the event manager instance
		events = new EventManager();
		
		//create the task manager instance
		taskManager = new TaskManager();
		
		//drop the default config
		if(!configFile.exists())
			DumpResource.dump("/config.ini", configFile);
		
		//create a config parser
		configParser = new IRCdConfigDSL();
		
		//parse the config file
		configParser.parse(configFile);
		
		//return the parsed config results
		config = configParser.getRuntime().getCommands();
		
		//setup translations
		setupTranslations();
		
		//insert version
		configParser.parse(new ArrayList<>(Arrays.asList("version=" + getIRCdVersion())));
		
		//copy the config variables
		keepAlive = fromConfigInt("keep.alive");
		
		//drop the default MOTD
		if(!getMOTDFile().exists())
			DumpResource.dump("/MOTD.txt", getMOTDFile());
		
		//load all server data
		db = new IRCdDB(this);
		
		//TODO plugins should be loaded here
		
		//init CLI
		cli = new CLI(this);
		
		//on boot event
		events.getIrcEvents().forEach(IRCdListener::onIRCBoot);
		
		//store the maximum simultaneous connections
		final int maximumSimultaneousConnections = fromConfigInt("simultaneous.connections.limit");
		
		//create a new socket server
		server = new SocketServer(fromConfigInt("port"), fromConfigInt("threads"),
		
		//setup the request filter
		client ->
		{
			//only allow X simultaneous connections
			if(getConnectedMap().containsKey(client.getRemoteAddress()))
				return getConnectedMap().get(client.getRemoteAddress()).incrementAndGet() <= maximumSimultaneousConnections;
			else
			{
				//no other simultaneous connections
				getConnectedMap().put(client.getRemoteAddress(), new AtomicLong(1));
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
			
			getConnected().remove(client.getUID());
			
			if(getConnectedMap().get(client.getRemoteAddress()).decrementAndGet() <= 0)
				getConnectedMap().remove(client.getRemoteAddress());
		});
		
		//set the serer timeout
		server.setTimeout(fromConfigInt("timeout"));
		
		//install CLI commands
		new OPCommands().install(this);
		new LanguageTranslationCommands().install(this);
		new HelpCommand().install(this);
		new ListUsers().install(this);
		new ListChannels().install(this);
		
		//install spam-filter extension
		if(isResourceLimiterEnabled())
			new ResourceLimiter().install(this);
		
		//install nickServ extension
		if(isNickServEnabled())
			new NickServ().install(this);
		
		//install connection notice extension
		if(isConnectionNoticeEnabled())
			new ConnectionNotice().install(this);
		
		//shutdown hook to fire irc stop events
		Runtime.getRuntime().addShutdownHook(new Thread(()-> events.getIrcEvents().forEach(IRCdListener::onIRCStop)));
	}
	
	private void setupTranslations()
	{
		String translation = fromConfig("translation");
		
		if(translation.equalsIgnoreCase("automatic"))
		{
			String userLanguage = System.getProperty("user.language");
			String systemLanguageCode = userLanguage != null ? userLanguage.toLowerCase() : "";
			translation = Language.getLanguageCodeLookup().getOrDefault(systemLanguageCode, Language.ENGLISH).name().toLowerCase();
		}
		
		//parse base (english) translations
		configParser.parse(new ArrayList<>(Arrays.asList(new String(Objects.requireNonNull(ReadResource.read("/translations/english.ini")), StandardCharsets.UTF_8).split("\\r?\\n"))));
		
		//parse non-english translations
		if(!translation.equals("english"))
			configParser.parse(new ArrayList<>(Arrays.asList(new String(Objects.requireNonNull(ReadResource.read("/translations/" + translation + ".ini")), StandardCharsets.UTF_8).split("\\r?\\n"))));
	}
	
	public Collection<User> getUsers()
	{
		return getConnected().values();
	}
	
	public User getUser(SocketClient client)
	{
		if(!getConnected().containsKey(client.getUID()))
			getConnected().put(client.getUID(), new User(this, client));
		
		return getConnected().get(client.getUID());
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

	public void CLI()
	{
		//handle CLI input while the application is running
		System.out.println(fromConfig("type.any.command") + " ('help' " + fromConfig("to.get.started") + "):");
		System.out.println();
		Scanner sc = new Scanner(System.in);
		while(running)
		{
			cli.execute(FastStringUtils.parseArguments(sc.nextLine()));
		}
		sc.close();
	}
	
	public void start()
	{
		server.start();
		taskManager.start();
		
		//announce that we're online
		System.out.println(fromConfig("startup"));
		System.out.println();
	}
	
	public void stop()
	{
		server.stopSocketServer();
		running = false;
	}
	
	public int getKeepAlive()
	{
		return keepAlive;
	}
	
	public SocketServer getServer()
	{
		return server;
	}
	
	public IRCdDB getDB()
	{
		return db;
	}
	
	public HashMap<String, Channel> getChannels()
	{
		return db.getChannels();
	}
	
	public HashMap<Long, User> getConnected()
	{
		return db.getConnected();
	}
	
	public HashMap<String, AtomicLong> getConnectedMap()
	{
		return db.getConnectedMap();
	}
	
	public HashMap<String, DSLRuntimeCommand> getConfig()
	{
		return config;
	}

	public DSL getConfigParser() {
		return configParser;
	}
	
	public EventManager getEvents()
	{
		return events;
	}
	
	public TaskManager getTaskManager()
	{
		return taskManager;
	}
	
	public CLI getCLI()
	{
		return cli;
	}
	
	public String getIRCdName()
	{
		return fromConfig("IRCd");
	}
	
	public String getIRCdVersion()
	{
		return "0.2.0";
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
		return fromConfigBoolean("resource.limiter");
	}
	
	public boolean isNickServEnabled()
	{
		return fromConfigBoolean("nickserv");
	}
	
	public boolean isConnectionNoticeEnabled()
	{
		return fromConfigBoolean("connection.notice");
	}
	
	public boolean isVerbose()
	{
		return fromConfigBoolean("verbose");
	}
	
	public String fromConfig(String variableName)
	{
		if(!config.containsKey(variableName))
			throw new RuntimeException("Could not find configuration-key `"+variableName+"`");

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
