package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.Faction.FactionRank;

public class Title extends AbstractCommand {
	public Title() {
		super("title", "faction.play.title", "%gold%/factions title %red%player title", "%gold%Met le titre %red%title %gold%au joueur %red%player", "/f title <player> <title>", null);
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
			sendMessage(sender, "%red%Vous devez être modérateur pour définir le titre d'un membre.");
		} else if(args.length < 2){
			if(args.length == 1){
				FactionPlayer fp = getPlayersManager().getPlayer(args[0]);
				if(fp == null){
					sendMessage(sender, "%red%Joueur introuvable !");
				} else if(fp.getFactionId() != faction.getFactionId()){
					sendMessage(sender, "%red%Ce joueur n'est pas dans votre faction !");
				} else if(fp.getFactionRank().getPower() >= player.getFactionRank().getPower() && player.getFactionRank() != FactionRank.LEADER && !player.getPlayer().hasPermission("faction.admin.bypasscmds") && !fp.getUniqueId().equals(player.getUniqueId())){
					sendMessage(sender, "%red%Ce joueur à un grade trop haut par rapport à vous pour que vous puissez changer son titre !");
				} else {
					fp.setTitle(null);
					fp.save(false);				
					faction.sendMessage(PREFIX + "%red%" + fp.getLastUsername() + " %yellow% n'a plus de titre !");
				}
			} else sendHelp(sender);
		} else if(args[1].length() > 16){
			sendMessage(sender, "%red%Le titre d'un joueur ne peut pas avoir plus de 16 caractères !");
		} else {
			FactionPlayer fp = getPlayersManager().getPlayer(args[0]);
			if(fp == null){
				sendMessage(sender, "%red%Joueur introuvable !");
			} else if(fp.getFactionId() != faction.getFactionId()){
				sendMessage(sender, "%red%Ce joueur n'est pas dans votre faction !");
			} else if(fp.getFactionRank().getPower() >= player.getFactionRank().getPower() && player.getFactionRank() != FactionRank.LEADER && !fp.getUniqueId().equals(player.getUniqueId())){
				sendMessage(sender, "%red%Ce joueur à un grade trop haut par rapport à vous pour que vous puissez changer son titre !");
			} else {
				fp.setTitle(args[1]);
				fp.save(false);				
				faction.sendMessage(PREFIX + "%red%" + fp.getLastUsername() + " %yellow% a maintenant comme titre %red%" + args[1] + " %yellow%!");
			}
		}
	}
}