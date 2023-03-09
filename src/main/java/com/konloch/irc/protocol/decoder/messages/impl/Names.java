package com.konloch.irc.protocol.decoder.messages.impl;

import static com.konloch.irc.protocol.encoder.messages.IRCOpcodes.RPL_ENDOFNAMES;
import static com.konloch.irc.protocol.encoder.messages.IRCOpcodes.RPL_NAMREPLY;

import com.konloch.irc.protocol.ProtocolMessage;
import com.konloch.irc.server.channel.Channel;
import com.konloch.irc.server.client.User;

/**
 * @author Konloch
 * @since 3/5/2023
 */
public class Names implements ProtocolMessage
{
	@Override
	public void run(User user, String msgVal)
	{
		final String channelName = msgVal;
		final Channel channel = user.getIRC().getChannels().get(channelName);
		
		if(channel == null)
		{
			//TODO repl_channel doesnt exist
			return;
		}
		
		StringBuilder users = new StringBuilder();
		for(User other : channel.getUsers())
		{
			users.append("@")
					.append(other.getNick()).append(" ");
		}
		
		user.getEncoder().newUserMessage()
				.opcode(RPL_NAMREPLY)
				.extra(user.getNick() + " = " + channelName)
				.message(users.toString())
				.send();
		
		user.getEncoder().newUserMessage()
				.opcode(RPL_ENDOFNAMES)
				.extra(user.getNick() + " " + channelName)
				.message("End of /NAMES list.")
				.send();
	}
}