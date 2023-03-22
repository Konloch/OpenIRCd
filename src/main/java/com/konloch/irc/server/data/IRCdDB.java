package com.konloch.irc.server.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.konloch.disklib.GZipDiskReader;
import com.konloch.disklib.GZipDiskWriter;
import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.events.listeners.IRCdAdapter;
import com.konloch.irc.server.channel.Channel;
import com.konloch.irc.server.client.User;
import com.konloch.irc.server.client.data.UserData;

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
	private final Gson serializer;
	private IRCdDBData data;
	private final File dataFile;
	
	public IRCdDB(OpenIRCd irc)
	{
		this.irc = irc;
		
		//define the various files
		dataFile = new File("irc.db");
		
		//create a new serializer
		serializer = new GsonBuilder().create();
		
		//create a new hashmap for the channels
		data = new IRCdDBData();
		
		//load the previously saved channels data if it exists
		if(dataFile.exists())
		{
			try
			{
				data = serializer.fromJson(GZipDiskReader.readString(dataFile), IRCdDBData.class);
			}
			catch (IOException | DataFormatException e)
			{
				e.printStackTrace();
			}
		}
		
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
			GZipDiskWriter.write(dataFile, serializer.toJson(data));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public HashMap<String, Channel> getChannels()
	{
		return data.channels;
	}
	
	public HashMap<String, UserData> getRegisteredUsers()
	{
		return data.registeredUsers;
	}
	
	public HashMap<String, AtomicLong> getConnectedMap()
	{
		return data.connectedMap;
	}
	
	public HashMap<Long, User> getConnected()
	{
		return data.connected;
	}
	
	public static class IRCdDBData
	{
		private final HashMap<String, Channel> channels = new HashMap<>();
		private final HashMap<String, UserData> registeredUsers = new HashMap<>();
		private final transient HashMap<Long, User> connected = new HashMap<>();
		private final transient HashMap<String, AtomicLong> connectedMap = new HashMap<>();
	}
}
