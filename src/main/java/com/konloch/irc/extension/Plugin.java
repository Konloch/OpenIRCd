package com.konloch.irc.extension;

import com.konloch.irc.OpenIRCd;

/**
 * @author Konloch
 * @since 3/5/2023
 */
public interface Plugin
{
	void install(OpenIRCd irc);
}
