package com.lelann.factions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.FactionObject;
import com.lelann.factions.Main;
import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionChunk;
import com.lelann.factions.api.FactionMap;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.managers.ChunksManager;
import com.lelann.factions.commands.factions.CommandsManager;
import com.lelann.factions.utils.ChatUtils;
import com.lelann.factions.utils.Title;

public class MoveListener extends FactionObject implements Listener {
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if(!e.getFrom().getChunk().equals(e.getTo().getChunk())){
			changeChunk(e.getPlayer(), e.getFrom().getChunk(), e.getTo().getChunk());
		}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e){
		Faction faction = null;
		if(!e.getFrom().getChunk().equals(e.getTo().getChunk())){
			faction = changeChunk(e.getPlayer(), e.getFrom().getChunk(), e.getTo().getChunk());
			if(faction == null) return;
			if(e.getCause() == TeleportCause.ENDER_PEARL && faction.isSafezone()){
				ChatUtils.sendMessage(e.getPlayer(), "%red%Vous ne pouvez pas utiliser d'EnderPeal dans la SafeZone !");
				e.setCancelled(true);
			}
		}
		
		if(e.isCancelled()) return;
		for(Player p : Bukkit.getOnlinePlayers()){
			if(p.getLocation().getWorld().equals(e.getTo().getWorld())){
				if(p.getLocation().distance(e.getTo()) < 3.0d && p.getGameMode() == GameMode.SPECTATOR){
					if(faction != null && !faction.isSafezone() && !faction.isWarzone()){
						e.setCancelled(true);
						ChatUtils.sendMessage(e.getPlayer(), "%red%Il est interdit de se téléporter à proximité d'un spectateur !");
					}
				}
			}
		}
	}
	
	public Faction changeChunk(Player player, Chunk from, Chunk to){
		ChunksManager fromManager = getChunksManager(from.getWorld()),
				toManager = getChunksManager(to.getWorld());
		FactionPlayer fplayer = getPlayersManager().getPlayer(player);
		Faction playerFaction = fplayer.getFaction();

		if(fplayer.isAutoclaim()){
			CommandsManager.getInstance().useCommand(player, new String[]{"claim"});
		} else if(fplayer.isAutounclaim()){
			CommandsManager.getInstance().useCommand(player, new String[]{"unclaim"});
		} else if(fplayer.isMap()){
			new FactionMap(player.getPlayer().getLocation().getChunk(), playerFaction).send(player.getPlayer());
		}
		
		if(toManager == null) return null;
		
		
		boolean isAp = false, wasAp = false;
		FactionChunk chunkTo = toManager.getFactionChunk(to), chunkFrom = fromManager == null ? null : fromManager.getFactionChunk(from);
		Faction factionTo = null;
		
		Faction factionFrom = null;

		if(chunkTo != null){
			isAp = chunkTo.isAp();
			factionTo = chunkTo.getOwner();
		} else factionTo = Faction.WILDERNESS;
		
		if(chunkFrom != null){
			wasAp = chunkFrom.isAp();
			factionFrom = chunkFrom.getOwner();
		} else factionFrom = Faction.WILDERNESS;

		int fromId = 0;
		if(factionFrom != null) fromId = factionFrom.getFactionId();
		
		if(factionTo == null || chunkTo == null) return Faction.WILDERNESS;
		
		if(fromId != factionTo.getFactionId() || (chunkTo != null && chunkTo.isAp()) || (!isAp && wasAp)){
			String color = color(playerFaction, factionTo);
			
			if(factionTo.isWilderness() && isAp){
				color = FactionConfiguration.getInstance().getApFactionColor();
				new Title(color + FactionConfiguration.getInstance().getApFactionName(), color + FactionConfiguration.getInstance().getApFactionDescription(), 10, 60, 10).send(player);
			} else new Title(color + factionTo.getName(), color + factionTo.getDescription(), 10, 60, 10).send(player);
		}
		
		return factionTo;
	}
	
	public static String color(Faction playerFaction, Faction factionTo){
		String color = "";
		if(playerFaction.isDefault()) color = Faction.defaultColor(factionTo);
		else if(factionTo.isDefault()) color = Faction.defaultColor(factionTo);
		else if(factionTo.equals(playerFaction)) color = FactionConfiguration.getInstance().getSameColor();
		else color = Main.getInstance().getFactionsManager().getRelationship(playerFaction, factionTo).getColor();
		
		return color;
	}
}
