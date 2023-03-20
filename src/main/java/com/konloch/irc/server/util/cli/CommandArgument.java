package com.konloch.irc.server.util.cli;

/**
 * @author Konloch
 * @since 3/17/2023
 */
public class CommandArgument
{
	private final Command parent;
	private final String name;
	private final String description;
	private final boolean optional;
	private CommandArgument child;
	
	public CommandArgument(Command parent, String name, String description, boolean optional)
	{
		this(parent, name, description, null, optional);
	}
	
	public CommandArgument(Command parent, String name, String description, CommandArgument child, boolean optional)
	{
		this.parent = parent;
		this.name = name;
		this.description = description;
		this.child = child;
		this.optional = optional;
	}
	
	public CommandArgument addArgument(String name, String description)
	{
		CommandArgument argument = new CommandArgument(parent, name, description, optional);
		setChild(argument);
		return argument;
	}
	
	public Command get()
	{
		return parent;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public CommandArgument getChild()
	{
		return child;
	}
	
	public boolean isOptional()
	{
		return optional;
	}
	
	public CommandArgument setChild(CommandArgument child)
	{
		this.child = child;
		return this;
	}
}
