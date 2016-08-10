package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.api.FactionPlayer;

public class ResetPower extends AbstractCommand {
	public ResetPower() {
		super("resetpower", "faction.admin.resetpower", "%gold%/factions resetpower %red%player", "%gold%Remet la power du joueur %red%player %gold%au maximum.", "/f resetpower <player>", null);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		if(args.length >= 1){
			FactionPlayer fp = getPlayersManager().getPlayer(args[0]);
			if(fp == null){
				sendMessage(sender, "%red%Joueur introuvable !");
			} else {
				fp.addPower(FactionConfiguration.getInstance().getMaxPower());
				fp.save(false);
				sendMessage(sender, "%yellow%Power reset !");
			}
		} else sendHelp(sender);
	}
}