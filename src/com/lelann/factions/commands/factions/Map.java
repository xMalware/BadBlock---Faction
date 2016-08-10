package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionMap;
import com.lelann.factions.api.FactionPlayer;

public class Map extends AbstractCommand {
	public Map() {
		super("map", "faction.play.map", "%gold%/factions map", "%gold%Active ou d�sactive la %red%carte %gold%des chunks autour de vous", "/f map", null);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			sendMessage(sender, NO_CONSOLE);
			return;
		}

		FactionPlayer player = getPlayersManager().getPlayer(sender);
		Faction faction = player.getFaction();

		if(player.isMap()){
			player.setMap(false);
			sendMessage(sender, "%yellow%Carte d�sactiv�e !");
		} else if(!player.isMap()){
			player.setMap(true);
			sendMessage(sender, "%yellow%Carte activ�e !");
			new FactionMap(player.getPlayer().getLocation().getChunk(), faction).send(player.getPlayer());
		}
	}
}