package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionRelationship.FactionRelationshipType;

public class Neutral extends AbstractCommand {
	public Neutral() {
		super("neutral", "faction.play.neutral", "%gold%/factions neutral %red%faction", "%gold%Met une %red%faction %gold%en %red%neutre", "/f neutral <faction>", null);
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
			sendMessage(sender, "%red%Vous devez être modérateur pour changer les relations entre votre faction et les autres.");
		} else if(args.length < 1){
			sendHelp(sender);
		} else {
			Faction theFaction = getFactionsManager().matchFaction(args[0]);
			if(theFaction == null || theFaction.isDefault()){
				sendMessage(sender, "%red%" + args[0] + " n'existe pas !");
			} else if(theFaction.equals(faction)){
				sendMessage(sender, "%red%Vous ne pouvez pas être neutre avec vous même ...");
			} else {
				FactionRelationshipType relation = getFactionsManager().getRelationship(faction, theFaction);
				if(relation == FactionRelationshipType.NEUTRAL){
					sendMessage(sender, "%red%Vous êtes déjà neutres avec cette faction !");
				} else if(theFaction.getNeutral().contains(faction.getFactionId()) || relation == FactionRelationshipType.ALLY){
					getFactionsManager().setRelationship(faction, theFaction, FactionRelationshipType.NEUTRAL);
					faction.sendMessage(PREFIX + "%yellow%Vous êtes maintenant neutres avec %red%" + theFaction.getName() + "%yellow% !");
					theFaction.sendMessage(PREFIX + "%yellow%Vous êtes maintenant neutres avec %red%" + faction.getName() + "%yellow% !");
				} else {
					faction.getNeutral().add(theFaction.getFactionId());
					faction.sendMessage(PREFIX + "%red%" + sender.getName() + "%yellow% a proposé à %red%" + theFaction.getName() + "%yellow% d'être neutres !");
					theFaction.sendMessage(PREFIX + "%red%" + sender.getName() + "%yellow% vous propose d'être neutres à %red%" + faction.getName() + "%yellow% !");
				}
			}
		}
	}
}