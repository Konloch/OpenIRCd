package com.konloch.irc.extension.cli;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.Plugin;
import com.konloch.irc.server.client.User;
import com.konloch.irc.server.client.data.PermissionUserGroup;
import com.konloch.irc.server.client.data.UserData;
import com.konloch.irc.server.util.cli.Command;
import com.konloch.irc.server.util.cli.CommandArgument;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author Konloch
 * @since 3/18/2023
 */
public class ListUsers implements Plugin
{
	@Override
	public void install(OpenIRCd irc)
	{
		//help command
		irc.getCLI().register(new Command("who", "List all users - connected or registered", command->
		{
			if(command.getArguments().length == 0)
			{
				System.out.println("Connected Users:");
				for(User connectedUser : irc.getConnected().values())
				{
					System.out.println("\t+ " + connectedUser.getNick() + " - " + connectedUser.getUser() + " - " + connectedUser.getRealName());
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
					
					return 0;
				});
				
				System.out.println("Registered Users:");
				for(UserData registeredUserData : registeredUsers)
				{
					System.out.println("\t+ " + registeredUserData.getNick() + " - " + registeredUserData.getEmail() + " - " + registeredUserData.getUsergroup());
				}
			}
			else
			{
				//TODO list all users connected to the channel, then all users with flags for this channel
			}
		}).addOptionalArgument("channel", "Any channel to list the users from").get());
	}
}
