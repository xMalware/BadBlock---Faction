package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;

public class Create extends AbstractCommand {

	public Create() {
		super("create", "faction.play.create", "%gold%/factions create %red%<nom>", "%gold%Cr�er une nouvelle %red%faction %gold%de nom %red%<nom>", "/f create <nom>", null);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			sendMessage(sender, NO_CONSOLE);
			return;
		}
		
		FactionPlayer player = getPlayersManager().getPlayer(sender);
		Faction faction = player.getFaction();
		
		if(!faction.isDefault()){
			sendMessage(sender, "%red%Vous avez d�j� une faction, impossible d'en cr�er une nouvelle. /f leave pour sortir de votre faction actuelle.");
		} else if(args.length < 1){
			sendHelp(sender);
		} else if(args[0].length() > 10){
			sendMessage(sender, "%red%Votre nom de faction ne doit pas d�passer 10 caract�res.");
		} else if(!Tag.isValid(args[0])){
			sendMessage(sender, "%red%Le nom de votre faction n'est pas valide. Il ne doit contenir que des lettres/chiffres !");
		} else {
			Faction newFaction = getFactionsManager().getFaction(args[0]);
			if(newFaction != null){
				sendMessage(sender, "%red%Ce nom de faction est d�j� prit, dommage ! :)");
			} else {
				getFactionsManager().createFaction(player, args[0]);
				player.save(false);
				broadcast("%red%" + player.getLastUsername() + "%yellow% a cr�� une nouvelle faction : %red%'" + args[0] + "' %yellow%!");
			}
		}
	}
}