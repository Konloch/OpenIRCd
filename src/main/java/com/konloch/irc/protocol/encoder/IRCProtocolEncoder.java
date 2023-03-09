package com.konloch.irc.protocol.encoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.konloch.irc.protocol.encoder.builder.ServerMessageBuilder;
import com.konloch.irc.protocol.encoder.builder.ServerUserMessageBuilder;
import com.konloch.irc.protocol.encoder.builder.SimpleMessageBuilder;
import com.konloch.irc.protocol.encoder.builder.UserMessageBuilder;
import com.konloch.irc.server.client.User;

/**
 * @author Konloch
 * @since 3/2/2023
 */
public class IRCProtocolEncoder
{
	private final User user;
	
	public IRCProtocolEncoder(User user)
	{
		this.user = user;
	}
	
	public void sendNotice(String notice)
	{
		newServerUserMessage().opcode("NOTICE").message(notice).send();
	}
	
	public ServerMessageBuilder newServerMessage()
	{
		return new ServerMessageBuilder(getUser());
	}
	
	public ServerUserMessageBuilder newServerUserMessage()
	{
		return new ServerUserMessageBuilder(getUser());
	}
	
	public UserMessageBuilder newUserMessage()
	{
		return new UserMessageBuilder(getUser());
	}
	
	public SimpleMessageBuilder newSimpleMessage()
	{
		return new SimpleMessageBuilder(getUser());
	}
	
	public void sendRaw(String message)
	{
		if(user.getIRC().isVerbose())
			System.out.println("O: " + message.replace("\n", "").replace("\r", ""));
		
		try
		{
			user.getBuffer().writeOutputAndFlush((message).getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public User getUser()
	{
		return user;
	}
}