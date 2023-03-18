package com.konloch.irc.server.command;

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
}
