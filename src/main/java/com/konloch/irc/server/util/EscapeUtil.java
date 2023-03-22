package com.konloch.irc.server.util;

import java.util.regex.Pattern;

/**
 * @author Konloch
 * @since 3/22/2023
 */
public class EscapeUtil
{
	public static Pattern ALPHA_NUMERIC = Pattern.compile("[^A-Za-z0-9]");
	public static Pattern ALPHA_NUMERIC_NICK = Pattern.compile("[^A-Za-z0-9 \\-_\\[\\]{}\\\\|]");
	public static Pattern ALPHA_NUMERIC_CHANNEL = Pattern.compile("[^A-Za-z0-9#]");
	public static Pattern ASCII = Pattern.compile("[^A-Za-z0-9\\[\\]\\\\=+\\-()_;:,.'\" @!?#$%^&*<>/|]");
	
	public static String escapeNonAlphaNumeric(String input)
	{
		return ALPHA_NUMERIC.matcher(input).replaceAll("");
	}
	
	public static String escapeNonAlphaNumericNick(String input)
	{
		return ALPHA_NUMERIC_NICK.matcher(input).replaceAll("");
	}
	
	public static String escapeNonAlphaNumericChannel(String input)
	{
		return ALPHA_NUMERIC_CHANNEL.matcher(input).replaceAll("");
	}
	
	public static String escapeNonASCII(String input)
	{
		return ASCII.matcher(input).replaceAll("");
	}
}