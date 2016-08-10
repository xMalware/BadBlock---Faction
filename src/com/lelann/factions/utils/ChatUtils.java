package com.lelann.factions.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class ChatUtils {
	public static String colorReplace(String input){
		String output = null;
		output = input.replace("%black%", ChatColor.BLACK.toString());
		output = output.replace("%dblue%", ChatColor.DARK_BLUE.toString());
		output = output.replace("%dgreen%", ChatColor.DARK_GREEN.toString());
		output = output.replace("%darkaqua%", ChatColor.DARK_AQUA.toString());
		output = output.replace("%dred%", ChatColor.DARK_RED.toString());
		output = output.replace("%darkred%", ChatColor.DARK_RED.toString());
		output = output.replace("%dpurple%", ChatColor.DARK_PURPLE.toString());
		output = output.replace("%gold%", ChatColor.GOLD.toString());
		output = output.replace("%gray%", ChatColor.GRAY.toString());
		output = output.replace("%dgray%", ChatColor.DARK_GRAY.toString());
		output = output.replace("%blue%", ChatColor.BLUE.toString());
		output = output.replace("%green%", ChatColor.GREEN.toString());
		output = output.replace("%aqua%", ChatColor.AQUA.toString());
		output = output.replace("%red%", ChatColor.RED.toString());
		output = output.replace("%lpurple%", ChatColor.LIGHT_PURPLE.toString());
		output = output.replace("%yellow%", ChatColor.YELLOW.toString());
		output = output.replace("%white%", ChatColor.WHITE.toString());
		output = output.replace("%bold%", ChatColor.BOLD.toString());
		output = output.replace("%italic%", ChatColor.ITALIC.toString());
		output = output.replace("%magic%", ChatColor.MAGIC.toString());
		output = output.replace("%default%", ChatColor.RESET.toString());

		output = ChatColor.translateAlternateColorCodes('&', output);
		return output;
	}
	
	public static String[] colorReplace(String... input){
		for(int i=0;i<input.length;i++){
			input[i] = colorReplace(input[i]);
		}
		
		return input;
	}
	
	public static String colorDelete(String input){
		input = input.replace("&n", "");
		input = colorReplace(input);
		String output = input.replace(ChatColor.BLACK.toString(), "");
		output = output.replace(ChatColor.DARK_BLUE.toString(), "");
		output = output.replace(ChatColor.DARK_GREEN.toString(), "");
		output = output.replace(ChatColor.DARK_AQUA.toString(), "");
		output = output.replace(ChatColor.DARK_RED.toString(), "");
		output = output.replace(ChatColor.DARK_RED.toString(), "");
		output = output.replace(ChatColor.DARK_PURPLE.toString(), "");
		output = output.replace(ChatColor.GOLD.toString(), "");
		output = output.replace(ChatColor.GRAY.toString(), "");
		output = output.replace(ChatColor.DARK_GRAY.toString(), "");
		output = output.replace(ChatColor.BLUE.toString(), "");
		output = output.replace(ChatColor.GREEN.toString(), "");
		output = output.replace(ChatColor.AQUA.toString(), "");
		output = output.replace(ChatColor.RED.toString(), "");
		output = output.replace(ChatColor.LIGHT_PURPLE.toString(), "");
		output = output.replace(ChatColor.YELLOW.toString(), "");
		output = output.replace(ChatColor.WHITE.toString(), "");
		output = output.replace(ChatColor.BOLD.toString(), "");
		output = output.replace(ChatColor.ITALIC.toString(), "");
		output = output.replace(ChatColor.MAGIC.toString(), "");
		output = output.replace(ChatColor.RESET.toString(), "");
		
		return output;
	}
	
	/* Toute les possibilites d'envois de message */

	public static void broadcast(String message){
		Bukkit.broadcastMessage(colorReplace(message));
	}
	/* ARRAY */
	public static void sendMessage(Player player, String... message){
		for(int i=0;i<message.length;i++)
			if(message[i] != null && player != null)
				player.sendMessage(colorReplace(message[i]));
	}
	public static void sendMessage(CommandSender player, String message[]){
		for(int i=0;i<message.length;i++)
			if(message[i] != null && player != null)
				player.sendMessage(colorReplace(message[i]));
	}
	public static void sendMessage(HumanEntity player, String message[]){
		for(int i=0;i<message.length;i++)
			if(message[i] != null && player != null)
				((CommandSender) player).sendMessage(message[i]);
	}

	/* SIMPLE */
	public static void sendMessage(Player player, String message){
		if(player == null || message == null) return;
		sendMessage(player, message.split(";"));
	}
	public static void sendMessage(CommandSender player, String message){
		if(player == null || message == null) return;
		sendMessage(player, message.split(";"));
	}
	public static void sendMessage(HumanEntity player, String message) {
		if(player == null || message == null) return;
		sendMessage(((CommandSender) player), message.split(";"));
	}
}
