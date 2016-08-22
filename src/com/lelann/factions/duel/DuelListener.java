package com.lelann.factions.duel;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.Essentials;
import com.lelann.factions.Main;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.duel.Duel.DuelState;
import com.lelann.factions.duel.Duels.PreDuel;
import com.lelann.factions.utils.ChatUtils;

public class DuelListener implements Listener {
	
	@EventHandler
	public void onDamageWorld(EntityDamageEvent e){
		if(!e.getEntity().getWorld().getName().equalsIgnoreCase("duel")){
			return;
		}

		FactionPlayer player = Main.getInstance().getPlayersManager().getPlayer(e.getEntity());
		if(player == null) return;

		Duel duel = Duels.getInstance().getDuelsByPlayer().get(player.getUniqueId());
		if(duel == null)
			e.setCancelled(true);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		if(event.getPlayer().getWorld().getName().equalsIgnoreCase("duel")){
			if (event.getAction() != Action.LEFT_CLICK_AIR) 
				event.setCancelled(true);
		}
		
		Duel duel = Duels.getInstance().getDuelsByPlayer().get(event.getPlayer().getUniqueId());

		if(duel != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && duel.getState() != DuelState.ENDED){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e){
		Duel duel = Duels.getInstance().getDuelsByPlayer().get(e.getPlayer().getUniqueId());

		if(duel != null || e.getPlayer().getWorld().getName().equalsIgnoreCase("duel")){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent e){
		Duel duel = Duels.getInstance().getDuelsByPlayer().get(e.getPlayer().getUniqueId());

		if(duel != null && !duel.isKeepStuff()) return;
		if(duel != null || e.getPlayer().getWorld().getName().equalsIgnoreCase("duel")){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		Duel duel = Duels.getInstance().getDuelsByPlayer().get(e.getPlayer().getUniqueId());

		if(duel != null){
			e.getPlayer().getOpenInventory().getTopInventory().clear();
			e.getPlayer().setItemOnCursor(new ItemStack(Material.AIR));

			if(e.getPlayer().getOpenInventory() != null && e.getPlayer().getOpenInventory().getTopInventory() != null){
				e.getPlayer().getOpenInventory().getTopInventory().clear();
			}

			e.getPlayer().closeInventory();

			Player p = duel.getOtherPlayer(e.getPlayer().getUniqueId());

			if(duel.isKeepStuff()) {
				p.getOpenInventory().getTopInventory().clear();
				p.setItemOnCursor(new ItemStack(Material.AIR));

				if(p.getOpenInventory() != null && p.getOpenInventory().getTopInventory() != null){
					p.getOpenInventory().getTopInventory().clear();
				}

				p.closeInventory();
				p.updateInventory();
			} 

			if(p != null)
				duel.win(p, e.getPlayer());
			duel.loose(e.getPlayer());

			duel.endDuel(e.getPlayer());
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e){
		if(e.getEntityType() == EntityType.PLAYER && e.getCause() != DamageCause.ENTITY_ATTACK){
			Duel duel = Duels.getInstance().getDuelsByPlayer().get(((Player) e.getEntity()).getUniqueId());
			if(duel != null && duel.getState() != DuelState.BEFORE_TELEPORTATION && duel.getState() != DuelState.FIGHT){
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		Duel duel = Duels.getInstance().getDuelsByPlayer().get(e.getEntity().getUniqueId());

		if(duel != null){
			e.getEntity().getOpenInventory().getTopInventory().clear();
			e.getEntity().setItemOnCursor(new ItemStack(Material.AIR));

			if(e.getEntity().getOpenInventory() != null && e.getEntity().getOpenInventory().getTopInventory() != null){
				e.getEntity().getOpenInventory().getTopInventory().clear();
			}

			e.getEntity().closeInventory();

			new BukkitRunnable(){
				@Override
				public void run(){
					e.getEntity().spigot().respawn();
				}
			}.runTaskLater(Main.getInstance(), 1L);

			if(duel.getState() != DuelState.FIGHT){
				return;
			}

			if(duel.isKeepStuff()){
				e.getDrops().clear();
				e.setDroppedExp(0);
			}

			Player p = duel.getOtherPlayer(e.getEntity().getUniqueId());

			if(duel.isKeepStuff()) {
				p.getOpenInventory().getTopInventory().clear();
				p.setItemOnCursor(new ItemStack(Material.AIR));

				if(p.getOpenInventory() != null && p.getOpenInventory().getTopInventory() != null){
					p.getOpenInventory().getTopInventory().clear();
				}

				p.closeInventory();
				p.updateInventory();
			}

			duel.win(p, e.getEntity());
			duel.loose(e.getEntity());

			duel.endDuel(null);
		}
	}

	@EventHandler
	public void onCommandPrepocess(PlayerCommandPreprocessEvent e) {
		if(e.getPlayer().getWorld().getName().equalsIgnoreCase("duel") && !e.getMessage().equalsIgnoreCase("/spawn")){
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cVous ne pouvez pas exécuter de commande dans une zone de duel. Faîtes /spawn.");
		}
		
		Duel duel = Duels.getInstance().getDuelsByPlayer().get(e.getPlayer().getUniqueId());
		
		if(duel != null && !e.getPlayer().isOp() && duel.getState() != DuelState.ENDED){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		DuelStuff stuff = Duels.getInstance().getKeepedStuff(e.getPlayer().getUniqueId());

		if(stuff != null){
			stuff.give(e.getPlayer());
			e.getPlayer().teleport(stuff.getLocation());

			new BukkitRunnable(){
				@Override
				public void run(){
					Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
					ess.getUser(e.getPlayer()).setLastLocation(stuff.getLocation());
				}
			}.runTaskLater(Main.getInstance(), 1L);
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent e){
		DuelStuff stuff = Duels.getInstance().getKeepedStuff(e.getPlayer().getUniqueId());

		if(stuff != null){
			e.setRespawnLocation(stuff.getLocation());

			new BukkitRunnable(){
				@Override
				public void run(){
					Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
					ess.getUser(e.getPlayer()).setLastLocation(stuff.getLocation());

					stuff.give(e.getPlayer());
				}
			}.runTaskLater(Main.getInstance(), 1L);
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e){
		if(e.getWhoClicked().getType() != EntityType.PLAYER
				|| e.getClickedInventory() == null
				|| e.getClickedInventory().getType() == InventoryType.PLAYER
				|| e.getClickedInventory().getItem(e.getSlot()) == null
				|| e.getClickedInventory().getItem(e.getSlot()).getType() == Material.AIR) return;

		Player p = (Player) e.getWhoClicked();

		PreDuel duel = Duels.getInstance().getPreparing().get(p.getUniqueId());
		if(duel == null) return;

		e.setCancelled(true);

		duel.setKeepStuff(e.getSlot() == 0 || e.getSlot() == 1);

		if(Bukkit.getPlayer(duel.getTo()) == null){
			ChatUtils.sendMessage(p, "&cVotre partenaire c'est déconnecté, duel impossible !");
		} else {
			Player to = Bukkit.getPlayer(duel.getTo());

			ChatUtils.sendMessage(to, "&8&l«&b&l-&8&l»&m---------&f&8&l«&b&l-&8&l»&b &b&lDuel &8&l«&b&l-&8&l»&m---------&f&8&l«&b&l-&8&l»");
			if(duel.isKeepStuff()){
				ChatUtils.sendMessage(to, "%red%" + p.getDisplayName() + "%gold% vous propose un &aduel amical %gold%! (pas de perte de stuff ou power)");
			} else {
				ChatUtils.sendMessage(to, "%red%" + p.getDisplayName() + "%gold% vous propose un &cduel à mort %gold%! (pas de perte de power)");
			}

			ChatUtils.sendMessage(to, "&4/&cduel accept " + p.getName() + " %gold%pour accepter !");
			ChatUtils.sendMessage(to, "&8&l«&b&l-&8&l»&m-----------------------------&f&8&l«&b&l-&8&l»&b");

			ChatUtils.sendMessage(p, "&aLa demande à bien été envoyée ! :)");

			Duels.getInstance().getPropositions().put(p.getUniqueId(), duel);

			new BukkitRunnable(){
				@Override
				public void run(){
					if(duel.equals(Duels.getInstance().getPropositions().get(p.getUniqueId()))){
						Duels.getInstance().getPropositions().remove(p.getUniqueId());
					}
				}
			}.runTaskLater(Main.getInstance(), 120 * 20L);
		}

		p.closeInventory();
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e){
		if(e.getPlayer().getType() != EntityType.PLAYER) return;

		Player p = (Player) e.getPlayer();

		PreDuel duel = Duels.getInstance().getPreparing().get(p.getUniqueId());
		if(duel == null) return;

		Duels.getInstance().getPreparing().remove(p.getUniqueId());
	}
}
