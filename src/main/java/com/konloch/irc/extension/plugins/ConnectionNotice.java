package com.konloch.irc.extension.plugins;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.Plugin;
import com.konloch.irc.extension.events.listeners.IRCdUserAdapter;
import com.konloch.irc.server.client.User;

/**
 * @author Konloch
 * @since 3/17/2023
 */
public class ConnectionNotice implements Plugin
{
	@Override
	public void install(OpenIRCd irc)
	{
		//alert hostname look-ups are disabled
		irc.getEvents().getUserEvents().add(new IRCdUserAdapter()
		{
			@Override
			public void onConnect(User user)
			{
				user.getEncoder().sendNotice("** This server is running an experimental IRCd");
				user.getEncoder().sendNotice("** Expect & report bugs / lack of functionality");
				user.getEncoder().sendNotice("** Hostname look-ups are not enabled");
			}
		});
	}
}