package com.lelann.factions.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.FactionObject;
import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionChunk;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.FactionRelationship.FactionRelationshipType;
import com.lelann.factions.api.managers.ChunksManager;
import com.lelann.factions.commands.factions.AbstractCommand;
import com.lelann.factions.duel.Duel;
import com.lelann.factions.duel.Duel.DuelState;
import com.lelann.factions.duel.Duels;
import com.lelann.factions.utils.ChatUtils;
import com.lelann.factions.utils.CreatureType;

public class FightListener extends FactionObject implements Listener {
	private List<UUID> playersInFight = new ArrayList<UUID>();
	private Map<String, BukkitTask> tasks = new HashMap<String, BukkitTask>();

	public void fight(final Player p1, final Player p2){
		fightThread(p1);
		fightThread(p2);
	}

	public void fightThread(final Player p){
		inFight(p);
		if(tasks.containsKey(p.getName())){
			tasks.get(p.getName()).cancel();
			tasks.remove(p.getName());
		}
		tasks.put(p.getName(), new BukkitRunnable(){
			public void run(){
				noFight(p);
			}
		}.runTaskLater(getMain(), 20L * FactionConfiguration.getInstance().getFightTime()));
	}

	public void inFight(Player p){
		if(!playersInFight.contains(p.getUniqueId())){
			playersInFight.add(p.getUniqueId());
			p.setFlying(false);
			p.setAllowFlight(false);
			p.removePotionEffect(PotionEffectType.INVISIBILITY);
			ChatUtils.sendMessage(p, FactionConfiguration.getInstance().getBeginFight());
		}
	}

	public void noFight(Player p){
		if(playersInFight.contains(p.getUniqueId())){
			playersInFight.remove(p.getUniqueId());
			ChatUtils.sendMessage(p, FactionConfiguration.getInstance().getEndFight());
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		FactionPlayer player = getPlayersManager().getPlayer(e.getPlayer());
		Faction f = player.getFaction();

		if(f != null && !f.isDefault() && f.getHome() != null)
			e.setRespawnLocation(f.getHome());
	}

	@EventHandler
	public void onKill(EntityDeathEvent e){
		CreatureType type = CreatureType.getByBukkitEntity(e.getEntity());
		
		if(type != null && e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			int value = FactionConfiguration.getInstance().getMobValues().get(type);
			if(value == 0) return;
			
			EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e.getEntity().getLastDamageCause();
			
			
			FactionPlayer p = getPlayersManager().getPlayer(ev.getDamager());
			if(p != null){
				p.addMoney(value);
				p.sendMessage("&aVous avez gagné " + value + "$ en tuant un(e) " + type.getFrenchName() + " !");
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		noFight(e.getEntity());
		FactionPlayer player = getPlayersManager().getPlayer(e.getEntity());

		boolean lostPower = true;
		if(getChunksManager(e.getEntity().getLocation().getWorld()) == null)
			lostPower = false;
		else {
			Faction factionAt = getChunksManager(e.getEntity().getLocation().getWorld()).getFactionAt(e.getEntity());

			if(factionAt.isSafezone() || factionAt.isWarzone()){
				lostPower = false;
			}
		}

		if(lostPower){
			player.removePower(FactionConfiguration.getInstance().getPowerLostByDeath());
			player.save(false);
			player.sendMessage(AbstractCommand.PREFIX, "%red%Vous avez perdu " + FactionConfiguration.getInstance().getPowerLostByDeath() + " de power.");
		} else {
			player.sendMessage(AbstractCommand.PREFIX, "%yellow%Pas de power perdu dans ce monde ou cette zone.");
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(playersInFight.contains(e.getPlayer().getUniqueId())){
			e.getPlayer().setHealth(0.0d);
			ChatUtils.broadcast(FactionConfiguration.getInstance().getDisconnectWhilstFighting().replace("%player%", e.getPlayer().getName()));
		}
	}
	
	@EventHandler
	public void onFight(EntityDamageByEntityEvent e){
		FactionPlayer damagedFP = getPlayersManager().getPlayer(e.getEntity()),
				damagerFP = getPlayersManager().getPlayer(e.getDamager());
		
		if(damagedFP == null || damagerFP == null)
			return;	

		Duel duelP1 = Duels.getInstance().getDuelsByPlayer().get(damagedFP.getUniqueId()),
				duelP2 = Duels.getInstance().getDuelsByPlayer().get(damagerFP.getUniqueId());
		
		if(duelP1 != null && duelP2 != null && duelP1.equals(duelP2)){
			if(duelP1.getState() != DuelState.FIGHT){
				e.setCancelled(true);
				damagerFP.sendMessage("&cCe n'est pas encore le moment ... patiente ! :)");
			} else return;
		}
		
		Faction damagedFaction = damagedFP.getFaction(),
				damagerFaction = damagerFP.getFaction();

		if(damagedFaction.isDefault() && damagerFaction.isDefault())
			return;
		if(damagedFaction.equals(damagerFaction)){
			damagerFP.sendMessage("%red%On ne tape pas ses amis, oh !");
			e.setCancelled(true); return;
		}
		FactionRelationshipType relation = getFactionsManager().getRelationship(damagedFaction, damagerFaction);
		if(relation == FactionRelationshipType.ALLY){
			damagerFP.sendMessage("%red%On ne tape pas ses alliés, oh !");
			e.setCancelled(true); return;
		}
		if(relation == FactionRelationshipType.ENEMY)
			return;

		ChunksManager cm = getChunksManager(e.getEntity().getLocation().getWorld());
		if(cm == null) return;
		Faction owner = cm.getFactionAt(e.getEntity()), ownerDamager = cm.getFactionAt(e.getDamager());

		if(owner.isSafezone() || ownerDamager.isSafezone()){
			e.setCancelled(true);
		}

		if(owner.equals(damagedFaction) && relation == FactionRelationshipType.NEUTRAL && !owner.isWilderness()){
			damagerFP.sendMessage("%red%On ne tape pas les propriétaires des lieux sans être son ennemi, oh !");
			damagedFP.sendMessage("%red%" + damagerFP.getLastUsername() + " a essayé de vous taper ! Attention ...");

			e.setCancelled(true); return;
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onFightHigh(EntityDamageByEntityEvent e){
		if(e.isCancelled()) return;

		FactionPlayer damagedFP = getPlayersManager().getPlayer(e.getEntity()),
				damagerFP = getPlayersManager().getPlayer(e.getDamager());

		if(damagedFP == null || damagerFP == null)
			return;	
		Player damaged = damagedFP.getPlayer(),
				damager = damagerFP.getPlayer();

		ChunksManager cm = getChunksManager(e.getEntity().getLocation().getWorld());
		if(cm == null) return;
		Faction owner = cm.getFactionAt(e.getEntity()), ownerDamager = cm.getFactionAt(e.getDamager());

		if(owner.isWarzone() && ownerDamager.isWarzone()){
			if(isNoStuff(damaged) && !isFighting(damaged)){
				e.setCancelled(true);
				ChatUtils.sendMessage(damager, FactionConfiguration.getInstance().getCanNotAttackANostuff());
			} else if(isNoStuff(damager) && !isFighting(damager)){
				e.setCancelled(true);
				ChatUtils.sendMessage(damager, FactionConfiguration.getInstance().getCanNotAttackWhenNostuff());
			}
		}

		if(damaged != null && damager != null && !e.isCancelled())
			fight(damaged, damager);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e){
		if(e.getEntityType() != EntityType.PLAYER) return;
		
		ChunksManager cm = getChunksManager(e.getEntity().getWorld());
		if(cm == null) return;

		FactionChunk fChunk = cm.getFactionChunk(e.getEntity().getLocation().getChunk());
		if(fChunk == null) return;

		Faction f = getFactionsManager().getFaction(fChunk.getFactionId());
		if(f == null) return;
		
		if(f.isSafezone()){
			e.setCancelled(true);
		}
		if(e.getCause() == DamageCause.FIRE_TICK && e.isCancelled()){
			e.getEntity().setFireTicks(0);
		}
	}

	@EventHandler
	public void onLostFood(FoodLevelChangeEvent e){
		if(e.getEntityType() != EntityType.PLAYER) return;
		Player p = (Player) e.getEntity();
		if(e.getFoodLevel() > p.getFoodLevel()) return;

		ChunksManager cm = getChunksManager(e.getEntity().getWorld());
		if(cm == null) return;

		FactionChunk fChunk = cm.getFactionChunk(e.getEntity().getLocation().getChunk());
		if(fChunk == null) return;

		Faction f = getFactionsManager().getFaction(fChunk.getFactionId());
		if(f == null) return;
		
		if(f.isSafezone()){
			e.setCancelled(true);
		} else if(f.isWarzone() && isNoStuff((Player)e.getEntity())){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPickUp(PlayerPickupItemEvent e){
		ChunksManager cm = getChunksManager(e.getPlayer().getWorld());
		if(cm == null) return;

		FactionChunk fChunk = cm.getFactionChunk(e.getPlayer().getLocation().getChunk());
		if(fChunk == null) return;

		Faction f = getFactionsManager().getFaction(fChunk.getFactionId());

		if(f == null) return;
		if(isNoStuff(e.getPlayer()) && f.isWarzone()){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onFlightRequested(PlayerToggleFlightEvent e){
		if(isFighting(e.getPlayer()) && !e.getPlayer().hasPermission("factions.admin.*")){
			e.getPlayer().setFlying(false);
			e.getPlayer().setAllowFlight(false);
		}
	}

	@EventHandler
	public void onCommandPrepocess(PlayerCommandPreprocessEvent e){
		if(isFighting(e.getPlayer()) && !e.getPlayer().hasPermission("factions.admin.*")){
			String[] cmds = e.getMessage().toLowerCase().split(" ");
			for(final String cmd : FactionConfiguration.getInstance().getDisallowedCommands()){
				int i = 0;
				for(String c : cmd.split(" ")){
					if(cmds.length > i && cmds[i].equals(c)){
						return;
					}
				}
			}
			ChatUtils.sendMessage(e.getPlayer(), FactionConfiguration.getInstance().getCannotUseCommandWhilstFighting());
			e.setCancelled(true);
		}
	}

	public boolean isFighting(Player p){
		return playersInFight.contains(p.getUniqueId());
	}

	public static boolean isNoStuff(Player p){
		for(ItemStack is : p.getInventory().getContents()){
			if(isStuffItem(is)){
				return false;
			}
		}
		
		for(ItemStack is : p.getInventory().getArmorContents()){
			if(isStuffItem(is)){
				return false;
			}
		}
		
		return true;
	}
	
	@SuppressWarnings("deprecation")
	private static boolean isStuffItem(ItemStack is){
		if(is == null) return false;
		for(Object i : FactionConfiguration.getInstance().getStuffItems()){
			String item = i.toString();

			String type = item.split(":")[0];
			if(is.getType() == Material.matchMaterial(type)){
				if(item.split(":").length > 1){
					byte data = (byte) Integer.parseInt(item.split(":")[1]);
					ItemStack isclone = is.clone();
					if(isclone.getType().isBlock() || isclone.getType().getMaxDurability() < 1){}
					else if(isclone.getDurability() == 0){} else {
						isclone.setDurability((short)0);
					}
					if(isclone.getData().getData() == (byte)data)
						return true;
				} else return true;
			}
		}
		
		return false;
	}
}
