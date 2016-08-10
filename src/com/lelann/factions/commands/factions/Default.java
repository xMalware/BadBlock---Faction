package com.lelann.factions.commands.factions;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.managers.ChunksManager;

public class Default extends AbstractCommand {
	public Default() {
		super("default", "faction.admin.default", "%gold%/factions default %red%warzone/safezone/ap/no", "%gold%Définit une zone comme WarZone, SafeZone, AP ou WilderNess", "/f default <type>", null);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			sendMessage(sender, NO_CONSOLE);
			return;
		}
		Player p = (Player) sender;
		if(args.length < 1){
			sendHelp(sender);
		} else {
			ChunksManager manager = getChunksManager(p.getLocation().getWorld());
			Chunk c = p.getLocation().getChunk();
			if(manager == null){
				sendMessage(p, "%red%Les sanctions ne sont pas activés dans ce monde !");
			} if(args[0].equalsIgnoreCase("warzone")){
				manager.setAp(c, false, Faction.WARZONE);
			} else if(args[0].equalsIgnoreCase("safezone")){
				manager.setAp(c, false, Faction.SAFEZONE);
			} else if(args[0].equalsIgnoreCase("no")){
				manager.setAp(c, false, Faction.WILDERNESS);
			} else if(args[0].equalsIgnoreCase("ap")){
				manager.setAp(c, true, Faction.WILDERNESS);
			} else {
				sendHelp(p);
				return;
			}
			sendMessage(p, "%yellow%Modifié !");
			
		}
	}
}