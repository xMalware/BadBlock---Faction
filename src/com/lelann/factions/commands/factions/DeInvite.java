package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.Faction.FactionRank;

public class DeInvite extends AbstractCommand {

	public DeInvite() {
		super("deinvite", "faction.play.deinvite", "%gold%/factions deinvite %red%player", "%gold%Annule l'invitation d'un joueur %red%player %gold%à la faction", "/f deinvite <player>", null);
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
			sendMessage(sender, "%red%Vous devez être modérateur pour annuler l'invitation d'un joueur dans votre faction.");
		} else if(args.length < 1){
			sendHelp(sender);
		} else {
			Player thePlayer = getServer().getPlayer(args[0]);
			if(thePlayer == null){
				sendMessage(sender, "%red%" + args[0] + " n'est pas connecté !");
			} else {
				if(faction.getPlayers().containsKey(thePlayer.getUniqueId())){
					sendMessage(sender, "%red%Le joueur fait déjà partie de la faction !");
					return;
				}
				if(!faction.getInvitedPlayers().contains(player.getUniqueId())){
					sendMessage(sender, "%red%Le joueur n'est pas invité à la faction !");
				} else {
					faction.getInvitedPlayers().remove(player.getUniqueId());
					sendMessage(thePlayer, "%red%" + player.getLastUsername() + " %yellow%a annulé votre invitation dans sa faction %red%" + faction.getName() + "%yellow% !");
					faction.sendMessage(PREFIX + "%red%" + thePlayer.getName() + "%yellow% n'est plus invité.");
				}
			}
		}
	}
}