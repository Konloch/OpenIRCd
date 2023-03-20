package com.konloch.irc.server.channel;

import java.util.ArrayList;
import java.util.List;

import com.konloch.irc.server.client.User;

/**
 * @author Konloch
 * @since 3/5/2023
 */
public class Channel
{
	private final String name;
	private String description;
	private long createdAt = System.currentTimeMillis();
	private transient List<User> users = new ArrayList<>();
	
	public Channel()
	{
		this(null);
	}
	
	public Channel(String name)
	{
		this.name = name;
	}
	
	public List<User> getUsers()
	{
		return users;
	}
}