package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.api.Faction;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionChunk;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.FactionRelationship.FactionRelationshipType;
import com.lelann.factions.api.managers.ChunksManager;

public class Claim extends AbstractCommand {
	public Claim() {
		super("claim", "faction.play.claim", "%gold%/factions claim", "%gold%Protège le %red%chunk %gold%sur lequel vous vous trouvez des autres factions", "/f claim", null);
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
			sendMessage(sender, "%red%Vous devez être modérateur pour claim un chunk.");
		} else if(faction.getPower() < faction.getChunkNumber() + 1){
			sendMessage(sender, "%red%Vous n'avez pas assez de power pour claim un autre chunk !");
		} else {
			ChunksManager cm = getChunksManager(player.getPlayer().getLocation().getWorld());
			if(cm == null){
				sendMessage(sender, "%red%Vous ne pouvez pas claim dans ce monde !");
			} else {
				FactionChunk chunk = cm.getFactionChunk(player.getPlayer().getLocation().getChunk());
				Faction owner = null;
				if(chunk != null) owner = chunk.getOwner();
				
				if(owner == null || (owner.isWilderness() && !chunk.isAp())){
					if(owner == null){
						chunk = cm.claim(faction, player.getPlayer().getLocation().getChunk());
					} else {
						chunk.setFactionId(faction.getFactionId());
						chunk.getAllowedMembers().clear();
						chunk.save(false);
					}
					faction.sendMessage("%red%" + sender.getName() + " %yellow%a claim un chunk !");
					faction.setChunkNumber(faction.getChunkNumber() + 1);

					faction.updateScoreboard();
					
					faction.save(false); chunk.save(false);
				} else if(owner.isWilderness() && chunk.isAp()){
					if(FactionConfiguration.getInstance().allowedApNumber(faction.getPlayers().size()) <= faction.getApChunkNumber()){
						sendMessage(sender, "%red%Vous avez déjà atteind la limite d'AP pour votre nombre de joueurs !");
					} else {
						chunk.setFactionId(faction.getFactionId());
						chunk.getAllowedMembers().clear();
						faction.sendMessage("%red%" + sender.getName() + " %yellow%a claim un AP !");
						faction.setChunkNumber(faction.getChunkNumber() + 1);
						faction.setApChunkNumber(faction.getApChunkNumber() + 1);
						
						faction.updateScoreboard();
						
						faction.save(false); chunk.save(false);
					}
				} else if(owner.isWarzone() || owner.isSafezone()){
					sendMessage(sender, "%red%Vous n'êtes pas autorisé à claim en " + owner.getName() + "!");
				} else if(owner.equals(faction)){
					sendMessage(sender, "%red%Ce chunk appartient déjà à votre faction !");
				} else {
					FactionRelationshipType type = getFactionsManager().getRelationship(faction, owner);
					if(type != FactionRelationshipType.ENEMY){
						sendMessage(sender, "%red%Vous ne pouvez surclaim que vos ennemis !");
					} else if(owner.getPower() < owner.getChunkNumber()){
						chunk.getAllowedMembers().clear();
						chunk.setFactionId(faction.getFactionId());

						if(chunk.isAp()){
							owner.setApChunkNumber(owner.getApChunkNumber() - 1);
							faction.setApChunkNumber(faction.getApChunkNumber() + 1);
						}
						owner.setChunkNumber(owner.getChunkNumber() - 1);
						faction.setChunkNumber(faction.getChunkNumber() + 1);

						owner.sendMessage(PREFIX + "%red%" + faction.getName() + " %yellow%vous a surclaim un chunk !");
						faction.sendMessage(PREFIX + "%red%" + sender.getName() + " %yellow%a surclaim un " + (chunk.isAp() ? "AP" : "chunk") + " de %red%" + owner.getName() + " %yellow%!");

						faction.updateScoreboard();
						owner.updateScoreboard();
						
						faction.save(false); owner.save(false); chunk.save(false);
					} else {
						sendMessage(sender, "%red%Cette faction n'est pas en sous-power, vous ne pouvez pas la surclaim.");
					}
				}
			}
		}
	}
}