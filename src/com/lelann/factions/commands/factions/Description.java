package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.utils.ChatUtils;
import com.lelann.factions.utils.StringUtils;

public class Description extends AbstractCommand {
	public Description() {
		super("description", "faction.play.description", "%gold%/factions desc %red%description", "%gold%Change la description de votre faction par %red%description", "/f desc <description>", null);
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
			sendMessage(sender, "%red%Vous devez être modérateur pour changer la description de la faction.");
		} else if(args.length < 1){
			sendHelp(sender);
		} else {
			faction.setDescription(ChatUtils.colorDelete(StringUtils.join(args, " ")));
			faction.save(false);
			sendMessage(sender, "%yellow%Description changée !");
		}
	}
}