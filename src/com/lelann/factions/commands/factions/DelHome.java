package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.Faction.FactionRank;

public class DelHome extends AbstractCommand {

	public DelHome() {
		super("delhome", "faction.play.delhome", "%gold%/factions delhome", "%gold%Supprime la %red%home %gold%de votre faction", "/f delhome", null);
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
		} else if(player.getFactionRank().getPower() < FactionRank.MODERATOR.getPower() && !player.getPlayer().hasPermission("faction.admin.bypasscmds")){
			sendMessage(sender, "%red%Vous devez être modérateur pour supprimer la home de votre faction.");
		} else {
			faction.setHome(null);
			faction.save(false);
			sendMessage(sender, "%yellow%Home de votre faction supprimé !");
		}
	}
}