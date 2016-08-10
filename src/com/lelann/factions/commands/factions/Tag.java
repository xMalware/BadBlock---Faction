package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.Faction.FactionRank;

public class Tag extends AbstractCommand {
	public Tag() {
		super("tag", "faction.play.tag", "%gold%/factions tag %red%tag", "%gold%Change le nom de votre faction par %red%tag", "/f tag <tag>", null);
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
			sendMessage(sender, "%red%Vous devez être modérateur pour changer le nom de la faction.");
		} else if(args.length < 1){
			sendHelp(sender);
		} else if(args[0].length() > 10){
			sendMessage(sender, "%red%Votre nom de faction ne doit pas dépasser 10 caractères.");
		} else if(!isValid(args[0])){
			sendMessage(sender, "%red%Le nom de votre faction n'est pas valide. Il ne doit contenir que des lettres/chiffres !");
		} else if(getFactionsManager().getFaction(args[0]) != null){
			sendMessage(sender, "%red%Ce nom est déjà prit.");
		} else {
			getFactionsManager().changeTag(faction.getFactionId(), args[0]);
			faction.setName(args[0]);
			faction.save(false);
			faction.sendMessage(PREFIX + "La faction s'appelle maintenant %red%" + args[0] + "%yellow% !");
		}
	}
	
	public static boolean isValid(String tag){
		for(char c : tag.toCharArray()){
			if(!Character.isAlphabetic(c) && !Character.isDigit(c)){
				return false;
			}
		}
		return true;
	}
}