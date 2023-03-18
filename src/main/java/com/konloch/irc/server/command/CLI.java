package com.konloch.irc.server.command;

import java.util.ArrayList;

/**
 * Acts as the CLI for the IRCd
 *
 * @author Konloch
 * @since 3/17/2023
 */
public class CLI
{
	private final ArrayList<Command> commands = new ArrayList<>();
	
	public void load()
	{
		//op
		register(new Command("op", "Give or remove operator privileges on a specific user's nick", command->{
		
		}).addArgument("add/remove", "Add or remove op").addArgument("nick", "User nick").get());
		
		//help command
		register(new Command("help", "List all commands", command->
		{
			if(command.getArguments().length == 0)
			{
				System.out.println("To get help for a specific command type `help <command>`");
				for (Command c : commands)
					System.out.println("+ " + c);
			}
			else
			{
				String cmd = command.getArguments()[0];
				
				for(Command c : commands)
				{
					if(c.getChain().getName().equalsIgnoreCase(cmd))
					{
						System.out.println("Help for `" + c.getChain().getName() + "`:");
						System.out.println(c.getChain().getDescription());
						System.out.println();
						System.out.println(c);
						
						CommandArgument argument = c.getChain().getChild();
						while(true)
						{
							if(argument == null)
								break;
							
							if(argument.isOptional())
								System.out.println("<*" + argument.getName() + "> - " + argument.getDescription() + " (* = optional command)");
							else
								System.out.println("<" + argument.getName() + "> - " + argument.getDescription());
							
							argument = argument.getChild();
						}
						System.out.println();
						return;
					}
				}
				
				System.out.println("Could not find command: `" + cmd + "`");
			}
		}).addOptionalArgument("command", "Get help for a specific command").get());
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
				break;
			}
	}
}