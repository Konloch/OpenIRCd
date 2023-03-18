package com.konloch.irc.server.util;

import com.konloch.disklib.DiskWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

/**
 * @author Konloch
 * @since 3/17/2023
 */
public class DumpResource
{
	public static void dump(String path, File out)
	{
		if(out.exists())
			return;
		
		try (InputStream in = DumpResource.class.getResourceAsStream(path);
		     ByteArrayOutputStream o = new ByteArrayOutputStream())
		{
			if(in == null)
				return;
			
			int read;
			while((read = in.read()) != -1)
			{
				o.write(read);
			}
			
			DiskWriter.write(out, o.toByteArray());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
