package com.konloch.ircd.protocol.decoder.messages.impl;

import com.konloch.ircd.protocol.decoder.messages.DecodeMessage;
import com.konloch.ircd.server.channel.Channel;
import com.konloch.ircd.server.client.User;
import com.konloch.ircd.protocol.ProtocolMessage;
import com.konloch.ircd.extension.events.listeners.IRCdUserListener;

import static com.konloch.ircd.protocol.encoder.messages.IRCOpcodes.ERR_NICKNAMEINUSE;
import static com.konloch.ircd.protocol.encoder.messages.IRCOpcodes.RPL_WELCOME;

/**
 * @author Konloch
 * @since 3/3/2023
 */
public class SetNick implements ProtocolMessage
{
	public static final Object LOCK = new Object();
	
	@Override
	public void run(User user, String msgVal)
	{
		if(msgVal == null || msgVal.isEmpty())
			return;
		
		//TODO check if msg val is ascii
		
		for(IRCdUserListener listener : user.getIRC().getEvents().getUserEvents())
			if(!listener.onChangeNick(user, msgVal))
				return;
		
		final String nick = msgVal.trim();
		
		synchronized (LOCK)
		{
			User nickInUse = user.getIRC().getUser(nick);
			
			//nick in use
			if(nickInUse != null)
			{
				user.getEncoder().newServerMessage()
						.opcode(ERR_NICKNAMEINUSE)
						.send();
				return;
			}
			
			
			user.setNick(nick);
			
			if(!user.isFlagHasSetInitialNick())
				user.setFlagHasSetInitialNick(true);
				
			//send the welcome message
			user.getEncoder().newServerUserMessage()
					.opcode(RPL_WELCOME)
					.message(user.getIRC().getWelcomeMessage())
					.send();
				
			//update the room list
			for(String channelName : user.getChannels())
			{
				Channel channel = user.getIRC().getChannels().get(channelName);
				
				if(channel == null)
					continue;
					
				for (User other : channel.getUsers())
					DecodeMessage.NAMES.getDecodeRunnable().run(other, channelName);
			}
		}
	}
}