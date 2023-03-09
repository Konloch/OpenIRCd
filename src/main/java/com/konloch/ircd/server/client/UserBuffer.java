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
	public boolean hasReachedEOL;
	public boolean outputBufferHasData;
	
	public void writeInput(byte[] bytes) throws IOException
	{
		inputBuffer.write(bytes);
		getEOL(bytes);

		//requests that have the EOL sent in chunks need to be handled by processing the entire buffer
		if(!hasReachedEOL)
			getEOL(outputBuffer.toByteArray());
	}
	
	public void writeOutputAndFlush(byte[] bytes) throws IOException
	{
		synchronized (outputBuffer)
		{
			outputBuffer.write(bytes);
			
			outputBufferHasData = true;
		}
	}
	
	private int getEOL(byte[] bytes)
	{
		boolean returnCarriage = false;
		
		int EOLIndex = 0;
		for(byte b : bytes)
		{
			if(!hasReachedEOL)
				EOLIndex++;
			
			char c = (char) b;
			if(c == '\n' || c == '\r')
			{
				if(returnCarriage)
					hasReachedEOL = true;
				else
					returnCarriage = true;
			}
			else if(returnCarriage)
				returnCarriage = false;
		}
		
		return EOLIndex;
	}
}
