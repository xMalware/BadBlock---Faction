package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionPlayer;

public class UnClaimAll extends AbstractCommand {
	public UnClaimAll() {
		super("unclaimall", "faction.play.unclaimall", "%gold%/factions unclaimall", "%gold%Enlève la protection de tous les %red%chunks %gold%claim par la faction", "/f unclaimall", null);
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
			sendMessage(sender, "%red%Vous devez être chef pour unclaim tous les chunks.");
		} else {
			faction.unclaimAll();
			faction.setApChunkNumber(0); faction.setChunkNumber(0);
			faction.setHome(null); faction.save(false);
			faction.sendMessage(PREFIX + "%red%" + sender.getName() + " %yellow%a unclaim tous les chunks de la faction !");
		}
	}
}