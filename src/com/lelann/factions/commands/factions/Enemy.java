package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.FactionRelationship.FactionRelationshipType;

public class Enemy extends AbstractCommand {
	public Enemy() {
		super("enemy", "faction.play.enemy", "%gold%/factions enemy %red%faction", "%gold%Met une %red%faction %gold%en %red%ennemi", "/f enemy <faction>", null);
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
				sendMessage(sender, "%red%Vous savez comment on appelle ça ? De la skyzophrénie !");
			} else {
				FactionRelationshipType relation = getFactionsManager().getRelationship(faction, theFaction);
				if(relation == FactionRelationshipType.ENEMY){
					sendMessage(sender, "%red%Vous êtes déjà ennemis avec cette faction !");
				} else {
					getFactionsManager().setRelationship(faction, theFaction, FactionRelationshipType.ENEMY);
					faction.sendMessage(PREFIX + "%yellow%Vous êtes maintenant ennemis avec %red%" + theFaction.getName() + "%yellow% ! :o");
					theFaction.sendMessage(PREFIX + "%yellow%Vous êtes maintenant ennemis avec %red%" + faction.getName() + "%yellow% ! :o");
				}
			}
		}
	}
}