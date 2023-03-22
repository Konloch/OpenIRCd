package com.konloch.irc.extension.cli;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.Plugin;
import com.konloch.irc.server.util.cli.Command;
import com.konloch.irc.server.util.cli.CommandArgument;

/**
 * @author Konloch
 * @since 3/18/2023
 */
public class HelpCommand implements Plugin
{
	@Override
	public void install(OpenIRCd irc)
	{
		//help command
		irc.getCLI().register(new Command("help", irc.fromConfig("command.help.description"), command->
		{
			if(command.getArguments().length == 0)
			{
				System.out.println(irc.fromConfig("command.help.specific.command") + " `help <command>`");
				for (Command c : irc.getCLI().getCommands())
					System.out.println("+ " + c);
			}
			else
			{
				String cmd = command.getArguments()[0];
				
				boolean listedHeader = false;
				for(Command c : irc.getCLI().getCommands())
				{
					if(c.getChain().getName().equalsIgnoreCase(cmd))
					{
						if(!listedHeader)
						{
							System.out.println(irc.fromConfig("command.help.for") + " `" + c.getChain().getName() + "`:");
							System.out.println(c.getChain().getDescription());
							System.out.println();
							listedHeader = true;
						}
						
						System.out.println(c);
						
						CommandArgument argument = c.getChain().getChild();
						while(argument != null)
						{
							if(argument.isOptional())
								System.out.println("<*" + argument.getName() + "> - " + argument.getDescription() + " (* = " + irc.fromConfig("command.help.optional.command") + ")");
							else
								System.out.println("<" + argument.getName() + "> - " + argument.getDescription());
							
							argument = argument.getChild();
						}
						
						System.out.println();
					}
				}
				
				if(!listedHeader)
					System.out.println(irc.fromConfig("command.incorrect.usage"));
			}
		}).addOptionalArgument("command", irc.fromConfig("command.help.command.description")).get());
	}
}
