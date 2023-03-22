package com.konloch.irc.extension.plugins;

import java.util.HashMap;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.Plugin;
import com.konloch.irc.extension.events.listeners.IRCdUserAdapter;
import com.konloch.irc.server.channel.Channel;
import com.konloch.irc.server.client.User;

/**
 * OpenIRCd Spam Filter
 *      + Max messages per user per minute
 *      + Max users per channel
 *      + Max channels joined per user
 *      + Max channels created per user per X milliseconds
 *      + Max channels simultaneously active / created
 *
 * @author Konloch
 * @since 3/5/2023
 */
public class ResourceLimiter implements Plugin
{
	private final HashMap<Long, RLUserData> limiterUserData = new HashMap<>();
	
	@Override
	public void install(OpenIRCd irc)
	{
		final int maxChannels = irc.fromConfigInt("channel.limit");
		final int maxChannelUsers = irc.fromConfigInt("channel.user.limit");
		final int maxChannelsPerUser = irc.fromConfigInt("user.channel.connection.limit");
		final long channelCreationTimeLimit = irc.fromConfigInt("user.channel.creation.time.limit");
		final int maxMessagesPerUserPerMinute = irc.fromConfigInt("user.mpm.limit");
		
		irc.getEvents().getUserEvents().add(new IRCdUserAdapter()
		{
			@Override
			public void onConnect(User user)
			{
				limiterUserData.put(user.getClient().getUID(), new RLUserData());
			}
			
			@Override
			public void onDisconnect(User user)
			{
				limiterUserData.remove(user.getClient().getUID());
			}
			
			@Override
			public boolean onJoinChannel(User user, String channel)
			{
				Channel chan = irc.getChannels().get(channel);
				
				if(chan != null)
				{
					//limit max users per channel
					if(chan.getUsers().size() >= maxChannelUsers)
						return false;
				}
				
				//limit max joined channels per user
				return user.getChannels().size() <= maxChannelsPerUser;
			}
			
			@Override
			public boolean onCreateChannel(User user, String channel)
			{
				final RLUserData userData = limiterUserData.get(user.getClient().getUID());
				
				boolean creatingTooFast = System.currentTimeMillis()-userData.lastChannelCreation >= channelCreationTimeLimit;
				
				if(!creatingTooFast)
					userData.lastChannelCreation = System.currentTimeMillis();
				
				//limit max channels
				if(user.getIRC().getChannels().size() > maxChannels)
					return false;
				
				return creatingTooFast;
			}
			
			@Override
			public boolean onChannelMessage(User user, String channel, String message)
			{
				final RLUserData userData = limiterUserData.get(user.getClient().getUID());
				
				userData.messageCount += 1;
				
				if(System.currentTimeMillis()- userData.messageLogCounter >= 60 * 1000)
					userData.reset();
				
				return userData.messageCount <= maxMessagesPerUserPerMinute;
			}
		});
	}
	
	public static class RLUserData
	{
		private long messageCount = 0;
		private long messageLogCounter = System.currentTimeMillis();
		private long lastChannelCreation;
		
		public void reset()
		{
			messageCount = 0;
			messageLogCounter = System.currentTimeMillis();
		}
	}
}