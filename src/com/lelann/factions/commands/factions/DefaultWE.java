package com.lelann.factions.commands.factions;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.managers.ChunksManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class DefaultWE extends AbstractCommand {
	public DefaultWE() {
		super("defaultwe", "faction.admin.defaultwe", "%gold%/factions defaultwe %red%warzone/safezone/ap/no", "%gold%Définit une zone comme WarZone, SafeZone, AP ou WilderNess la séléction", "/f default <type>", null);
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
			WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
			Selection selection = worldEdit.getSelection(p);
			 
			if (selection != null) {
			    World world = selection.getWorld();
			    Location min = selection.getMinimumPoint();
			    Location max = selection.getMaximumPoint();
			    
			    int xMin = min.getChunk().getX(), zMin = min.getChunk().getZ(),
			    		xMax = max.getChunk().getX(), zMax = max.getChunk().getX();
			    
				ChunksManager manager = getChunksManager(p.getLocation().getWorld());
				if(manager == null){
					sendMessage(p, "%red%Les claims ne sont pas activés dans ce monde !");
				} else if(args[0].equalsIgnoreCase("warzone") || args[0].equalsIgnoreCase("ap")
						|| args[0].equalsIgnoreCase("safezone") || args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("ap")){
					manager.setAp(p.getLocation().getChunk(), false, Faction.WARZONE);
				} else {
					sendHelp(p);
					return;
				}
				
				int nbr = 0;
				
				if(xMin > xMax){
					int temp = xMin;
					xMin = xMax;
					xMax = temp;
				}
				
				if(zMin > zMax){
					int temp = zMin;
					zMin = zMax;
					zMax = temp;
				}
				
			    for(int x=xMin;x<=xMax;x++){
			    	for(int z=zMin;z<=zMax;z++){
			    		Chunk c = world.getChunkAt(x,  z);
			    		if(args[0].equalsIgnoreCase("warzone")){
							manager.setAp(c, false, Faction.WARZONE);
						} else if(args[0].equalsIgnoreCase("safezone")){
							manager.setAp(c, false, Faction.SAFEZONE);
						} else if(args[0].equalsIgnoreCase("no")){
							manager.setAp(c, false, Faction.WILDERNESS);
						} else if(args[0].equalsIgnoreCase("ap")){
							manager.setAp(c, true, Faction.WILDERNESS);
						}
			    		nbr++;
			    	}
			    }
				sendMessage(p, "%yellow%Modifié ! (" + nbr + ")");
			} else {
			    sendMessage(p, "%red%Vous n'avez pas de séléction WorldEdit !");
			}
			 
		}
	}
}