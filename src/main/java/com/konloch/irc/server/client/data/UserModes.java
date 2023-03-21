package com.konloch.irc.server.client.data;

/**
 * @author Konloch
 * @since 3/21/2023
 */
public enum UserModes
{
	DEAF("D", "Ignores all channel messages."),
	CALLER_ID("g", "Ignores private messages from unknown users."),
	SOFT_CALLER_ID("G", "Ignores private messages from users who you do not share channels with."),
	INVISIBLE("i", "Hides you from global WHO by normal users, and shows only shared channels in /WHOIS output."),
	DISABLE_FORWARDING("q", "Prevents channel forwards from affecting you."),
	BLOCK_UNIDENTIFIED("R", "Ignores private messages from users who are not identified with services."),
	WALLOPS("w", "Subscribes you to /wallops messages."),
	TLS("Z", "Set automatically by the network when you connect via SSL/TLS."),
	;
	
	private final String mode;
	private final String description;
	
	UserModes(String mode, String description)
	{
		this.mode = mode;
		this.description = description;
	}
	
	public String getMode()
	{
		return mode;
	}
	
	public String getDescription()
	{
		return description;
	}
}
