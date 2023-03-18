package com.konloch.irc.server.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author Konloch
 * @since 3/17/2023
 */
public class ReadResource
{
	public static byte[] read(String path)
	{
		try (InputStream in = ReadResource.class.getResourceAsStream(path);
		     ByteArrayOutputStream o = new ByteArrayOutputStream())
		{
			int read;
			while((read = in.read()) != -1)
			{
				o.write(read);
			}
			
			return o.toByteArray();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
