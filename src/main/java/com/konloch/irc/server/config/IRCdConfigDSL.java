package com.konloch.irc.server.config;

import com.konloch.dsl.DSL;

/**
 * @author Konloch
 * @since 3/3/2023
 */
public class IRCdConfigDSL extends DSL
{
	public IRCdConfigDSL()
	{
		super('=', '%',
				'(', ')',
				'{', '}',
				'#'
		);
	}
}
