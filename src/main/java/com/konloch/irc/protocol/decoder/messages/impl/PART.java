package com.konloch.irc.protocol.decoder.messages.impl;

import static com.konloch.irc.protocol.encoder.messages.IRCOpcodes.RPL_PART;

import com.konloch.irc.extension.events.listeners.IRCdUserListener;
import com.konloch.irc.protocol.ProtocolMessage;
import com.konloch.irc.protocol.decoder.messages.DecodeMessage;
import com.konloch.irc.server.channel.Channel;
import com.konloch.irc.server.client.User;
import com.konloch.irc.server.util.EscapeUtil;
import com.konloch.util.FastStringUtils;

/**
 * @author Konloch
 * @since 3/3/2023
 */
public class PART implements ProtocolMessage
{
	@Override
	public void run(User user, String msgVal)
	{
		if(msgVal == null || msgVal.isEmpty())
			return;
		
		//don't process until they have set first nick
		if(!user.isFlagHasSetInitialNick())
			return;
		
		for(IRCdUserListener listener : user.getIRC().getEvents().getUserEvents())
			if(!listener.onJoinChannel(user, msgVal))
				return;
		
		if(!msgVal.startsWith("#") || msgVal.length() < 2)
			return;
		
		final String[] channels = FastStringUtils.split(msgVal.replace(" ", ""), ",");
		
		for(String channelName : channels)
			leaveChannel(user,  EscapeUtil.escapeNonAlphaNumericChannel(channelName));
	}
	
	private void leaveChannel(User user, String channelName)
	{
		Channel channel = user.getIRC().getChannels().get(channelName);
		
		if(channel != null)
		{
			for(IRCdUserListener listener : user.getIRC().getEvents().getUserEvents())
				listener.onLeaveChannel(user, channelName);
			
			user.getChannels().remove(channelName);
			user.leaveChannel(channelName);
		}
		
		//signal the join back to the client
		user.getEncoder().newUserMessage()
				.opcode(RPL_PART)
				.message(channelName)
				.send();
		
		//update the room list
		for(User other : channel.getUsers())
			DecodeMessage.NAMES.getDecodeRunnable().run(other, channelName);
	}
}
