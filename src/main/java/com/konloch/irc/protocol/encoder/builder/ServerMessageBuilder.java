package com.konloch.irc.protocol.encoder.builder;

import com.konloch.irc.protocol.encoder.messages.IRCOpcodes;
import com.konloch.irc.server.client.User;

/**
 * @author Konloch
 * @since 3/5/2023
 */
public class ServerMessageBuilder
{
	private final User user;
	private final String host;
	private String opcode;
	private String message = "";
	
	public ServerMessageBuilder(User user)
	{
		this.user = user;
		this.host = user.getIRC().getHost();
	}
	
	public ServerMessageBuilder opcode(String opcode)
	{
		this.opcode = opcode;
		return this;
	}
	
	public ServerMessageBuilder message(String message)
	{
		this.message = message;
		return this;
	}
	
	public void send()
	{
		user.getEncoder().sendRaw(":" + host + " " + opcode + " :" + message + IRCOpcodes.EOL);
	}
}
