package com.konloch.irc.server.client.data;

/**
 * @author Konloch
 * @since 3/20/2023
 */
public enum PermissionUserGroup
{
	USER("user"),
	LOCAL_OPERATOR("local operator"),
	GLOBAL_OPERATOR("global operator"),
	;
	
	private final String title;
	
	PermissionUserGroup(String title)
	{
		this.title = title;
	}
	
	public String getTitle()
	{
		return title;
	}
}
