package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.Faction.FactionRank;

public class Kick extends AbstractCommand {

	public Kick() {
		super("kick", "faction.play.kick", "%gold%/factions kick <player>", "%gold%Ejecte %red%player %gold%de votre %red%faction", "/f kick <player>", null);
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
			sendMessage(sender, "%red%Vous ne pouvez pas executer cette commande en tant que membre de faction !");
		} else if(args.length == 0){
			sendHelp(sender);
		} else {
			FactionPlayer fp = getPlayersManager().getPlayer(args[0]);
			if(fp == null){
				sendMessage(sender, "%red%Joueur introuvable !");
			} else if(fp.getFactionId() != faction.getFactionId()){
				sendMessage(sender, "%red%Ce joueur n'est pas dans votre faction !");
			} else if(fp.getFactionRank().getPower() >= player.getFactionRank().getPower() && player.getFactionRank() != FactionRank.LEADER){
				sendMessage(sender, "%red%Ce joueur à un grade trop haut par rapport à vous pour que vous puissez l'éjecté !");
			} else {
				faction.sendMessage(PREFIX + "%red%" + player.getLastUsername() + " %yellow%a été éjecté ! :o");
				faction.removeMember(fp); faction.save(false);
				fp.leaveFaction(); fp.save(false);
			}
		}
	}
}