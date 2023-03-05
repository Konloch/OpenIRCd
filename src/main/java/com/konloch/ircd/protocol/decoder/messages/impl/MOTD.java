package com.konloch.ircd.protocol.decoder.messages.impl;

import com.konloch.disklib.DiskReader;
import com.konloch.ircd.server.client.User;
import com.konloch.ircd.protocol.ProtocolMessage;

import java.io.File;
import java.io.IOException;

import static com.konloch.ircd.protocol.encoder.messages.IRCOpcodes.*;

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
					.message(user.getIRC().fromConfig("MOTDStart"))
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
					.message(user.getIRC().fromConfig("MOTDEnd"))
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
