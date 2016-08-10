package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.Faction.FactionRank;

public class Invite extends AbstractCommand {

	public Invite() {
		super("invite", "faction.play.invite", "%gold%/factions invite %red%player", "%gold%Invite le joueur %red%player %gold%dans la faction", "/f invite <player>", null);
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
			sendMessage(sender, "%red%Vous devez être modérateur pour inviter un joueur dans votre faction.");
		} else if(args.length < 1){
			sendHelp(sender);
		} else {
			Player thePlayer = getServer().getPlayer(args[0]);
			if(thePlayer == null){
				sendMessage(sender, "%red%" + args[0] + " n'est pas connecté !");
			} else {
				if(!faction.getInvitedPlayers().contains(thePlayer.getUniqueId()))
					faction.getInvitedPlayers().add(thePlayer.getUniqueId());
				else {
					sendMessage(sender, "%red%Le joueur fait déjà partie de la faction !");
					return;
				}
				sendMessage(thePlayer, "%red%" + player.getLastUsername() + " %yellow%vous a invité dans sa faction %red%" + faction.getName() + "%yellow% !");
				faction.sendMessage(PREFIX + "%red%" + thePlayer.getName() + "%yellow% a été invité par %red%" + sender.getName() + "%yellow%.");
			}
		}
	}
}