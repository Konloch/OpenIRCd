package com.konloch.irc.extension.cli;

import com.konloch.irc.OpenIRCd;
import com.konloch.irc.extension.Plugin;
import com.konloch.irc.server.client.data.PermissionUserGroup;
import com.konloch.irc.server.client.data.UserData;
import com.konloch.irc.server.util.cli.Command;

import static com.konloch.irc.server.client.data.PermissionUserGroup.*;

/**
 * @author Konloch
 * @since 3/18/2023
 */
public class OPCommands implements Plugin
{
	private OpenIRCd irc;
	
	@Override
	public void install(OpenIRCd irc)
	{
		this.irc = irc;
		
		//global op
		irc.getCLI().register(new Command("op", irc.fromConfig("command.op.description"), command->
		{
			String[] args = command.getArguments();
			
			if(args.length >= 2 && args[0].equals("add"))
				setUserGroup(command.asString(1), GLOBAL_OPERATOR);
			else if(args.length >= 2 && args[0].equals("remove"))
				removeUserGroup(command.asString(1), GLOBAL_OPERATOR);
			else if(args.length >= 1 && args[0].equals("list"))
				list(GLOBAL_OPERATOR);
			else
				System.out.println("Incorrect command usage");
		}).addArgument("add/remove", irc.fromConfig("command.op.add.description"))
				.addArgument("nick", irc.fromConfig("command.op.add.nick.description"))
				.get());
		
		//local op
		irc.getCLI().register(new Command("lop", "Give or remove local operator privileges on a specific user's nick", command->
		{
			String[] args = command.getArguments();
			
			if(args.length >= 2 && args[0].equals("add"))
				setUserGroup(command.asString(1), LOCAL_OPERATOR);
			else if(args.length >= 2 && args[0].equals("remove"))
				removeUserGroup(command.asString(1), LOCAL_OPERATOR);
			else if(args.length >= 1 && args[0].equals("list"))
				list(LOCAL_OPERATOR);
			else
				System.out.println("Incorrect command usage");
		}).addArgument("add/remove", irc.fromConfig("command.op.add.description"))
				.addArgument("nick", irc.fromConfig("command.op.add.nick.description"))
				.get());
	}
	
	private void setUserGroup(String nick, PermissionUserGroup userGroup)
	{
		if(!irc.getDB().getRegisteredUsers().containsKey(nick))
		{
			System.out.println("`" + nick + "` needs to register and secure their nick before you can give them " + userGroup.getTitle() + " status.");
			return;
		}
		
		UserData data = irc.getDB().getRegisteredUsers().get(nick);
		
		if(data.getUsergroup() == userGroup)
		{
			System.out.println("`" + nick + "` is already a " + userGroup.getTitle() + ".");
			return;
		}
		
		data.setUsergroup(userGroup);
		System.out.println("`" + nick + "` has been given " + userGroup.getTitle() + ".");
	}
	
	private void removeUserGroup(String nick, PermissionUserGroup userGroup)
	{
		if(!irc.getDB().getRegisteredUsers().containsKey(nick))
		{
			System.out.println("`" + nick + "` is already not a " + userGroup.getTitle() + ".");
			return;
		}
		
		UserData data = irc.getDB().getRegisteredUsers().get(nick);
		
		if(data.getUsergroup() != userGroup)
		{
			System.out.println("`" + nick + "` is already not a " + userGroup.getTitle() + ".");
			return;
		}
		
		data.setUsergroup(USER);
		System.out.println("`" + nick + "` has been given user privileges.");
	}
	
	
	public void list(PermissionUserGroup userGroup)
	{
		for(UserData data : irc.getDB().getRegisteredUsers().values())
		{
			if(data.getUsergroup() != userGroup)
				continue;
			
			System.out.println("\t+ " + data.getNick() + " - " + data.getEmail() + " - " + data.getUsergroup());
		}
	}
}
