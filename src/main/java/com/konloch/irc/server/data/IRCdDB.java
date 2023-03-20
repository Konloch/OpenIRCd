package com.konloch.irc.server.data;

import com.konloch.disklib.GZipDiskReader;
import com.konloch.disklib.GZipDiskWriter;
import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.events.listeners.IRCdAdapter;
import com.konloch.irc.server.channel.Channel;
import com.konloch.irc.server.client.User;
import com.konloch.irc.server.data.serializer.CollectionsSerializer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.DataFormatException;

/**
 * @author Konloch
 * @since 3/19/2023
 */
public class IRCdDB
{
	private final OpenIRCd irc;
	private final CollectionsSerializer serializer;
	private final HashMap<String, Channel> channels;
	private final transient HashMap<Long, User> connected;
	private final transient HashMap<String, AtomicLong> connectedMap;
	private final File channelsFile;
	
	public IRCdDB(OpenIRCd irc)
	{
		this.irc = irc;
		
		//definte the channels file
		channelsFile = new File("channels.db");
		
		//create a new serializer
		serializer = new CollectionsSerializer();
		
		//create a new hashmap for the channels
		channels = new HashMap<>();
		
		//load the previously saved channels data if it exists
		if(channelsFile.exists())
		{
			try
			{
				System.out.println("IM CURIOUS: " + GZipDiskReader.readString(channelsFile));
				serializer.deserializeHashMap(GZipDiskReader.readString(channelsFile), channels);
			}
			catch (IOException | DataFormatException e)
			{
				e.printStackTrace();
			}
		}
		
		//init connected maps
		connected = new HashMap<>();
		connectedMap = new HashMap<>();
		
		//save on IRCd shutdown
		irc.getEvents().getIrcEvents().add(new IRCdAdapter()
		{
			@Override
			public void onIRCStop()
			{
				save();
			}
		});
	}
	
	public void save()
	{
		try
		{
			GZipDiskWriter.write(channelsFile, serializer.serialize(channels));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public HashMap<String, Channel> getChannels()
	{
		return channels;
	}
	
	public HashMap<String, AtomicLong> getConnectedMap()
	{
		return connectedMap;
	}
	
	public HashMap<Long, User> getConnected()
	{
		return connected;
	}
}
