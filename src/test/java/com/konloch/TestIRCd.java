package com.konloch;

import java.io.IOException;
import java.net.URISyntaxException;

import com.konloch.irc.OpenIRCd;

/**
 * @author Konloch
 * @since 3/2/2023
 */
public class TestIRCd
{
	public static void main(String[] args) throws IOException, URISyntaxException
	{
		//start IRCd
		OpenIRCd.main(args);
		
		//start-up pircbot
		/*PircBot bot = new Bot();
		
		bot.setVerbose(true);
		
		try
		{
			bot.connect("localhost");
			
			// Join the #pircbot channel.
			bot.joinChannel("#pircbot");
			
			while(true)
			{
				bot.sendMessage("#pircbot", "Test...");
				
				try
				{
					Thread.sleep(2000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (IrcException e)
		{
			e.printStackTrace();
		}*/
	}
	
	/*private static class Bot extends PircBot
	{
		public Bot()
		{
			this.setName("Bot");
		}
	}*/
}
