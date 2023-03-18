package com.konloch.irc.extension.cli;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.server.command.Command;
import com.konloch.irc.server.command.CommandArgument;

/**
 * @author Konloch
 * @since 3/17/2023
 */
public class IRCdCLI
{
	public void load(OpenIRCd irc)
	{
		//op
		irc.getCLI().register(new Command("op", irc.fromConfig("command.op.description"), command->{
		
		}).addArgument("add/remove", irc.fromConfig("command.op.add.description"))
				.addArgument("nick", irc.fromConfig("command.op.add.nick.description"))
				.get());
		
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
				
				for(Command c : irc.getCLI().getCommands())
				{
					if(c.getChain().getName().equalsIgnoreCase(cmd))
					{
						System.out.println(irc.fromConfig("command.help.for") + " `" + c.getChain().getName() + "`:");
						System.out.println(c.getChain().getDescription());
						System.out.println();
						System.out.println(c);
						
						CommandArgument argument = c.getChain().getChild();
						while(true)
						{
							if(argument == null)
								break;
							
							if(argument.isOptional())
								System.out.println("<*" + argument.getName() + "> - " + argument.getDescription() + " (* = " + irc.fromConfig("command.help.optional.command") + ")");
							else
								System.out.println("<" + argument.getName() + "> - " + argument.getDescription());
							
							argument = argument.getChild();
						}
						System.out.println();
						return;
					}
				}
				
				System.out.println(irc.fromConfig("command.help.could.not.find") + ": `" + cmd + "`");
			}
		}).addOptionalArgument("command", irc.fromConfig("command.help.command.description")).get());
	}
}
