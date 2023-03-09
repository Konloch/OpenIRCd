package com.konloch.irc.extension.events;

import java.util.ArrayList;
import java.util.List;

import com.konloch.irc.extension.events.listeners.IRCdListener;
import com.konloch.irc.extension.events.listeners.IRCdUserListener;

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
