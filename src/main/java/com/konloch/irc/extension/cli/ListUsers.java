package com.konloch.irc.extension.cli;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.Plugin;
import com.konloch.irc.server.channel.Channel;
import com.konloch.irc.server.client.User;
import com.konloch.irc.server.client.data.PermissionUserGroup;
import com.konloch.irc.server.client.data.UserData;
import com.konloch.irc.server.util.cli.Command;

import java.util.ArrayList;

/**
 * @author Konloch
 * @since 3/18/2023
 */
public class ListUsers implements Plugin
{
	@Override
	public void install(OpenIRCd irc)
	{
		//list users command
		irc.getCLI().register(new Command("who", "List all users - connected or registered", command->
		{
			if(command.getArguments().length == 0)
			{
				System.out.println("Connected Users:");
				if(irc.getConnected().isEmpty())
					System.out.println("\tNone");
				else for(User connectedUser : irc.getConnected().values())
				{
					StringBuilder sb = new StringBuilder("\t+ ");
					
					if(!connectedUser.isFlagHasAuthorizedNick() && irc.getDB().getRegisteredUsers().containsKey(connectedUser.getNick()))
						sb.append("[UNAUTHORIZED] ");
					
					sb.append(connectedUser.getNick()).append(" - ");
					sb.append(connectedUser.getUser()).append(" - ");
					sb.append(connectedUser.getRealName());
					
					System.out.println(sb);
				}
				
				ArrayList<UserData> registeredUsers = new ArrayList<>(irc.getDB().getRegisteredUsers().values());
				
				registeredUsers.sort((o1, o2) ->
				{
					if(o1.getUsergroup() == PermissionUserGroup.GLOBAL_OPERATOR && o2.getUsergroup() == PermissionUserGroup.USER)
						return -1;
					if(o1.getUsergroup() == PermissionUserGroup.GLOBAL_OPERATOR && o2.getUsergroup() == PermissionUserGroup.LOCAL_OPERATOR)
						return -1;
					if(o1.getUsergroup() == PermissionUserGroup.LOCAL_OPERATOR && o2.getUsergroup() == PermissionUserGroup.USER)
						return -1;
					
					return o1.getNick().compareTo(o2.getNick());
				});
				
				System.out.println();
				System.out.println("Registered Users:");
				if(registeredUsers.isEmpty())
					System.out.println("\tNone");
				else for(UserData registeredUserData : registeredUsers)
				{
					System.out.println("\t+ " + registeredUserData.getNick() + " - " + registeredUserData.getEmail() + " - " + registeredUserData.getUsergroup());
				}
			}
			else
			{
				String channelName = command.asString(0);
				Channel channel = irc.getChannels().get(channelName);
				
				if(channel == null)
				{
					System.out.println("`" + channelName + "` is closed.");
					return;
				}
				
				System.out.println("Connected Users:");
				if(irc.getConnected().isEmpty())
					System.out.println("\tNone");
				else for(User connectedUser : channel.getUsers())
				{
					StringBuilder sb = new StringBuilder("\t+ ");
					
					if(!connectedUser.isFlagHasAuthorizedNick() && irc.getDB().getRegisteredUsers().containsKey(connectedUser.getNick()))
						sb.append("[UNAUTHORIZED] ");
					
					sb.append(connectedUser.getNick()).append(" - ");
					sb.append(connectedUser.getUser()).append(" - ");
					sb.append(connectedUser.getRealName());
					
					System.out.println(sb);
				}
				
				//TODO list all users connected to the channel, then all users with flags for this channel
			}
		}).addOptionalArgument("channel", "Any channel to list the users from").get());
	}
}
