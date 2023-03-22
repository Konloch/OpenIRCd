package com.konloch.irc.extension.cli;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.Plugin;
import com.konloch.irc.server.channel.Channel;
import com.konloch.irc.server.util.cli.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Konloch
 * @since 3/18/2023
 */
public class ListChannels implements Plugin
{
	@Override
	public void install(OpenIRCd irc)
	{
		//list channels command
		irc.getCLI().register(new Command("list", irc.fromConfig("command.list.description"), command->
		{
			
			ArrayList<Channel> channels = new ArrayList<>(irc.getDB().getChannels().values());
			
			channels.sort(Comparator.comparingInt(o -> o.getUsers().size()));
			
			for(Channel channel : channels)
			{
				StringBuilder sb = new StringBuilder("\t+ ");
				
				if(channel.isPermanent())
					sb.append("[PERMANENT] ");
				
				sb.append(channel.getName()).append(" - ");
				sb.append(channel.getUsers().size()).append(" connected - ");
				sb.append(Arrays.toString(channel.getModes().toArray()));
				
				System.out.println(sb);
			}
		}));
	}
}
