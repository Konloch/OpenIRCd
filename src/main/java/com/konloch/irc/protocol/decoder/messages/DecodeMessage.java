package com.konloch.irc.protocol.decoder.messages;

import java.util.HashMap;

import com.konloch.irc.protocol.ProtocolMessage;
import com.konloch.irc.protocol.decoder.messages.impl.*;

/**
 * @author Konloch
 * @since 3/2/2023
 */
public enum DecodeMessage
{
	PING("PING", "https://www.rfc-editor.org/rfc/rfc2812#section-3.7.2", new PING()),
	PONG("PONG", "https://www.rfc-editor.org/rfc/rfc2812#section-3.7.3", new PONG()),
	NICK("NICK", "https://www.rfc-editor.org/rfc/rfc1459#section-4.1.2", new SETNICK()),
	USER("USER", "https://www.rfc-editor.org/rfc/rfc1459#section-4.1.3", new SETUSER()),
	JOIN("JOIN", "https://www.rfc-editor.org/rfc/rfc1459#section-4.2.1", new JOIN()),
	NAMES("NAMES", "https://www.rfc-editor.org/rfc/rfc1459#section-4.2.5", new NAMES()),
	PRIVMSG("PRIVMSG", "https://www.rfc-editor.org/rfc/rfc1459#section-4.4.1", new PRIVMSG()),
	MOTD("MOTD", "https://dd.ircdocs.horse/refs/commands/motd", new MOTD()),
	CAP("CAP", "https://ircv3.net/specs/extensions/capability-negotiation.html", new CAP()),
	;
	
	private static final HashMap<String, DecodeMessage> lookup = new HashMap<>();
	
	static
	{
		for(DecodeMessage message : values())
			lookup.put(message.getIdentifier().toLowerCase(), message);
	}
	
	private final String identifier;
	private final String documentation;
	private final ProtocolMessage decodeRunnable;
	
	DecodeMessage(String messageIdentifier, String messageDocumentation, ProtocolMessage decodeRunnable)
	{
		this.identifier = messageIdentifier;
		this.documentation = messageDocumentation;
		this.decodeRunnable = decodeRunnable;
	}
	
	public String getIdentifier()
	{
		return identifier;
	}
	
	public String getDocumentation()
	{
		return documentation;
	}
	
	public ProtocolMessage getDecodeRunnable()
	{
		return decodeRunnable;
	}
	
	public static HashMap<String, DecodeMessage> getLookup()
	{
		return lookup;
	}
}
