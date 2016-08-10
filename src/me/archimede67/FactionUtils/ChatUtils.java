package me.archimede67.FactionUtils;

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
	public static String colorDelete(String input){
		String output = null;
		output = input.replace("%black%", "");
		output = output.replace("%dblue%", "");
		output = output.replace("%dgreen%", "");
		output = output.replace("%darkaqua%", "");
		output = output.replace("%dred%", "");
		output = output.replace("%dpurple%", "");
		output = output.replace("%gold%", "");
		output = output.replace("%gray%", "");
		output = output.replace("%dgray%", "");
		output = output.replace("%blue%", "");
		output = output.replace("%green%", "");
		output = output.replace("%aqua%", "");
		output = output.replace("%red%", "");
		output = output.replace("%lpurple%", "");
		output = output.replace("%yellow%", "");
		output = output.replace("%white%", "");
		output = output.replace("%bold%", "");
		output = output.replace("%italic%", "");
		output = output.replace("%magic%", "");
		output = output.replace("%default%", "");

		output = output.replace("&0", "");
		output = output.replace("&1", "");
		output = output.replace("&2", "");
		output = output.replace("&3", "");
		output = output.replace("&4", "");
		output = output.replace("&5", "");
		output = output.replace("&6", "");
		output = output.replace("&7", "");
		output = output.replace("&8", "");
		output = output.replace("&9", "");
		output = output.replace("&a", "");
		output = output.replace("&b", "");
		output = output.replace("&c", "");
		output = output.replace("&d", "");
		output = output.replace("&e", "");
		output = output.replace("&f", "");
		output = output.replace("&l", "");
		output = output.replace("&r", "");
		output = output.replace("&o", "");
		output = output.replace("&k", "");
		
		output = output.replace("ß0", "");
        output = output.replace("ß1", "");
        output = output.replace("ß2", "");
        output = output.replace("ß3", "");
        output = output.replace("ß4", "");
        output = output.replace("ß5", "");
        output = output.replace("ß6", "");
        output = output.replace("ß7", "");
        output = output.replace("ß8", "");
        output = output.replace("ß9", "");
        output = output.replace("ßa", "");
        output = output.replace("ßb", "");
        output = output.replace("ßc", "");
        output = output.replace("ßd", "");
        output = output.replace("ße", "");
        output = output.replace("ßf", "");
        output = output.replace("ßo", "");
        output = output.replace("ßk", "");
        output = output.replace("ßl", "");
		return output;
	}

	/* Toute les possibilit√©es d'envois de message */

	public static void broadcast(String message){
		Bukkit.broadcastMessage(colorReplace(message));
	}
	/* ARRAY */
	public static void sendMessagePlayer(Player player, String message[]){
		for(int i=0;i<message.length;i++)
			player.sendMessage(colorReplace(message[i]));
	}
	public static void sendMessagePlayer(CommandSender player, String message[]){
		for(int i=0;i<message.length;i++)
			player.sendMessage(colorReplace(message[i]));
	}
	public static void sendMessagePlayer(HumanEntity player, String message[]){
		for(int i=0;i<message.length;i++)
			((CommandSender) player).sendMessage(message[i]);
	}

	/* SIMPLE */
	public static void sendMessagePlayer(Player player, String message){
		if(player == null) return;
		sendMessagePlayer(player, message.split(";"));
	}
	public static void sendMessagePlayer(CommandSender player, String message){
		sendMessagePlayer(player, message.split(";"));
	}
	public static void sendMessagePlayer(HumanEntity player, String message) {
		sendMessagePlayer(((CommandSender) player), message.split(";"));
	}
}
