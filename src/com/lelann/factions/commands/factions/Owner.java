package com.lelann.factions.commands.factions;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionPlayer;

public class Owner extends AbstractCommand {
	public Owner() {
		super("owner", "faction.play.owner", "%gold%/factions owner %red%player", "%gold%Met le joueur %red%player %gold%chef de votre faction", "/f owner <player>", null);
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
			sendMessage(sender, "%red%Vous devez être chef pour définir un nouveau chef de faction.");
		} else if(args.length < 1){
			sendHelp(sender);
		} else {
			if(player.getFactionRank() != FactionRank.LEADER){
				for(UUID uniqueId : faction.getPlayers().keySet()){
					if(faction.getPlayers().get(uniqueId) == FactionRank.LEADER){
						player = getPlayersManager().getPlayer(uniqueId);
					}
				}
			}
			FactionPlayer fp = getPlayersManager().getPlayer(args[0]);
			if(fp == null){
				sendMessage(sender, "%red%Joueur introuvable !");
			} else if(fp.getFactionId() != faction.getFactionId()){
				sendMessage(sender, "%red%Ce joueur n'est pas dans votre faction !");
			} else {
				faction.setRank(fp, FactionRank.LEADER);
				faction.setRank(player, FactionRank.MODERATOR);
				fp.save(false); player.save(false); faction.save(false);

				faction.sendMessage(PREFIX + "%red%" + fp.getLastUsername() + " %yellow%est maintenant chef de votre faction !");
			}
		}
	}
}