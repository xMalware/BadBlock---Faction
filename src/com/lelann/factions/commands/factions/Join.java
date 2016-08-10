package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;

public class Join extends AbstractCommand {

	public Join() {
		super("join", "faction.play.join", "%gold%/factions join %red%faction", "%gold%Rejoint la faction %red%faction", "/f join <faction>", null);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			sendMessage(sender, NO_CONSOLE);
			return;
		}

		FactionPlayer player = getPlayersManager().getPlayer(sender);
		Faction faction = player.getFaction();
		
		if(!faction.isDefault()){
			sendMessage(sender, "%red%Vous avez déjà une faction, vous ne pouvez pas en rejoindre une autre. /f leave pour quitter.");
		} else if(args.length < 1){
			sendHelp(sender);
		} else {
			Faction theFaction = getFactionsManager().matchFaction(args[0]);
			if(theFaction == null || theFaction.isDefault()){
				sendMessage(sender, "%red%" + args[0] + " n'existe pas !");
			} else {
				if(!theFaction.getInvitedPlayers().contains(player.getUniqueId()) && !hasPermission(sender, "faction.admin.join")){
					sendMessage(sender, "%red%Vous n'êtes pas invité dans cette faction !");
				} else if(theFaction.getPlayers().size() >= 15){
					sendMessage(sender, "%red%La faction est déjà pleine !");
				} else {
					theFaction.getInvitedPlayers().remove(player.getUniqueId());
					theFaction.addMember(player);
					theFaction.sendMessage(PREFIX + "%red%" + player.getLastUsername() + "%yellow% a rejoint la faction.");
				}
			}
		}
	}
}