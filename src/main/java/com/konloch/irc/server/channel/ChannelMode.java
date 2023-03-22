package com.konloch.irc.server.channel;

/**
 * @author Konloch
 * @since 3/21/2023
 */
public enum ChannelMode
{
	CHANNEL_BAN("b", "Takes a mask as a parameter. Users matching the mask are prevented from joining or speaking."),
	CHANNEL_BAN_EXEMPTION("e", "Takes a mask as a parameter. Ban exemption matches override +b and +q bans for all clients it matches."),
	BLOCK_CTCP("C", "Blocks CTCP commands (other than /me actions)."),
	FORWARD("f", "Takes a channel name as a parameter. Users who cannot join the channel (because of +i, +j, +l, +S, +r, see below) are instead sent to the given channel. Clients are notified when the forward takes effect."),
	ENABLE_FORWARDING("F", "Allow operators in other channels to forward clients to this channel, without requiring ops in the target channel."),
	FREE_INVITE("g", "Anybody in the channel may invite others (using the /invite command) to the channel."),
	INVITE_ONLY("i", "Users are unable to join invite-only channels unless they are invited or match a +I entry."),
	INVITE_EXEMPTION("I", "Takes a mask parameter. Matching clients do not need to be invited to join the channel when it is invite-only (+i) or blocking unidentified users (+r)."),
	JOIN_THROTTLE("j", "This mode takes one parameter of the form n:t, where n and t are positive integers. Only n users may join in each period of t seconds, so with e.g. 3:10 only 3 users could join within 10 seconds."),
	CHANNEL_PASSWORD("k", "To enter the channel, you must specify the password on your /join command."),
	JOIN_LIMIT("l", "Takes a positive integer parameter. Limits the number of users who can be in the channel at the same time."),
	MODERATED("m", "Only opped and voiced users can send to the channel. This mode does not prevent users from changing nicks."),
	PREVENT_EXTERNAL_SEND("n", "Users outside the channel may not send messages to it."),
	PRIVATE("p", "The KNOCK command cannot be used on the channel, and users will not be shown the channel in whois."),
	QUIET("q", "Takes a mask parameter. Works like +b (channel ban), but allows matching users to join the channel."),
	BLOCK_FORWARDED_USERS("Q", "Users cannot be forwarded (see +f above) to a channel with +Q."),
	BLOCK_UNIDENTIFIED("r", "Prevents users who are not identified to services from joining the channel."),
	SILENCE_UNIDENTIFIED("R", "Prevents users who are not identified to services from sending messages to the channel."),
	SECRET("s", "This channel will not appear on channel lists or WHO or WHOIS output unless you are on it."),
	TLS_ONLY("S", "Only users connected via TLS may join the channel while this mode is set"),
	OPS_TOPIC("t", "Only channel operators may set the channel topic."),
	BLOCK_NOTICE("T", "Blocks channel notices (other than CTCP replies, see +C)."),
	
	//Restricted channel modes
	//the following channel modes can only be added by admins
	PERMANENT("P", "Channel does not disappear when empty."),
	;
	
	private final String mode;
	private final String description;
	private final boolean restricted;
	
	ChannelMode(String mode, String description)
	{
		this(mode, description, false);
	}
	
	ChannelMode(String mode, String description, boolean restricted)
	{
		this.mode = mode;
		this.description = description;
		this.restricted = restricted;
	}
	
	public String getMode()
	{
		return mode;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public boolean isRestricted()
	{
		return restricted;
	}
}
