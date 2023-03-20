package com.konloch.irc.server.data.config;

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
