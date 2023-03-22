package com.konloch.irc.protocol.decoder.messages.impl;

import com.konloch.irc.extension.events.listeners.IRCdUserListener;
import com.konloch.irc.protocol.ProtocolMessage;
import com.konloch.irc.protocol.encoder.messages.IRCOpcodes;
import com.konloch.irc.server.client.User;
import com.konloch.irc.server.util.EscapeUtil;

/**
 * @author Konloch
 * @since 3/3/2023
 */
public class PRIVMSG implements ProtocolMessage
{
	@Override
	public void run(User user, String msgVal)
	{
		if(msgVal == null || msgVal.isEmpty())
			return;
		
		if(!user.isFlagHasSetInitialNick())
			return;
		
		if(!user.isFlagHasSetInitialUser())
			return;
		
		final String[] splitData = msgVal.split(" ", 2);
		final String channel = EscapeUtil.escapeNonAlphaNumericChannel(splitData[0]);
		final String text = EscapeUtil.escapeNonAlphaNumeric(splitData[1].substring(1));
		
		for(IRCdUserListener listener : user.getIRC().getEvents().getUserEvents())
			if(!listener.onChannelMessage(user, channel, text))
				return;
		
		//private messaging between users
		if(!channel.startsWith("#"))
		{
			User other = user.getIRC().getUser(channel);
			if(other == null)
			{
				//TODO REPL_error of some kind?
				return;
			}
			
			/*if(!other.isFlagHasSetInitialUser())
			{
				//TODO REPL_error of some kind?
				return;
			}*/
			
			other.getEncoder().newUserMessage()
					.who(user.getNetworkIdentifier())
					.opcode(IRCOpcodes.RPL_PRIVMSG)
					.extra(channel)
					.message(text)
					.send();
			
			return;
		}
		
		//private messaging between a group
		//forward message to everyone in the channel
		for(User other : user.getIRC().getConnected().values())
		{
			if(other == user)
				continue;
			
			if(!other.getChannels().contains(channel))
				continue;
			
			other.getEncoder().newUserMessage()
					.who(user.getNetworkIdentifier())
					.opcode(IRCOpcodes.RPL_PRIVMSG)
					.extra(channel)
					.message(text)
					.send();
		}
	}
}
