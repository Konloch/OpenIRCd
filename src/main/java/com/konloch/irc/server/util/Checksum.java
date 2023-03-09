package com.konloch.irc.server.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Konloch
 * @since 3/5/2023
 */
public class Checksum
{
	public static String sha256(String s) throws NoSuchAlgorithmException
	{
		return sha256(s.getBytes(StandardCharsets.UTF_8));
	}
	
	public static String sha256(byte[] bytes) throws NoSuchAlgorithmException
	{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		return hex(digest.digest(bytes));
	}
	
	public static String hex(byte[] bytes)
	{
		StringBuilder result = new StringBuilder();
		
		for (byte aByte : bytes)
			result.append(String.format("%02x", aByte));
		
		return result.toString();
	}
}
