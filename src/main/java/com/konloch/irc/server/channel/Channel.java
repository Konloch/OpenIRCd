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
	
	public String getName()
	{
		return name;
	}
	
	public Channel(String name)
	{
		this.name = name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public long getCreatedAt()
	{
		return createdAt;
	}
	
	public List<User> getUsers()
	{
		return users;
	}
}