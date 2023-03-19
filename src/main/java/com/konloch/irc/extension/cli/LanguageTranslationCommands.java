package com.konloch.irc.extension.cli;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.Plugin;
import com.konloch.irc.server.command.Command;
import com.konloch.irc.server.translation.Language;
import com.konloch.irc.server.util.ReadResource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Konloch
 * @since 3/18/2023
 */
public class LanguageTranslationCommands implements Plugin
{
	
	@Override
	public void install(OpenIRCd irc)
	{
		//translation list
		irc.getCLI().register(new Command("lang", irc.fromConfig("command.translation.description"), command->{
			if(command.getArguments().length == 2 && command.getArguments()[0].equals("set")) {} //handled below
			else if(command.getArguments().length == 1 && command.getArguments()[0].equals("list"))
			{
				System.out.println("There are " + Language.values().length + " translations:");
				for(Language language : Language.values())
					System.out.println(" + " + language.name().toLowerCase() + " - " + language.getReadableName());
			}
			else
			{
				System.out.println("Incorrect command usage");
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
	}
}
