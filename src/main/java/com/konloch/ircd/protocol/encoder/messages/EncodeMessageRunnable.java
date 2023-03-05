package com.konloch.ircd.protocol.encoder.messages;

import com.konloch.ircd.server.client.User;

import java.io.IOException;

/**
 * @author Konloch
 * @since 3/2/2023
 */
public interface EncodeMessageRunnable
{
	void run(User user) throws IOException;
}
