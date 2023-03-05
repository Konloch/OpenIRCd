package com.konloch.ircd.extension;

import com.konloch.ircd.IRCd;

/**
 * @author Konloch
 * @since 3/5/2023
 */
public interface Plugin
{
	void install(IRCd irc);
}
