package com.konloch.irc.server.client.data;

import static com.konloch.irc.server.client.data.PermissionUserGroup.USER;

/**
 * @author Konloch
 * @since 3/20/2023
 */
public class UserData
{
	private String nick;
	private String passwordSHA256;
	private String email;
	private PermissionUserGroup usergroup = USER;
	
	public UserData()
	{
		this(null, null, null);
	}
	public UserData(String nick, String passwordSHA256, String email)
	{
		this.nick = nick;
		this.passwordSHA256 = passwordSHA256;
		this.email = email;
	}
	
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
	
	public PermissionUserGroup getUsergroup()
	{
		return usergroup;
	}
	
	public void setUsergroup(PermissionUserGroup usergroup)
	{
		this.usergroup = usergroup;
	}
}
