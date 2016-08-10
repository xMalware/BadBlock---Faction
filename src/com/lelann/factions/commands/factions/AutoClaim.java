package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionPlayer;

public class AutoClaim extends AbstractCommand {
	public AutoClaim() {
		super("autoclaim", "faction.play.autoclaim", "%gold%/factions autoclaim", "%gold%Protège automatiquement tous les %red%chunks %gold%sur lesquels vous allez", "/f autoclaim", null);
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
				sendMessage(sender, "%yellow%Claim automatique désactivé !");
			} else {
				player.setAutoclaim(true);
				player.setAutounclaim(false);
				CommandsManager.getInstance().useCommand(sender, new String[]{"claim"});
				sendMessage(sender, "%yellow%Claim automatique activé !");
			}
		}
	}
}