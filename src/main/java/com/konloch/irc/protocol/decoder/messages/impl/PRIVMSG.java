package com.konloch.irc.protocol.decoder.messages.impl;

import com.konloch.irc.extension.events.listeners.IRCdUserListener;
import com.konloch.irc.protocol.ProtocolMessage;
import com.konloch.irc.protocol.encoder.messages.IRCOpcodes;
import com.konloch.irc.server.channel.Channel;
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
		final String channelName = EscapeUtil.escapeNonAlphaNumericChannel(splitData[0]);
		final String text = EscapeUtil.escapeNonAlphaNumeric(splitData[1].substring(1));
		
		for(IRCdUserListener listener : user.getIRC().getEvents().getUserEvents())
			if(!listener.onChannelMessage(user, channelName, text))
				return;
		
		//private messaging between users
		if(!channelName.startsWith("#"))
		{
			User other = user.getIRC().getUser(channelName);
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
					.extra(channelName)
					.message(text)
					.send();
			
			return;
		}
		
		Channel channel = user.getIRC().getChannels().get(channelName);
		
		if(channel == null)
			return; //TODO REPL_error of some kind?
		
		//you cannot send messages into channels you're not a part of
		if(!channel.getUsers().contains(user))
			return; //TODO REPL_error of some kind?
		
		
		//private messaging between a group
		//forward message to everyone in the channelName
		for(User other : channel.getUsers())
		{
			if(other == user)
				continue;
			
			if(!other.getChannels().contains(channelName))
				continue;
			
			other.getEncoder().newUserMessage()
					.who(user.getNetworkIdentifier())
					.opcode(IRCOpcodes.RPL_PRIVMSG)
					.extra(channelName)
					.message(text)
					.send();
		}
	}
}
