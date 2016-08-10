package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.Faction.FactionRank;

public class AutoUnClaim extends AbstractCommand {
	public AutoUnClaim() {
		super("autounclaim", "faction.play.autounclaim", "%gold%/factions autounclaim", "%gold%Enlève automatiquement la protection de tous les %red%chunks %gold%sur lesquels vous allez", "/f autounclaim", null);
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
			sendMessage(sender, "%red%Vous devez être modérateur pour claim un chunk.");
		} else {
			if(player.isAutoclaim()){
				player.setAutoclaim(false);
				player.setAutounclaim(false);
				sendMessage(sender, "%yellow%Unclaim automatique désactivé !");
			} else {
				player.setAutoclaim(false);
				player.setAutounclaim(true);
				CommandsManager.getInstance().useCommand(sender, new String[]{"unclaim"});
				sendMessage(sender, "%yellow%Unclaim automatique activé !");
			}
		}
	}
}