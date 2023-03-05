package com.konloch.ircd.extension.events;

import com.konloch.ircd.extension.events.listeners.IRCdListener;
import com.konloch.ircd.extension.events.listeners.IRCdUserListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Konloch
 * @since 3/3/2023
 */
public class EventManager
{
	private final List<IRCdListener> ircEvents = new ArrayList<>();
	private final List<IRCdUserListener> userEvents = new ArrayList<>();
	
	public List<IRCdListener> getIrcEvents()
	{
		return ircEvents;
	}
	
	public List<IRCdUserListener> getUserEvents()
	{
		return userEvents;
	}
}
