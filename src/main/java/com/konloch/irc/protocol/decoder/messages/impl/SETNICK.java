package com.konloch.irc.protocol.decoder.messages.impl;

import com.konloch.irc.extension.events.listeners.IRCdUserListener;
import com.konloch.irc.protocol.ProtocolMessage;
import com.konloch.irc.protocol.decoder.messages.DecodeMessage;
import com.konloch.irc.server.channel.Channel;
import com.konloch.irc.server.client.User;
import com.konloch.irc.server.util.EscapeUtil;

import static com.konloch.irc.protocol.encoder.messages.IRCOpcodes.*;

/**
 * @author Konloch
 * @since 3/3/2023
 */
public class SETNICK implements ProtocolMessage
{
	public static final Object LOCK = new Object();
	
	@Override
	public void run(User user, String msgVal)
	{
		if(msgVal == null || msgVal.isEmpty())
			return;
		
		//get nick from msg value and escape invalid nick characters
		final String nick = EscapeUtil.escapeNonAlphaNumericNick(msgVal.trim());
		boolean invalidNick = false;
		
		for(IRCdUserListener listener : user.getIRC().getEvents().getUserEvents())
			if(!listener.canChangeNick(user, nick))
			{
				invalidNick = true;
				break;
			}
		
		//do not process invalid nicks
		if(nick.isEmpty()
				|| nick.length() < user.getIRC().fromConfigInt("nick.length.min")
				|| nick.length() > user.getIRC().fromConfigInt("nick.length.max"))
			invalidNick = true;
		
		//reply with invalid nick
		if(invalidNick)
		{
			user.getEncoder().newServerUserMessage()
					.opcode(ERR_ERRONEOUSNICKNAME)
					.send();
			return;
		}
		
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
			
			//set the users nick
			user.setNick(nick);
			
			//set the initial nick has been set flag
			if(!user.isFlagHasSetInitialNick())
				user.setFlagHasSetInitialNick(true);
			
			//call on the on changed event
			for(IRCdUserListener listener : user.getIRC().getEvents().getUserEvents())
				listener.onChangeNick(user, msgVal);
			
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