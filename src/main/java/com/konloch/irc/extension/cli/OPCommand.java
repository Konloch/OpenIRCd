package com.konloch.irc.extension.cli;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.Plugin;
import com.konloch.irc.server.command.Command;

/**
 * @author Konloch
 * @since 3/18/2023
 */
public class OPCommand implements Plugin
{
	@Override
	public void install(OpenIRCd irc)
	{
		//op
		irc.getCLI().register(new Command("op", irc.fromConfig("command.op.description"), command->{
			if(command.getArguments().length > 0 && command.getArguments()[0].equals("add"))
			{
			
			}
			else if(command.getArguments().length > 0 && command.getArguments()[0].equals("add"))
			{
			
			}
			else
			{
				System.out.println("Incorrect command usage");
			}
		}).addArgument("add/remove", irc.fromConfig("command.op.add.description"))
				.addArgument("nick", irc.fromConfig("command.op.add.nick.description"))
				.get());
	}
}
