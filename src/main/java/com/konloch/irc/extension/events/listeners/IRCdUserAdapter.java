package com.konloch.irc.extension.events.listeners;

import com.konloch.irc.server.client.User;

/**
 * @author Konloch
 * @since 3/3/2023
 */
public class IRCdUserAdapter implements IRCdUserListener
{
	/**
	 * Called any time a user connects
	 * @param user the user who this event belongs to
	 */
	@Override
	public void onConnect(User user)
	{
	
	}
	
	/**
	 * Called any time a user disconnects
	 * @param user the user who this event belongs to
	 */
	@Override
	public void onDisconnect(User user)
	{
	
	}
	
	/**
	 * If this returns false it will stop the message from being processed
	 *
	 * @param user the user who this event belongs to
	 * @param message the raw message being sent
	 * @return if false it will drop the message from being processed
	 */
	@Override
	public boolean onProtocolMessageSent(User user, String message)
	{
		return true;
	}
	
	/**
	 * Called whenever a user changes their nick, return false to cancel the action
	 *
	 * @param user the user who this event belongs to
	 * @param nick the nick sent to be changed to
	 * @return false to cancel the action
	 */
	@Override
	public boolean onChangeNick(User user, String nick)
	{
		return true;
	}
	
	/**
	 * Called when the initial user message is sent, return false to cancel the action
	 *
	 * @param user the user who this event belongs to
	 * @param userBlob the data sent in the initial message
	 * @return false to cancel the action
	 */
	@Override
	public boolean onSetUser(User user, String userBlob)
	{
		return true;
	}
	
	/**
	 * Called whenever a new channel is created by a new user joining it for the first time, return false to cancel the action
	 *
	 * @param user the user who this event belongs to
	 * @param channel the channel the user is requesting to join
	 * @return false to cancel the action
	 */
	@Override
	public boolean onCreateChannel(User user, String channel)
	{
		return true;
	}
	
	/**
	 * Called whenever a user joins a channel, return false to cancel the action
	 *
	 * @param user the user who this event belongs to
	 * @param channel the channel the user is requesting to join
	 * @return false to cancel the action
	 */
	@Override
	public boolean onJoinChannel(User user, String channel)
	{
		return true;
	}
	
	/**
	 * Called whenever a user leaves a channel
	 *
	 * @param user the user who this event belongs to
	 * @param channel the channel the user is leaving
	 */
	@Override
	public void onLeaveChannel(User user, String channel)
	{
	
	}
	
	/**
	 * Called whenever a user sends a privmsg, return false to cancel the action
	 *
	 * @param user the user who this event belongs to
	 * @param channel the channel the user is requesting to join
	 * @param message the message being sent
	 * @return false to cancel the action
	 */
	@Override
	public boolean onChannelMessage(User user, String channel, String message)
	{
		return true;
	}
}
