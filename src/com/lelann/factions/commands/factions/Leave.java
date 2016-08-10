package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionPlayer;

public class Leave extends AbstractCommand {

	public Leave() {
		super("leave", "faction.play.leave", "%gold%/factions leave", "%gold%Quitter votre %red%faction", "/f leave", null);
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
		} else if(player.getFactionRank() == FactionRank.LEADER){
			sendMessage(sender, "%red%Vous ne pouvez pas executer cette commande en tant que chef de faction ! Utilisez /f disband.");
		} else {
			faction.sendMessage(PREFIX + "%red%" + player.getLastUsername() + " %yellow%a quitté la faction ! :(");
			faction.removeMember(player); faction.save(false);
			player.leaveFaction(); player.save(false);
		}
	}
}