package com.lelann.factions.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

import com.lelann.factions.FactionObject;
import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionChunk;
import com.lelann.factions.api.managers.ChunksManager;

public class InfiniteAnvilListener extends FactionObject implements Listener {
	private Map<UUID, Block> usedAnvils = new HashMap<>();
	
	@EventHandler
	public void onUseAnvil(PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ANVIL){
			ChunksManager cm = getChunksManager(e.getClickedBlock().getChunk().getWorld());
			if(cm == null) return;

			FactionChunk fChunk = cm.getFactionChunk(e.getClickedBlock().getChunk());
			if(fChunk == null) return;

			Faction faction = fChunk.getOwner();
			if(fChunk == null || faction == null) return;
			
			if(faction.isSafezone()){
				usedAnvils.put(e.getPlayer().getUniqueId(), e.getClickedBlock());
			}
		}
	}
	
	@EventHandler
	public void onCloseAnvil(InventoryCloseEvent e){
		if(e.getPlayer().getType() == EntityType.PLAYER && e.getInventory().getType() == InventoryType.ANVIL){
			Player p = (Player) e.getPlayer();
			
			if(usedAnvils.containsKey(p.getUniqueId())){
				Block b = usedAnvils.get(p.getUniqueId());
				
				b.setType(Material.AIR);
				b.setType(Material.ANVIL);
				
				usedAnvils.remove(p.getUniqueId());
			}
		}
	}
}
