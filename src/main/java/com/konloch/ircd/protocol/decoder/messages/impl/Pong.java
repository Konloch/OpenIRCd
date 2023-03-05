package com.konloch.ircd.protocol.decoder.messages.impl;

import com.konloch.ircd.server.client.User;
import com.konloch.ircd.protocol.ProtocolMessage;

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
