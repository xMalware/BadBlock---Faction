package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.FactionObject;
import com.lelann.factions.utils.ChatUtils;
import com.lelann.factions.utils.JRawMessage;
import com.lelann.factions.utils.JRawMessage.ClickEventType;
import com.lelann.factions.utils.JRawMessage.HoverEventType;

import lombok.Getter;

public abstract class AbstractCommand extends FactionObject {
	public static final String NO_PERM = "%red%Vous n'avez pas la permission d'executer cette commande !",
			NO_CONSOLE = "%red%Seul les joueurs peuvent utiliser cette commande !",
			NO_FACTION = "%red%Vous devez avoir une faction pour executer cette commande !";
	public static final String PREFIX = "%darkaqua%[%aqua%Factions%darkaqua%] ";
	private JRawMessage message;
	@Getter private String messageConsole, name, permission;

	public boolean hasPermission(CommandSender sender){
		return hasPermission(sender, permission);
	}
	
	public boolean hasPermission(CommandSender sender, String permission){
		if(permission.startsWith("faction.admin.") && sender.hasPermission("faction.admin.*")){
			return true;
		} else if(permission.startsWith("faction.play.") && sender.hasPermission("faction.play.*")){
			return true;
		} else {
			return sender.hasPermission("faction.*")
					|| sender.hasPermission("*")
					|| sender.hasPermission(permission);
		}
	}
	
	public void sendHelp(CommandSender sender){
		if(sender instanceof Player){
			message.send((Player) sender);
		} else ChatUtils.sendMessage(sender, messageConsole);
	}
	
	protected void sendMessage(CommandSender sender, String msg){
		ChatUtils.sendMessage(sender, PREFIX + msg);
	}
	
	protected void broadcast(String msg){
		ChatUtils.broadcast(PREFIX + msg);
	}
	
	public AbstractCommand(String name, String permission, String description, String hover, String onClickShow, String onClickRun){
		message = new JRawMessage(description);
		messageConsole = ChatUtils.colorReplace(description);
		this.permission = permission;
		this.name = name;
		
		message.addHoverEvent(HoverEventType.SHOW_TEXT, hover);
		if(onClickShow != null)
			message.addClickEvent(ClickEventType.SUGGEST_COMMAND, onClickShow, false);
		else if(onClickRun != null)
			message.addClickEvent(ClickEventType.RUN_COMMAND, onClickRun, false);
	}
	
	public abstract void runCommand(CommandSender sender, String[] args);
}
