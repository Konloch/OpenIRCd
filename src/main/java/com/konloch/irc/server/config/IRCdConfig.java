package com.konloch.irc.server.config;

import com.konloch.dsl.DSL;

/**
 * @author Konloch
 * @since 3/3/2023
 */
public class IRCdConfig extends DSL
{
	public IRCdConfig()
	{
		super('=', '%',
				'(', ')',
				'{', '}',
				'#'
		);
	}
}
