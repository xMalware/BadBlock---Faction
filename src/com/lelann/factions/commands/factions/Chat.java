package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.FactionPlayer.FactionChat;

public class Chat extends AbstractCommand {
	public Chat() {
		super("chat", "faction.play.chat", "%gold%/factions chat %red%type (ally, faction, public)", "%gold%Change la portée de vos messages (publique, à votre faction ou à vos alliés)", "/f c a/f/p", null);
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
		} else if(args.length < 1){
			sendHelp(sender);
		} else {
			FactionChat chat = FactionChat.matchChat(args[0]);
			if(chat == null)
				sendHelp(sender);
			else {
				sendMessage(sender, "%yellow%Votre chat est maintenant en mode %red%" + chat + "%yellow% !");
				player.setChat(chat);
			}
		}
	}
}