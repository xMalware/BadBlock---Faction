package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.Faction.FactionRank;

public class Rank extends AbstractCommand {

	public Rank() {
		super("rank", "faction.play.rank", "%gold%/factions rank %red%player rank (modo/membre/nouveau)", "%gold%Définit le grade %red%rank %gold%d'un joueur %red%player", "/f sethome", null);
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
		} else if(player.getFactionRank().getPower() < FactionRank.LEADER.getPower() && !player.getPlayer().hasPermission("faction.admin.bypasscmds")){
			sendMessage(sender, "%red%Vous devez être chef pour définir le grade d'un joueur.");
		} else if(args.length < 2){
			sendHelp(sender);
		} else {
			FactionPlayer fp = getPlayersManager().getPlayer(args[0]);
			if(fp == null){
				sendMessage(sender, "%red%Joueur introuvable !");
			} else if(fp.getFactionId() != faction.getFactionId()){
				sendMessage(sender, "%red%Ce joueur n'est pas dans votre faction !");
			} else if(fp.getUniqueId().equals(player.getUniqueId()) && !player.getPlayer().hasPermission("faction.admin.bypasscmds")){
				sendMessage(sender, "%red%Vous ne pouvez pas changer votre propre grade !");
			} else {
				FactionRank rank = FactionRank.matchRank(args[1]);
				if(rank == null){
					sendMessage(sender, "%red%Les grades sont : modo, membre et nouveau.");
					return;
				}
				faction.setRank(fp, rank);
				fp.save(false); faction.save(false);
				
				faction.sendMessage(PREFIX + "%red%" + fp.getLastUsername() + " %yellow%est maintenant %red%" + rank.getRankName().toLowerCase() + " %yellow%!");
			}
		}
	}
}