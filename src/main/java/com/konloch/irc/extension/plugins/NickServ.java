package com.konloch.irc.extension.plugins;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.Plugin;
import com.konloch.irc.extension.events.listeners.IRCdAdapter;
import com.konloch.irc.extension.events.listeners.IRCdUserAdapter;
import com.konloch.irc.protocol.encoder.messages.IRCOpcodes;
import com.konloch.irc.server.client.User;
import com.konloch.irc.server.client.data.UserData;
import com.konloch.irc.server.util.Checksum;

/**
 * @author Konloch
 * @since 3/5/2023
 */
public class NickServ implements Plugin
{
	private static final Object LOCK = new Object();
	private static final String nickServName = "NickServ";
	private static final String nickServIdentifier = "NickServ!NickServ@";
	private OpenIRCd irc;
	private HashMap<String, NSAttempts> attemptLogs;
	
	@Override
	public void install(OpenIRCd irc)
	{
		this.irc = irc;
		attemptLogs = new HashMap<>();
		
		irc.getEvents().getIrcEvents().add(new IRCdAdapter()
		{
			@Override
			public void onIRCStop()
			{
				save();
			}
		});
		
		final ArrayList<String> removeList = new ArrayList<>();
		irc.getTaskManager().delayLoop(1000, (task)->
		{
			//TODO x minute amount should be a configuration file
			for(String ipAddress : attemptLogs.keySet())
			{
				NSAttempts attemptLog = attemptLogs.get(ipAddress);
				
				//the attempt log resets after 15 minutes from the last login attempt
				if(System.currentTimeMillis()-attemptLog.loginAttemptsLast >= 1000 * 60 * 15)
				{
					attemptLog.loginAttempts = 0;
					removeList.add(ipAddress);
				}
			}
			
			if(!removeList.isEmpty())
			{
				removeList.removeIf(ipAddress ->
				{
					attemptLogs.remove(ipAddress);
					return true;
				});
			}
		});
		
		irc.getEvents().getUserEvents().add(new IRCdUserAdapter()
		{
			@Override
			public void onConnect(User user)
			{
				if(hasReachedAttemptLimit(user))
				{
					user.disconnect();
					//TODO some kind of alert / warning to console or admins?
				}
			}
			
			@Override
			public boolean canChangeNick(User user, String nick)
			{
				//block setting nickServ as a nick
				return !nick.equalsIgnoreCase(nickServName);
			}
			
			@Override
			public void onChangeNick(User user, String nick)
			{
				if(irc.getDB().getRegisteredUsers().containsKey(nick.toLowerCase()))
				{
					//send registered
					user.getEncoder().sendNotice(irc.fromConfig("registered"));
					sendMessage(user, "This nick is registered, type '/msg NickServ IDENTIFY <password>` to authorize yourself as the owner.");
					
					//automatically disconnect after 30 seconds of being unauthorized
					//TODO 30 seconds should be in config file
					user.getIRC().getTaskManager().delay(30_000, (task)->
					{
						//user has disconnected
						if(!user.isActive())
							return;
						
						//nick already changed
						if(!user.getNick().equals(nick))
							return;
						
						//user has not identified yet
						if(!user.isFlagHasAuthorizedNick())
							user.disconnect();
					});
				}
				else
				{
					//send unregistered
					user.getEncoder().sendNotice(irc.fromConfig("unregistered"));
				}
			}
			
			@Override
			public boolean onJoinChannel(User user, String channel)
			{
				//prevent joining channels if you are not registered yet
				if(irc.getDB().getRegisteredUsers().containsKey(user.getNick().toLowerCase())
						&& !user.isFlagHasAuthorizedNick())
					return false;
				
				return true;
			}
			
			@Override
			public boolean onChannelMessage(User user, String channel, String message)
			{
				final String nick = user.getNick();
				final boolean isNickRegistered = isNickRegistered(nick);
				String password;
				String email = null;
				
				//TODO check if channel is to a user, then check if that user is not authorized yet & they are in the registry
				// if they are, return false and don't allow users to pm non-authorized users
				
				if (channel.equalsIgnoreCase(nickServName))
				{
					String[] split = message.split(" ", 2);
					switch(split[0].toUpperCase())
					{
						default:
							sendMessage(user, "Invalid command. Use /msg NickServ help for a command listing.");
							break;
							
						case "H":
						case "HELP":
							sendMessage(user, "/msg NickServ REGISTER <password> <email>");
							sendMessage(user, "+ Register your nick using the REGISTER command");
							sendMessage(user, "");
							sendMessage(user, "/msg NickServ IDENTIFY <password>");
							sendMessage(user, "+ Authenticate your nick on login using the IDENTIFY command");
							break;
							
						case "R":
						case "REG":
						case "REGISTER":
							if(split[1].contains(" ")) //registered with e-mail
							{
								split = message.split(" ", 3);
								password = split[1];
								email = split[2].trim();
							}
							else
							{
								password = split[1];
							}
							
							if(user.isFlagHasAuthorizedNick())
							{
								sendMessage(user, "Your session has already authenticated you as the owner of this nick.");
								return false;
							}
							
							if(isNickRegistered)
							{
								sendMessage(user, "This nick is already registered. If you are the owner you can authenticate using /msg NickServ IDENTIFY <password>");
								return false;
							}
							
							synchronized (LOCK)
							{
								try
								{
									//build the nick storage data
									UserData data = new UserData(nick, Checksum.sha256(password), email);
									
									//store the nick
									irc.getDB().getRegisteredUsers().put(nick.toLowerCase(), data);
									
									//set as authorized
									user.setFlagHasAuthorizedNick(true);
									
									sendMessage(user, "Nick successfully registered.");
								}
								catch (NoSuchAlgorithmException e)
								{
									e.printStackTrace();
								}
							}
							
							break;
						
						case "I":
						case "IDENT":
						case "IDENTIFY":
							password = split[1].trim();
							
							if(user.isFlagHasAuthorizedNick())
							{
								sendMessage(user, "Your session has already authenticated you as the owner of this nick.");
								return false;
							}
							
							if(!isNickRegistered)
							{
								sendMessage(user, "Your nick is not currently registered.");
								return false;
							}
							
							boolean validPassword = false;
							try
							{
								if(irc.getDB().getRegisteredUsers().get(nick).getPasswordSHA256()
										.equals(Checksum.sha256(password)))
									validPassword = true;
							}
							catch (NoSuchAlgorithmException e)
							{
								e.printStackTrace();
							}
							
							if(validPassword)
							{
								user.setFlagHasAuthorizedNick(true);
								sendMessage(user, "Welcome back " + user.getNick() + ".");
							}
							else
							{
								NSAttempts attemptLog = attemptLogs.get(user.getClient().getRemoteAddress());
								
								if(attemptLog == null)
								{
									attemptLog = new NSAttempts();
									attemptLogs.put(user.getClient().getRemoteAddress(), attemptLog);
								}
								
								if(attemptLog.loginAttempts == 0)
									attemptLog.loginAttemptsStarted = System.currentTimeMillis();
								
								attemptLog.loginAttemptsLast = System.currentTimeMillis();
								attemptLog.loginAttempts++;
								sendMessage(user, "Invalid password - you have " + attemptLog.loginAttempts + "/10 attempts left.");
							}
							
							break;
					}
					
					return false;
				}
				
				//only allow messages to be sent if the nick is unregistered, or if the user has authorized the nick
				return !isNickRegistered || user.isFlagHasAuthorizedNick();
			}
		});
	}
	
	private void save()
	{
	
	}
	
	public void sendMessage(User user, String message)
	{
		user.getEncoder().newUserMessage()
				.who(nickServIdentifier)
				.opcode(IRCOpcodes.RPL_PRIVMSG)
				.extra(user.getNick())
				.message(message)
				.send();
	}
	
	public boolean hasReachedAttemptLimit(User user)
	{
		NSAttempts attemptLog = attemptLogs.get(user.getClient().getRemoteAddress());
		
		if(attemptLog == null)
			return false;
		
		//TODO login attempt limit should be moved to config file
		return attemptLog.loginAttempts >= 10;
	}
	
	public boolean isNickRegistered(String nick)
	{
		return irc.getDB().getRegisteredUsers().containsKey(nick);
	}
	
	public static class NSAttempts
	{
		public int loginAttempts;
		public long loginAttemptsStarted;
		public long loginAttemptsLast;
	}
}
