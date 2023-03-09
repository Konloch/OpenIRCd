package com.konloch.irc.protocol.decoder;

import static com.konloch.irc.protocol.encoder.messages.IRCOpcodes.ERR_UNKNOWNCOMMAND;

import com.konloch.irc.extension.events.listeners.IRCdUserListener;
import com.konloch.irc.protocol.decoder.messages.DecodeMessage;
import com.konloch.irc.server.client.User;
import com.konloch.util.FastStringUtils;

/**
 * @author Konloch
 * @since 3/2/2023
 */
public class IRCProtocolDecoder
{
	public void decodeMessage(User user, String messages)
	{
		if(messages == null || messages.isEmpty())
			return;
		
		if(user.getIRC().isVerbose())
			System.out.println("I: " + messages);
		
		String[] msg = messages.split("\\r?\\n");
		
		for(String message : msg)
		{
			String messageIdentifier;
			String messageValue;
			
			if (message.contains(" "))
			{
				String[] blob = FastStringUtils.split(message, " ", 2);
				messageIdentifier = blob[0];
				messageValue = blob[1];
			}
			else
			{
				messageIdentifier = message;
				messageValue = null;
			}
			
			DecodeMessage decodeMessage = DecodeMessage.getLookup().get(messageIdentifier.toLowerCase());
			
			if (decodeMessage != null)
			{
				boolean cancelled = false;
				for(IRCdUserListener listener : user.getIRC().getEvents().getUserEvents())
					if(!listener.onProtocolMessageSent(user, messageValue))
						cancelled = true;
				
				if(!cancelled)
					decodeMessage.getDecodeRunnable().run(user, messageValue);
			}
			else
			{
				user.getEncoder().newServerUserMessage()
						.opcode(ERR_UNKNOWNCOMMAND)
						.message(user.getIRC().fromConfig("unknownCommand"))
						.send();
			}
		}
	}
}