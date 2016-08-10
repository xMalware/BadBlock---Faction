package com.lelann.factions.listeners;

import static com.lelann.factions.listeners.AntiDupliUtils.cleanBlockInventory;
import static com.lelann.factions.listeners.AntiDupliUtils.cleanInventory;
import static com.lelann.factions.listeners.AntiDupliUtils.logDupli;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.lelann.factions.Main;

public class AntiDupliListener implements Listener{
	public AntiDupliListener(File dataFolder){}

	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK 
				&& e.getClickedBlock().getType() == Material.MOB_SPAWNER 
				&& !e.getPlayer().isOp())
		{
			e.setCancelled(true); return;
		}

		if(e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof InventoryHolder){
			InventoryHolder holder = (InventoryHolder) e.getClickedBlock().getState();
			Inventory		inv	   = holder.getInventory();

			String log = "Coffre X=" + e.getClickedBlock().getX() + " - Y=" + e.getClickedBlock().getY() + " - Z=" + e.getClickedBlock().getZ();
			cleanInventory(inv, e.getPlayer().getName(), log, 1);
		}
		
		if(e.getItem() == null) return;
		if(e.getItem().getAmount() <= 0){
			e.getItem().setAmount(1);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		cleanInventory(e.getPlayer().getInventory(),  e.getPlayer().getName(), "Inventaire du joueur", 1);
		cleanInventory(e.getPlayer().getEnderChest(), e.getPlayer().getName(), "EnderChest du joueur", 3);
	
		e.getPlayer().updateInventory();
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e){
		cleanInventory(e.getPlayer().getInventory(), e.getPlayer().getName(), "Inventaire du joueur (" + e.getInventory().getName() + ")", 1);
		cleanInventory(e.getInventory(), e.getPlayer().getName(), "Inventaire - " + e.getInventory().getName(), e.getInventory().getType() == InventoryType.ENDER_CHEST ? 2 : 1);
	
		if((e.getPlayer().getType() != EntityType.PLAYER)) return;
		
		UUID player = ((Player) e.getPlayer()).getUniqueId();
		
		new BukkitRunnable(){
			@Override
			public void run(){
				Player p = Bukkit.getPlayer(player);
			
				if(p != null){
					cleanInventory(p.getInventory(), e.getPlayer().getName(), "Inventaire du joueur", 1);
				}
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}
	
	@EventHandler
	public void onExplode(BlockExplodeEvent e){
		for(Block block : e.blockList()){
			if(block != null && block.getState() instanceof InventoryHolder){
				InventoryHolder holder = (InventoryHolder) block.getState();
				Inventory		inv	   = holder.getInventory();

				String log = "Coffre X=" + block.getX() + " - Y=" + block.getY() + " - Z=" + block.getZ();
				cleanInventory(inv, "Inconnu (explosion)", log, 1);
			}
		}
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent e){
		for(Block block : e.blockList()){
			if(block != null && block.getState() instanceof InventoryHolder){
				InventoryHolder holder = (InventoryHolder) block.getState();
				Inventory		inv	   = holder.getInventory();

				String log = "Coffre X=" + block.getX() + " - Y=" + block.getY() + " - Z=" + block.getZ();
				cleanInventory(inv, "Inconnu (explosion)", log, 1);
			}
		}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e){
		cleanInventory(e.getPlayer().getInventory(), e.getPlayer().getName(), "Inventaire du joueur", 1);
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e){
		if(e.getItem().getItemStack().getAmount() <= 0){
			e.getItem().getItemStack().setAmount(1);
		}
	}

	@EventHandler
	public void onSpawn(ItemSpawnEvent e){
		if(e.getEntity().getItemStack().getAmount() <= 0){
			e.getEntity().getItemStack().setAmount(1);
		}
	}

	@EventHandler
	public void onMoveItemInInventory(InventoryMoveItemEvent e){
		/*String who   = "Inconnu (bloc)";
		String where = null;
		
		if(e.getSource().getType() == InventoryType.PLAYER){
			who = ((PlayerInventory) e.getSource()).getHolder().getName();
		} else if(e.getDestination().getType() == InventoryType.PLAYER){
			who = ((PlayerInventory) e.getDestination()).getHolder().getName();
		}
		
		String wT = cleanBlockInventory(who, e.getSource());
		if(wT != null) where = wT;
		wT = cleanBlockInventory(who, e.getDestination());
		if(wT != null) where = wT;
		
		if(where == null)
			where = "Inconnu (bloc)";
		
		if(e.getItem().getAmount() <= 0){
			ItemStack is = e.getItem();
			is.setAmount(1);
			e.setItem(is);
			
			logDupli(who, where, "Items 'vides' x" + 1);
		}*/
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e){
		if(e.getItemDrop().getItemStack().getAmount() <= 0){
			e.getItemDrop().getItemStack().setAmount(1);
		}
	}

	@EventHandler
	public void onPickup(InventoryPickupItemEvent e){
		String who = "Inconnu (block)";
		
		if(e.getInventory().getType() == InventoryType.PLAYER){
			who = ((PlayerInventory) e.getInventory()).getHolder().getName();
		}
		
		String where = cleanBlockInventory(who, e.getInventory());
		
		if(e.getItem().getItemStack().getAmount() <= 0){
			e.getItem().getItemStack().setAmount(1);
			logDupli(who, where == null ? "Inconnu (block)" : where, "Items 'vides' x" + 1);
		}
	}

	@EventHandler
	public void onDispense(BlockDispenseEvent  e){
		if(e.getItem().getAmount() <= 0){
			e.getItem().setAmount(1);
		}
	}
}
