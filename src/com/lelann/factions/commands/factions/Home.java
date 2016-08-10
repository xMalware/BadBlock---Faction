package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.runnables.TeleportRunnable;

public class Home extends AbstractCommand {

	public Home() {
		super("home", "faction.play.home", "%gold%/factions home", "%gold%T�l�porte � votre %red%home %gold%de faction", "/f home", null);
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
		} else if(faction.getHome() == null){
			sendMessage(sender, "%red%Votre faction doit d�finir une home de faction (/f sethome) avant que vous puissiez vous y t�l�porter !");
		} else {
			new TeleportRunnable(player.getPlayer(), faction.getHome(), FactionConfiguration.getInstance().getTimeBeforeTeleportation()).start();
		}
	}
}
