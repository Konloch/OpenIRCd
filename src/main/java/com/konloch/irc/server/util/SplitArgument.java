package com.konloch.irc.server.util;

import java.util.ArrayList;

/**
 * @author Konloch
 * @since 3/17/2023
 */
public class SplitArgument
{
	public static String[] parse(String input)
	{
		ArrayList<String> results = new ArrayList<>();
		
		StringBuilder buffer = new StringBuilder();
		StringBuilder escapeBuffer = new StringBuilder();
		boolean escapeFlag = false;
		boolean quoteFlag = false;
		
		char[] arr = input.toCharArray();
		for(char c : arr)
		{
			boolean delimiter = false;
			boolean escape = false;
			boolean quote = false;
			switch(c)
			{
				case ' ':
					delimiter = true;
					break;
					
				case '\\':
					escape = true;
					break;
					
				case '"':
					quote = true;
					break;
			}
			
			if(escapeFlag && quote)
			{
				buffer.append(c);
				escapeFlag = false;
				continue;
			}
			
			if(delimiter && !quoteFlag)
			{
				results.add(buffer.toString());
				buffer = new StringBuilder();
			}
			else if(escape)
			{
				escapeFlag = true;
				escapeBuffer.append(c);
			}
			else if(quote)
			{
				quoteFlag = !quoteFlag;
			}
			else if(!quoteFlag)
			{
				if(escapeFlag)
				{
					buffer.append(escapeBuffer);
					escapeBuffer = new StringBuilder();
					escapeFlag = false;
				}
				
				buffer.append(c);
			}
			else
			{
				System.out.println("WTF: " + quoteFlag);
				buffer.append(c);
			}
		}
		
		if(buffer.length() != 0)
			results.add(buffer.toString());
		
		return results.toArray(new String[0]);
	}
}
