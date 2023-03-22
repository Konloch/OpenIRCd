package com.konloch.irc.extension.cli;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.Plugin;
import com.konloch.irc.server.util.cli.Command;

/**
 * @author Konloch
 * @since 3/18/2023
 */
public class ListChannels implements Plugin
{
	@Override
	public void install(OpenIRCd irc)
	{
		//help command
		irc.getCLI().register(new Command("list", "List all channels", command->
		{
		
		}));
	}
}
