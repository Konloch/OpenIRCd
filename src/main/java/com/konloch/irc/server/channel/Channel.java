package com.konloch.irc.server.channel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.konloch.irc.server.client.User;

/**
 * @author Konloch
 * @since 3/5/2023
 */
public class Channel
{
	private final String name;
	private String topic;
	private String description;
	private final long createdAt = System.currentTimeMillis();
	private final HashSet<ChannelMode> modes = new HashSet<>();
	private final transient List<User> users = new ArrayList<>();
	
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
	
	public String getTopic()
	{
		return topic;
	}
	
	public void setTopic(String topic)
	{
		this.topic = topic;
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
	
	public HashSet<ChannelMode> getModes()
	{
		return modes;
	}
	
	public List<User> getUsers()
	{
		return users;
	}
	
	public boolean isPermanent()
	{
		return getModes().contains(ChannelMode.PERMANENT);
	}
	
	public boolean isPrivate()
	{
		return getModes().contains(ChannelMode.PRIVATE);
	}
	
	public boolean isInviteOnly()
	{
		return getModes().contains(ChannelMode.INVITE_ONLY);
	}
	
	public boolean isSecret()
	{
		return getModes().contains(ChannelMode.SECRET);
	}
	
	public boolean isTLSOnly()
	{
		return getModes().contains(ChannelMode.TLS_ONLY);
	}
	
	public boolean isModerated()
	{
		return getModes().contains(ChannelMode.MODERATED);
	}
}