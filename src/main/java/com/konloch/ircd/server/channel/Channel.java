package com.konloch.ircd.server.channel;

import com.konloch.ircd.server.client.User;

import java.util.ArrayList;
import java.util.List;

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
	
	public Channel(String name)
	{
		this.name = name;
	}
	
	public List<User> getUsers()
	{
		return users;
	}
}
