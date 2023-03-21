package com.konloch.irc.server.client.data;

import static com.konloch.irc.server.client.data.UserPermissionGroup.USER;

/**
 * @author Konloch
 * @since 3/20/2023
 */
public class UserData
{
	private String nick;
	private String passwordSHA256;
	private String email;
	private UserPermissionGroup permission = USER;
	
	public String getNick()
	{
		return nick;
	}
	
	public void setNick(String nick)
	{
		this.nick = nick;
	}
	
	public String getPasswordSHA256()
	{
		return passwordSHA256;
	}
	
	public void setPasswordSHA256(String passwordSHA256)
	{
		this.passwordSHA256 = passwordSHA256;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public UserPermissionGroup getPermission()
	{
		return permission;
	}
	
	public void setPermission(UserPermissionGroup permission)
	{
		this.permission = permission;
	}
}
