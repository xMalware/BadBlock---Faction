package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.utils.MathsUtils;

public class Power extends AbstractCommand {
	public Power() {
		super("power", "faction.play.power", "%gold%/factions power %red%player", "%gold%Récupère la power du joueur %player%", "/f power <player>", null);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			sendMessage(sender, NO_CONSOLE);
			return;
		}
		
		FactionPlayer player = null;
		if(args.length == 0){
			player = getPlayersManager().getPlayer(sender);
		} else player = getPlayersManager().getPlayer(args[0]);
		
		if(player == null){
			sendMessage(sender, "%red%Joueur introuvable !");
		} else {
			sendMessage(sender, "%red%" + player.getLastUsername() + "%yellow% a une power de %aqua%" + MathsUtils.round(player.getPower(), 2) + "%darkaqua%/%aqua%" + FactionConfiguration.getInstance().getMaxPower() + " %yellow%!");
		}
	}
}