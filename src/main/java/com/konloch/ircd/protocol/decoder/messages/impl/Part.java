package com.konloch.ircd.protocol.decoder.messages.impl;

import com.konloch.ircd.extension.events.listeners.IRCdUserListener;
import com.konloch.ircd.protocol.ProtocolMessage;
import com.konloch.ircd.protocol.decoder.messages.DecodeMessage;
import com.konloch.ircd.server.channel.Channel;
import com.konloch.ircd.server.client.User;
import com.konloch.util.FastStringUtils;

import static com.konloch.ircd.protocol.encoder.messages.IRCOpcodes.RPL_JOIN;

/**
 * @author Konloch
 * @since 3/3/2023
 */
public class Part implements ProtocolMessage
{
	@Override
	public void run(User user, String msgVal)
	{
		if(msgVal == null || msgVal.isEmpty())
			return;
		
		//TODO check if msg val is ascii
		
		if(!user.isFlagHasSetInitialNick())
			user.setFlagHasSetInitialNick(true);
		
		for(IRCdUserListener listener : user.getIRC().getEvents().getUserEvents())
			if(!listener.onJoinChannel(user, msgVal))
				return;
		
		if(!msgVal.startsWith("#") || msgVal.length() < 2)
			return;
		
		final String[] channels = FastStringUtils.split(msgVal.replace(" ", ""), ",");
		
		for(String channelName : channels)
			leaveChannel(user, channelName);
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
		
		//TODO
		//signal the join back to the client
		/*user.getEncoder().newUserMessage()
				.opcode(RPL_JOIN)
				.message(channelName)
				.send();*/
		
		//update the room list
		for(User other : channel.getUsers())
			DecodeMessage.NAMES.getDecodeRunnable().run(other, channelName);
	}
}
