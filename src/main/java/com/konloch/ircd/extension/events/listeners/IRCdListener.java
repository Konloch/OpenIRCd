package com.konloch.ircd.extension.events.listeners;

/**
 * @author Konloch
 * @since 3/3/2023
 */
public interface IRCdListener
{
	void onIRCBoot();
	void onIRCStop();
}
