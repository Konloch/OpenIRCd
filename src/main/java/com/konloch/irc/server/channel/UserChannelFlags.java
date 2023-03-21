package com.konloch.irc.server.channel;

/**
 * @author Konloch
 * @since 3/21/2023
 */
public enum UserChannelFlags
{
	VOICE("v", "Voice privilege flag."),
	OPERATOR("o", "Operator privilege flag."),
	OWNER("O", "Owner privilege flag."),
	;
	
	private final String mode;
	private final String description;
	
	UserChannelFlags(String mode, String description)
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
