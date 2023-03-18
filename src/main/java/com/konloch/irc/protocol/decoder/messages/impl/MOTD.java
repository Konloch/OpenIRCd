package com.konloch.irc.protocol.decoder.messages.impl;

import com.konloch.disklib.DiskReader;
import com.konloch.irc.protocol.ProtocolMessage;
import com.konloch.irc.server.client.User;

import static com.konloch.irc.protocol.encoder.messages.IRCOpcodes.*;

import java.io.File;
import java.io.IOException;

/**
 * @author Konloch
 * @since 3/5/2023
 */
public class MOTD implements ProtocolMessage
{
	@Override
	public void run(User user, String msgVal)
	{
		File MOTD = user.getIRC().getMOTDFile();
		if(MOTD.exists())
		{
			user.getEncoder().newServerUserMessage()
					.opcode(RPL_ENDOFMOTD)
					.message(user.getIRC().fromConfig("MOTD.start"))
					.send();
			
			try
			{
				String[] MOTDLines = DiskReader.readArray(MOTD);
				
				for(String MOTDLine : MOTDLines)
				{
					user.getEncoder().newServerUserMessage()
							.opcode(RPL_MOTD)
							.message(MOTDLine)
							.send();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			user.getEncoder().newServerUserMessage()
					.opcode(RPL_ENDOFMOTD)
					.message(user.getIRC().fromConfig("MOTD.end"))
					.send();
		}
		else
		{
			user.getEncoder().newServerUserMessage()
					.opcode(ERR_NOMOTD)
					.send();
		}
	}
}
