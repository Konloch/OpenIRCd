package com.konloch.ircd.protocol.decoder.messages.impl;

import com.konloch.ircd.server.client.User;
import com.konloch.ircd.protocol.ProtocolMessage;

import static com.konloch.ircd.protocol.encoder.messages.IRCOpcodes.RPL_PONG;

/**
 * @author Konloch
 * @since 3/3/2023
 */
public class Ping implements ProtocolMessage
{
	@Override
	public void run(User user, String msgVal)
	{
		if(msgVal == null || msgVal.isEmpty())
			return;
		
		if(!user.isFlagHasSetInitialUser())
			return;
		
		user.getEncoder().newServerMessage()
				.opcode(RPL_PONG)
				.message(msgVal)
				.send();
	}
}