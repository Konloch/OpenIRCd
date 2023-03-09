package com.konloch.irc.protocol;

import com.konloch.irc.server.client.User;

/**
 * @author Konloch
 * @since 3/2/2023
 */
public interface ProtocolMessage
{
	void run(User user, String msgVal);
}
