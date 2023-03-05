package com.konloch.ircd.server.client;

import com.konloch.ircd.IRCd;
import com.konloch.ircd.server.channel.Channel;
import com.konloch.ircd.protocol.encoder.IRCProtocolEncoder;
import com.konloch.socket.SocketClient;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.konloch.ircd.protocol.encoder.messages.IRCOpcodes.PING;

/**
 * @author Konloch
 * @since 3/2/2023
 */
public class User
{
	private final IRCd irc;
	private final SocketClient client;
	private final UserBuffer buffer;
	private final IRCProtocolEncoder encoder;
	private final Set<String> channels;
	private boolean active = true;
	private boolean flagHasSetInitialUser;
	private boolean flagHasSetInitialNick;
	private boolean flagHasAuthorizedNick;
	private String nick = "AUTH";
	private String user;
	private String realName;
	
	/**
	 * Construct a new user
	 * @param irc the IRCd instance this user is bound to
	 * @param client the socket client this user is bound to
	 */
	public User(IRCd irc, SocketClient client)
	{
		this.irc = irc;
		this.client = client;
		this.buffer = new UserBuffer();
		this.encoder = new IRCProtocolEncoder(this);
		this.channels = new HashSet<>();
		
		//fire the on connect event
		irc.getEvents().getUserEvents().forEach(listener -> listener.onConnect(this));
	}
	
	/**
	 * Called whenever a user disconnects
	 */
	public void onDisconnect()
	{
		active = false;
		
		final User user = this;
		getChannels().removeIf(channelName ->
		{
			leaveChannel(channelName);
			
			return true;
		});
		
		getIRC().getEvents().getUserEvents().forEach(listener -> listener.onConnect(user));
	}
	
	/**
	 * Call this function to forcefully disconnect this user
	 */
	public void disconnect()
	{
		try
		{
			getClient().getSocket().close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if the network has been inactive enough for the server to send a ping
	 */
	public void keepAlive()
	{
		long now = System.currentTimeMillis();
		boolean hasTimedOutEnoughForPing =  (Math.min(now - client.getLastNetworkActivityWrite(),
				now - client.getLastNetworkActivityRead()) > getIRC().getKeepAlive());
		
		//send a ping request if there has not been enough network activity
		if (hasTimedOutEnoughForPing)
		{
			getEncoder().newSimpleMessage()
					.opcode(PING)
					.message(getIRC().getHost())
					.send();
		}
	}
	
	public void leaveChannel(String channelName)
	{
		Channel channel = getIRC().getChannels().get(channelName);
		
		if(channel != null)
		{
			channel.getUsers().remove(this);
			
			if(channel.getUsers().size() == 0)
				getIRC().getChannels().remove(channelName);
		}
	}
	
	/**
	 * Return the user network identifier
	 * @return the user network identifier
	 */
	public String getNetworkIdentifier()
	{
		return getNick() + "!" + getUser() + "@" + getIRC().getHost();
	}
	
	public IRCd getIRC()
	{
		return irc;
	}
	
	public SocketClient getClient()
	{
		return client;
	}
	
	public UserBuffer getBuffer()
	{
		return buffer;
	}
	
	public IRCProtocolEncoder getEncoder()
	{
		return encoder;
	}
	
	public Set<String> getChannels()
	{
		return channels;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public boolean isFlagHasSetInitialNick()
	{
		return flagHasSetInitialNick;
	}
	
	public void setFlagHasSetInitialNick(boolean flagHasSetInitialNick)
	{
		this.flagHasSetInitialNick = flagHasSetInitialNick;
	}
	
	public boolean isFlagHasSetInitialUser()
	{
		return flagHasSetInitialUser;
	}
	
	public void setFlagHasSetInitialUser(boolean flagHasSetInitialUser)
	{
		this.flagHasSetInitialUser = flagHasSetInitialUser;
	}
	
	public boolean isFlagHasAuthorizedNick()
	{
		return flagHasAuthorizedNick;
	}
	
	public void setFlagHasAuthorizedNick(boolean flagHasAuthorizedNick)
	{
		this.flagHasAuthorizedNick = flagHasAuthorizedNick;
	}
	
	public String getNick()
	{
		return nick;
	}
	
	public void setNick(String nick)
	{
		this.nick = nick;
	}
	
	public String getUser()
	{
		return user;
	}
	
	public void setUser(String user)
	{
		this.user = user;
	}
	
	public String getRealName()
	{
		return realName;
	}
	
	public void setRealName(String realName)
	{
		this.realName = realName;
	}
}
