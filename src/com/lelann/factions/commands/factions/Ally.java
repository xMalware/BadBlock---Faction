package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionRelationship.FactionRelationshipType;

public class Ally extends AbstractCommand {
	public Ally() {
		super("ally", "faction.play.ally", "%gold%/factions ally %red%faction", "%gold%Met une %red%faction %gold%en %red%alli�", "/f ally <faction>", null);
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
			sendMessage(sender, "%red%Vous devez �tre mod�rateur pour changer les relations entre votre faction et les autres.");
		} else if(args.length < 1){
			sendHelp(sender);
		} else {
			Faction theFaction = getFactionsManager().matchFaction(args[0]);
			if(theFaction == null || theFaction.isDefault()){
				sendMessage(sender, "%red%" + args[0] + " n'existe pas !");
			} else if(theFaction.equals(faction)){
				sendMessage(sender, "%red%Parce que vous n'�tes pas alli�s avec vous-m�me ?");
			} else {
				FactionRelationshipType relation = getFactionsManager().getRelationship(faction, theFaction);
				if(relation == FactionRelationshipType.ALLY){
					sendMessage(sender, "%red%Vous �tes d�j� alli�s avec cette faction !");
				} else if(theFaction.getAlly().contains(faction.getFactionId())){
					getFactionsManager().setRelationship(faction, theFaction, FactionRelationshipType.ALLY);
					faction.sendMessage(PREFIX + "%yellow%Vous �tes maintenant alli�s avec %red%" + theFaction.getName() + "%yellow% ! :)");
					theFaction.sendMessage(PREFIX + "%yellow%Vous �tes maintenant alli�s avec %red%" + faction.getName() + "%yellow% ! :)");
				} else {
					faction.getAlly().add(theFaction.getFactionId());
					faction.sendMessage(PREFIX + "%red%" + sender.getName() + "%yellow% a propos� � %red%" + theFaction.getName() + "%yellow% d'�tre alli�s ! :)");
					theFaction.sendMessage(PREFIX + "%red%" + sender.getName() + "%yellow% vous proposent d'�tre alli�s � %red%" + faction.getName() + "%yellow% ! :)");
				}
			}
		}
	}
}