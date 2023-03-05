package com.konloch.ircd.protocol;

import com.konloch.ircd.server.client.User;

/**
 * @author Konloch
 * @since 3/2/2023
 */
public interface ProtocolMessage
{
	void run(User user, String msgVal);
}
