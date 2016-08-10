package com.lelann.factions.commands.factions;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionPlayer;

public class Disband extends AbstractCommand {

	public Disband() {
		super("disband", "faction.play.disband", "%gold%/factions disband", "%gold%Supprime votre %red%faction", "/f disband", null);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			sendMessage(sender, NO_CONSOLE);
			return;
		}
		
		FactionPlayer player = getPlayersManager().getPlayer(sender);
		Faction faction = player.getFaction();
		
		if(faction.isDefault()){
			sendMessage(sender, NO_FACTION);
		} else if(player.getFactionRank() != FactionRank.LEADER && !player.getPlayer().hasPermission("faction.admin.bypasscmds")){
			sendMessage(sender, "%red%Seul le chef de la faction peut utiliser cette commande !");
		} else {
			for(UUID uniqueId : faction.getPlayers().keySet()){
				FactionPlayer thePlayer = getPlayersManager().getPlayer(uniqueId);
				
				thePlayer.sendMessage(PREFIX + "%yellow%Votre faction a été disband par %red%" + player.getLastUsername());
				thePlayer.leaveFaction();
				thePlayer.save(false);
			}
			broadcast("%yellow%La faction %red%" + faction.getName() + " %yellow%a été disband !");
			faction.delete();
		}
	}
}