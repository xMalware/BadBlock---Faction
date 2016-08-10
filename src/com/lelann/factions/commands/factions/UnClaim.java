package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionChunk;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.managers.ChunksManager;

public class UnClaim extends AbstractCommand {
	public UnClaim() {
		super("unclaim", "faction.play.unclaim", "%gold%/factions unclaim", "%gold%Enlève la protection du %red%chunk %gold%sur lequel vous vous trouvez", "/f unclaim", null);
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
			sendMessage(sender, "%red%Vous devez être modérateur pour unclaim un chunk.");
		} else {
			ChunksManager cm = getChunksManager(player.getPlayer().getLocation().getWorld());
			if(cm == null){
				sendMessage(sender, "%red%Le chunk ne vous appartient pas !");
			} else {
				FactionChunk chunk = cm.getFactionChunk(player.getPlayer().getLocation().getChunk());
				Faction owner = null;
				if(chunk != null) owner = chunk.getOwner();

				if(owner == null || owner.isDefault() || !owner.equals(faction)){
					sendMessage(sender, "%red%Le chunk ne vous appartient pas !");
				} else {
					if(chunk.isAp()){
						faction.sendMessage(PREFIX + "%red%" + sender.getName() + " %yellow%a unclaim un AP !");
						faction.setApChunkNumber(faction.getApChunkNumber() - 1);
					} else faction.sendMessage(PREFIX + "%red%" + sender.getName() + " %yellow%a unclaim un chunk !");
					
					faction.setChunkNumber(faction.getChunkNumber() - 1);
					chunk.setFactionId(Faction.WILDERNESS.getFactionId()); chunk.save(false);
				
					if(faction.getHome() != null && player.getPlayer().getLocation().getChunk().equals(faction.getHome().getChunk())){
						faction.setHome(null);
						faction.save(false);
						faction.sendMessage(PREFIX + "%red%La home de la faction se trouvait sur le chunk, celle-ci est perdue !");
					}
				}
			}
		}
	}
}