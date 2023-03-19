package com.konloch.irc.server.command;

import java.util.ArrayList;

import com.konloch.irc.OpenIRCd;

/**
 * Acts as the CLI for the IRCd
 *
 * @author Konloch
 * @since 3/17/2023
 */
public class CLI
{
	private final OpenIRCd irc;
	private final ArrayList<Command> commands = new ArrayList<>();
	
	public CLI(OpenIRCd irc)
	{
		this.irc = irc;
	}
	
	public CLI register(Command command)
	{
		commands.add(command);
		commands.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getChain().getName(), o2.getChain().getName()));
		return this;
	}
	
	public void execute(String[] commandWithArgs)
	{
		if(commandWithArgs == null || commandWithArgs.length == 0)
			return;
		
		boolean found = false;
		for(Command command : commands)
			if(command.getChain().getName().equalsIgnoreCase(commandWithArgs[0]))
			{
				String[] args = new String[0];
				if(commandWithArgs.length >= 2)
				{
					args = new String[commandWithArgs.length-1];
					System.arraycopy(commandWithArgs, 1, args, 0, args.length);
				}
				
				command.getRun().run(new CommandInstance(command.getChain().getName(), args));
				found = true;
			}

		if(!found)
		{
			System.out.println(irc.fromConfig("command.help.could.not.find") + ": `" + commandWithArgs[0] + "`");
		}
	}
	
	public ArrayList<Command> getCommands()
	{
		return commands;
	}
}