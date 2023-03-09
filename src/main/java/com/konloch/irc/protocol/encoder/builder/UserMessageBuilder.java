package com.konloch.irc.protocol.encoder.builder;

import com.konloch.irc.protocol.encoder.messages.IRCOpcodes;
import com.konloch.irc.server.client.User;

/**
 * @author Konloch
 * @since 3/5/2023
 */
public class UserMessageBuilder
{
	private final User user;
	private String who;
	private String opcode;
	private String extra = "";
	private String message = "";
	
	public UserMessageBuilder(User user)
	{
		this.user = user;
		this.who = user.getNetworkIdentifier();
	}
	
	public UserMessageBuilder opcode(String opcode)
	{
		this.opcode = opcode;
		return this;
	}
	
	public UserMessageBuilder message(String message)
	{
		this.message = message;
		return this;
	}
	
	public UserMessageBuilder extra(String extra)
	{
		this.extra = extra;
		return this;
	}
	
	public UserMessageBuilder who(String who)
	{
		this.who = who;
		return this;
	}
	
	public void send()
	{
		user.getEncoder().sendRaw(":" + who + " " + opcode + " " + extra + " :" + message + IRCOpcodes.EOL);
	}
}
