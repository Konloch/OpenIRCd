package com.konloch.irc.server.util.cli;

/**
 * @author Konloch
 * @since 3/17/2023
 */
public class CommandInstance
{
	private final String name;
	private final String[] arguments;
	
	public CommandInstance(String name, String[] arguments)
	{
		this.name = name;
		this.arguments = arguments;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String[] getArguments()
	{
		return arguments;
	}
	
	public String asString(int fromIndex)
	{
		StringBuilder sb = new StringBuilder();
		for(int i = fromIndex; i < getArguments().length; i++)
		{
			if(i >= fromIndex + 1)
				sb.append(" ");
			sb.append(getArguments()[i]);
		}
		
		return sb.toString();
	}
}
