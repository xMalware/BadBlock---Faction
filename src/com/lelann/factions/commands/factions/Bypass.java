package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.FactionPlayer;

public class Bypass extends AbstractCommand {
	public Bypass() {
		super("bypass", "faction.admin.bypass", "%gold%/factions bypass", "%gold%Active ou désactive le %red%bypass", "/f bypass", null);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			sendMessage(sender, NO_CONSOLE);
			return;
		}

		FactionPlayer player = getPlayersManager().getPlayer(sender);

		if(player.isBypass()){
			player.setBypass(false);
			sendMessage(sender, "%yellow%Bypass désactivée !");
		} else if(!player.isBypass()){
			player.setBypass(true);
			sendMessage(sender, "%yellow%Bypass activée !");
		}
	}
}