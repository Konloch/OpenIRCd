package com.konloch.irc.protocol.encoder.builder;

import com.konloch.irc.protocol.encoder.messages.IRCOpcodes;
import com.konloch.irc.server.client.User;

/**
 * @author Konloch
 * @since 3/5/2023
 */
public class ServerUserMessageBuilder
{
	private final User user;
	private final String host;
	private String nick;
	private String opcode;
	private String message = "";
	
	public ServerUserMessageBuilder(User user)
	{
		this.user = user;
		this.host = user.getIRC().getHost();
		this.nick = user.getNick();
	}
	
	public ServerUserMessageBuilder opcode(String opcode)
	{
		this.opcode = opcode;
		return this;
	}
	
	public ServerUserMessageBuilder message(String message)
	{
		this.message = message;
		return this;
	}
	
	public ServerUserMessageBuilder nick(String nick)
	{
		this.nick = nick;
		return this;
	}
	
	public void send()
	{
		user.getEncoder().sendRaw(":" + host + " " + opcode + " " + nick + " :" + message + IRCOpcodes.EOL);
	}
}
