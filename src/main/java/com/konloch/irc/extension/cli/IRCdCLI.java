package com.konloch.irc.extension.cli;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.server.command.Command;
import com.konloch.irc.server.command.CommandArgument;
import com.konloch.irc.server.translation.Language;
import com.konloch.irc.server.util.ReadResource;

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

		//translation list
		irc.getCLI().register(new Command("lang", irc.fromConfig("command.translation.description"), command->{
			if(command.getArguments().length == 1 && command.getArguments()[0].equals("list"))
			{
				System.out.println("There are " + Language.values().length + " translations:");
				for(Language language : Language.values())
					System.out.println(" + " + language.name().toLowerCase() + " - " + language.getReadableName());
			}
		}).addArgument("list", irc.fromConfig("command.command.list.description"))
				.get());

		//translation select
		irc.getCLI().register(new Command("lang", irc.fromConfig("command.translation.description"), command->{
			if(command.getArguments().length == 2 && command.getArguments()[0].equals("set"))
			{
				String translation = command.getArguments()[1];

				try
				{
					Language.valueOf(translation.toUpperCase());
				}
				catch(IllegalArgumentException e)
				{
					System.out.println("Language `" + translation + "` is not found, type `lang list` for the supported list");
					return;
				}

				irc.getConfigParser().parse(new ArrayList<>(Arrays.asList(new String(ReadResource.read("/translations/" + translation + ".ini"), StandardCharsets.UTF_8).split("\\r?\\n"))));
				System.out.println("Language set to `" + translation + "`");
			}
		}).addArgument("set", irc.fromConfig("command.translation.set.description"))
				.addArgument("translation", irc.fromConfig("command.translation.set.translation.description"))
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
					}
				}
				
				System.out.println(irc.fromConfig("command.help.could.not.find") + ": `" + cmd + "`");
			}
		}).addOptionalArgument("command", irc.fromConfig("command.help.command.description")).get());
	}
}
