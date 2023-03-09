package com.konloch.irc.protocol.encoder.messages;

import java.io.IOException;

import com.konloch.irc.server.client.User;

/**
 * @author Konloch
 * @since 3/2/2023
 */
public interface EncodeMessageRunnable
{
	void run(User user) throws IOException;
}
