package com.konloch.irc.protocol.decoder.messages.impl;

import static com.konloch.irc.protocol.encoder.messages.IRCOpcodes.RPL_PONG;

import com.konloch.irc.protocol.ProtocolMessage;
import com.konloch.irc.server.client.User;

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