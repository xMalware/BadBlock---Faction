package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionChunk;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.managers.ChunksManager;

public class Ap extends AbstractCommand {
	public Ap() {
		super("ap", "faction.play.ap", "%gold%/factions ap give|accept <faction>", "%gold%Commandes pour manager les APs.", "/f ap ", null);
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
			sendMessage(sender, "%red%Vous devez être modérateur pour gérer les APs.");
		} else if(args.length < 2){
			sendHelp(sender);
		} else {
			ChunksManager cm = getChunksManager(player.getPlayer().getLocation().getWorld());
			if(cm == null){
				sendMessage(sender, "%red%Vous ne pouvez pas claim dans ce monde !");
			} else {
				FactionChunk chunk = cm.getFactionChunk(player.getPlayer().getLocation().getChunk());
				Faction owner = null;
				if(chunk != null) owner = chunk.getOwner();

				Faction other = getFactionsManager().getFaction(args[1]);

				if(other == null){
					sendMessage(sender, "%red%Faction introuvable !"); return;
				}

				if(args[0].equalsIgnoreCase("give")){
					if(owner == null || owner.getFactionId() != faction.getFactionId() || !chunk.isAp()){
						sendMessage(sender, "%red%Ce chunk n'est pas un de vos APs !");
					} else {
						other.getChunksChunk().put(faction.getFactionId(), chunk);

						other.sendMessage(PREFIX + "%red%" + owner.getName() + " %yellow%vous propose d'obtenir leur AP en (" + chunk.getX() + ";" + chunk.getZ() + "). /f ap accept " + owner.getName() + " pour accepter.");
						owner.sendMessage(PREFIX + "%red%" + sender.getName() + " %yellow%propore à %red%" + other.getName() + " %yellow%d'obtenir votre AP en (" + chunk.getX() + ";" + chunk.getZ() + ").");
					}
				} else if(args[0].equalsIgnoreCase("accept")){
					FactionChunk c = faction.getChunksChunk().get(other.getFactionId());
					if(c == null){
						sendMessage(sender, "%red%Cette faction ne vous a pas proposé son AP !"); return;
					}

					c.getAllowedMembers().clear();
					c.setFactionId(faction.getFactionId());

					other.setChunkNumber(other.getChunkNumber() - 1);
					other.setApChunkNumber(other.getApChunkNumber() - 1);
					faction.setApChunkNumber(faction.getApChunkNumber() + 1);
					faction.setChunkNumber(faction.getChunkNumber() + 1);

					other.updateScoreboard();
					faction.updateScoreboard();
					
					other.sendMessage(PREFIX + "%red%" + faction.getName() + "%yellow% a bien reçu votre AP !");
					faction.sendMessage(PREFIX + "%yellow%Vous avez bien reçu l'AP de %red%" + owner.getName() + "%yellow% !");


					faction.getChunksChunk().remove(other.getFactionId());

					c.save(false); other.save(false); faction.save(false);
				}
			}
		}
	}
}