package com.konloch.ircd.server.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Konloch
 * @since 3/1/2023
 */
public class UserBuffer
{
	public final ByteArrayOutputStream inputBuffer = new ByteArrayOutputStream();
	public final ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
	public boolean outputBufferHasData;
	
	public void writeInput(byte[] bytes) throws IOException
	{
		inputBuffer.write(bytes);
	}
	
	public void writeOutputAndFlush(byte[] bytes) throws IOException
	{
		synchronized (outputBuffer)
		{
			outputBuffer.write(bytes);
			
			outputBufferHasData = true;
		}
	}
}
