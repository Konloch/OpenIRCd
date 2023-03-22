package com.konloch.irc.extension.events.listeners;

import com.konloch.irc.server.client.User;

/**
 * @author Konloch
 * @since 3/3/2023
 */
public interface IRCdUserListener
{
	/**
	 * Called any time a user connects
	 * @param user the user who this event belongs to
	 */
	void onConnect(User user);
	
	/**
	 * Called any time a user disconnects
	 * @param user the user who this event belongs to
	 */
	void onDisconnect(User user);
	
	/**
	 * If this returns false it will stop the message from being processed
	 *
	 * @param user the user who this event belongs to
	 * @param message the raw message being sent
	 * @return if false it will drop the message from being processed
	 */
	boolean onProtocolMessageSent(User user, String message);
	
	/**
	 * Called whenever a user changes their nick, before the action is processed, return false to cancel the action
	 *
	 * @param user the user who this event belongs to
	 * @param nick the nick sent to be changed to
	 * @return false to cancel the action
	 */
	boolean canChangeNick(User user, String nick);
	
	/**
	 * Called whenever a user changes their nick
	 *
	 * @param user the user who this event belongs to
	 * @param nick the nick sent to be changed to
	 */
	void onChangeNick(User user, String nick);
	
	/**
	 * Called when the initial user message is sent, return false to cancel the action
	 *
	 * @param user the user who this event belongs to
	 * @param userBlob the data sent in the initial message
	 * @return false to cancel the action
	 */
	boolean onSetUser(User user, String userBlob);
	
	/**
	 * Called whenever a new channel is created by a new user joining it for the first time, return false to cancel the action
	 *
	 * @param user the user who this event belongs to
	 * @param channel the channel the user is requesting to join
	 * @return false to cancel the action
	 */
	boolean onCreateChannel(User user, String channel);
	
	/**
	 * Called whenever a user joins a channel, return false to cancel the action
	 *
	 * @param user the user who this event belongs to
	 * @param channel the channel the user is requesting to join
	 * @return false to cancel the action
	 */
	boolean onJoinChannel(User user, String channel);
	
	/**
	 * Called whenever a user leaves a channel
	 *
	 * @param user the user who this event belongs to
	 * @param channel the channel the user is leaving
	 */
	void onLeaveChannel(User user, String channel);
	
	/**
	 * Called whenever a user sends a privmsg, return false to cancel the action
	 *
	 * @param user the user who this event belongs to
	 * @param channel the channel the user is requesting to join
	 * @param message the message being sent
	 * @return false to cancel the action
	 */
	boolean onChannelMessage(User user, String channel, String message);
}
