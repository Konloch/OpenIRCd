package com.konloch.irc.server.data;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.events.listeners.IRCdAdapter;
import com.konloch.irc.server.channel.Channel;
import com.konloch.irc.server.client.User;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Konloch
 * @since 3/19/2023
 */
public class IRCdDB
{
	private final OpenIRCd irc;
	private final HashMap<String, Channel> channels;
	private final transient HashMap<Long, User> connected;
	private final transient HashMap<String, AtomicLong> connectedMap;
	
	public IRCdDB(OpenIRCd irc)
	{
		this.irc = irc;
		
		//TODO data needs to be loaded here
		channels = new HashMap<>();
		
		//init connected maps
		connected = new HashMap<>();
		connectedMap = new HashMap<>();
		
		//save on IRCd shutdown
		irc.getEvents().getIrcEvents().add(new IRCdAdapter()
		{
			@Override
			public void onIRCStop()
			{
				save();
			}
		});
	}
	
	public void save()
	{
	
	}
	
	public HashMap<String, Channel> getChannels()
	{
		return channels;
	}
	
	public HashMap<String, AtomicLong> getConnectedMap()
	{
		return connectedMap;
	}
	
	public HashMap<Long, User> getConnected()
	{
		return connected;
	}
}
