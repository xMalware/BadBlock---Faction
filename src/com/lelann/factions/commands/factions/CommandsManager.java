package com.lelann.factions.commands.factions;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

import com.lelann.factions.utils.ChatUtils;

public class CommandsManager {
	private static CommandsManager instance;
	public static CommandsManager getInstance(){
		return instance;
	}
	
	private Map<String, AbstractCommand> commands;
	
	public void addCommand(AbstractCommand command, String... aliases){
		commands.put(command.getName().toLowerCase(), command);
		for(final String aliase : aliases){
			commands.put(aliase, command);
		}
	}
	public void sendHelp(CommandSender sender){
		for(AbstractCommand command : commands.values())
			command.sendHelp(sender);
	}
	public AbstractCommand get(String name){
		return commands.get(name.toLowerCase());
	}
	public void useCommand(CommandSender sender, String[] args){
		if(args.length == 0){
			useCommand(sender, new String[]{"help"});
			return;
		}
		
		AbstractCommand command = commands.get(args[0].toLowerCase());
		if(command == null){
			ChatUtils.sendMessage(sender, AbstractCommand.PREFIX + "%red%Mauvaise utilisation. Pour en savoir plus /f help !");
		} else if(!command.hasPermission(sender)){
			ChatUtils.sendMessage(sender, "%red%Vous n'avez pas la permission d'utiliser cette commande !");
		} else {
			String[] otherArgs = new String[args.length - 1];
			for(int i=1;i<args.length;i++)
				otherArgs[i-1] = args[i];
			command.runCommand(sender, otherArgs);
		}
	}
	public CommandsManager(){
		instance = this;
		commands = new LinkedHashMap<String, AbstractCommand>();
		
		addCommand(new Chat(), "c");
		addCommand(new Create());
		addCommand(new DeInvite());
		addCommand(new DelHome());
		addCommand(new Description(), "desc");
		addCommand(new Disband());
		addCommand(new Home(), "h");
		addCommand(new Info(), "f", "view");
		addCommand(new Invite());
		addCommand(new Join());
		addCommand(new Leave(), "quit");
		addCommand(new Kick(), "eject");
		addCommand(new Owner(), "admin", "leader");
		addCommand(new Rank());
		addCommand(new SetHome());
		addCommand(new Tag());
		addCommand(new Title());
		addCommand(new Claim());
		addCommand(new UnClaim());
		addCommand(new UnClaimAll());
		addCommand(new AutoClaim());
		addCommand(new AutoUnClaim());
		addCommand(new com.lelann.factions.commands.factions.Map());
		addCommand(new Enemy());
		addCommand(new Ally());
		addCommand(new Neutral());
		addCommand(new Default());
		addCommand(new DefaultWE());
		addCommand(new Bypass());
		addCommand(new Help(), "h");
		addCommand(new Power(), "p", "pow");
		addCommand(new ResetPower());
		addCommand(new Ap());
	}
}
