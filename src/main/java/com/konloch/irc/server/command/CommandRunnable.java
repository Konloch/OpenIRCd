package com.konloch.irc.server.command;

/**
 * @author Konloch
 * @since 3/17/2023
 */
public interface CommandRunnable
{
	void run(CommandInstance instance);
}
