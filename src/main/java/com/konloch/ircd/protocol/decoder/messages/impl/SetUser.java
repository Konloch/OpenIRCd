package com.konloch.ircd.protocol.decoder.messages.impl;

import com.konloch.ircd.server.client.User;
import com.konloch.ircd.protocol.decoder.messages.DecodeMessage;
import com.konloch.ircd.protocol.ProtocolMessage;
import com.konloch.ircd.extension.events.listeners.IRCdUserListener;
import com.konloch.util.FastStringUtils;

import static com.konloch.ircd.protocol.encoder.messages.IRCOpcodes.*;

/**
 * @author Konloch
 * @since 3/3/2023
 */
public class SetUser implements ProtocolMessage
{
	@Override
	public void run(User user, String msgVal)
	{
		if(msgVal == null || msgVal.isEmpty())
			return;
		
		if(!user.isFlagHasSetInitialNick())
			return;
		
		if(user.isFlagHasSetInitialUser())
			return;
		
		for(IRCdUserListener listener : user.getIRC().getEvents().getUserEvents())
			if(!listener.onSetUser(user, msgVal))
				return;
		
		String[] blob = FastStringUtils.split(msgVal, " ", 4);
		
		if(blob.length != 4)
		{
			//TODO send not enough arguments
			return;
		}
		
		final String userName = blob[0];
		final String realName = blob[3].substring(1);
		
		user.setUser(userName);
		user.setRealName(realName);
		
		user.setFlagHasSetInitialUser(true);
		
		//=================
		//= Welcome burst =
		//=================
		
		//send the welcome message
		user.getEncoder().newServerUserMessage()
				.opcode(RPL_WELCOME)
				.message(user.getIRC().getWelcomeMessage())
				.send();
		
		//send the host message
		user.getEncoder().newServerUserMessage()
				.opcode(RPL_YOURHOST)
				.message("Your host is " + user.getIRC().getHost() + ", running version " + user.getIRC().getIRCdVersionString())
				.send();
		
		//send the creation date message
		user.getEncoder().newServerUserMessage()
				.opcode(RPL_CREATED)
				.message("This server was created " + user.getIRC().getCreated())
				.send();
		
		//send the info message
		user.getEncoder().newServerUserMessage()
				.opcode(RPL_INFO)
				.message(user.getIRC().getHost() + " " + user.getIRC().getIRCdVersion() + " " + user.getIRC().fromConfig("userModes") + " " + user.getIRC().fromConfig("channelModes"))
				.send();
		
		//send the MOTD
		DecodeMessage.MOTD.getDecodeRunnable().run(user, "");
		
		//TODO user modes
		//user.getEncoder().sendRaw(":" + user.getNick() + " MODE " + user.getNick() + " :+iw\n");
	}
}
