package com.konloch.irc.protocol.decoder.messages.impl;

import com.konloch.irc.protocol.ProtocolMessage;
import com.konloch.irc.server.client.User;

/**
 * @author Konloch
 * @since 3/5/2023
 */
public class Pong implements ProtocolMessage
{
	@Override
	public void run(User user, String msgVal)
	{
		//do nothing on PONG response
	}
}
