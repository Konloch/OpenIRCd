package com.konloch.irc.server.util.cli;

/**
 * @author Konloch
 * @since 3/17/2023
 */
public class Command
{
	private final CommandArgument chain;
	private final CommandRunnable run;
	
	public Command(String name, String description, CommandRunnable run)
	{
		this.run = run;
		this.chain = new CommandArgument(this, name, description, false);
	}
	
	public CommandArgument getChain()
	{
		return chain;
	}
	
	public CommandArgument addArgument(String name, String description)
	{
		CommandArgument argument = new CommandArgument(this, name, description, false);
		chain.setChild(argument);
		return argument;
	}
	
	public CommandArgument addOptionalArgument(String name, String description)
	{
		CommandArgument argument = new CommandArgument(this, name, description, true);
		chain.setChild(argument);
		return argument;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		CommandArgument argument = chain;
		while(true)
		{
			if(argument == null)
				break;
			
			if(sb.length() != 0)
			{
				sb.append("\t");
				sb.append("<");
				if(argument.isOptional())
					sb.append("*");
				sb.append(argument.getName());
				sb.append(">");
			}
			else
				sb.append(argument.getName());
			
			argument = argument.getChild();
		}
		
		return sb.toString();
	}
	
	public CommandRunnable getRun()
	{
		return run;
	}
}
