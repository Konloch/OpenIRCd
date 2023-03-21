package com.konloch;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.server.channel.Channel;
import com.konloch.irc.server.client.data.UserData;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author Konloch
 * @since 3/20/2023
 */
public class ViewSerializedDBs
{
	public static void main(String[] args) throws IOException, URISyntaxException
	{
		OpenIRCd irc = new OpenIRCd(new File("./config.ini"));
		
		System.out.println("Registered Nicks: " + irc.getDB().getRegisteredUsers().size());
		for(String nick : irc.getDB().getRegisteredUsers().keySet())
		{
			UserData data = irc.getDB().getRegisteredUsers().get(nick);
			
			System.out.println("\t+ " + nick + " - " + data.getUsergroup() + " - " + data.getEmail());
		}
		
		System.out.println();
		System.out.println("Registered Channels: " + irc.getDB().getChannels().size());
		for(String channel : irc.getDB().getChannels().keySet())
		{
			Channel data = irc.getDB().getChannels().get(channel);
			
			System.out.println("\t+ " + channel + " - " + data.getDescription());
		}
	}
}
