package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.managers.ChunksManager;

public class SetHome extends AbstractCommand {

	public SetHome() {
		super("sethome", "faction.play.sethome", "%gold%/factions sethome", "%gold%Définit le %red%home %gold%de votre faction", "/f sethome", null);
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
			sendMessage(sender, "%red%Vous devez être modérateur pour définir la home de votre faction.");
		} else {
			ChunksManager manager = getChunksManager(player.getPlayer().getLocation().getWorld());
			boolean owner = false;
			if(manager != null){
				owner = manager.getFactionAt(player.getPlayer()).equals(faction);
			}
			if(!owner){
				sendMessage(sender, "%red%Vous ne pouvez définir votre home de faction que sur un territoire claim !");
			} else {
				faction.setHome(player.getPlayer().getLocation());
				faction.save(false);
				sendMessage(sender, "%yellow%Home de votre faction définie !");
			}
		}
	}
}