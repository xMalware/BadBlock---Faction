package com.lelann.factions.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.FactionObject;
import com.lelann.factions.api.Faction;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.api.FactionChunk;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.managers.ChunksManager;
import com.lelann.factions.utils.ChatUtils;

public class DestroyChunkListener extends FactionObject implements Listener{
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e){
		if(e.getEntityType() == EntityType.CREEPER && e.getSpawnReason() == SpawnReason.SPAWNER_EGG)
			return;
		ChunksManager cm = getChunksManager(e.getEntity().getWorld());
		if(cm == null) return;

		FactionChunk fChunk = cm.getFactionChunk(e.getEntity().getLocation().getChunk());
		if(fChunk == null) return;

		Faction f = getFactionsManager().getFaction(fChunk.getFactionId());
		if(f.isWarzone() || f.isSafezone() || (f.isWilderness() && fChunk.isAp())){
			if(e.getSpawnReason() != SpawnReason.CUSTOM && !(f.isWarzone() && e.getSpawnReason() == SpawnReason.EGG))
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEmptyBucket(PlayerBucketEmptyEvent e){
		if(cancel(getPlayersManager().getPlayer(e.getPlayer()), e.getBlockClicked())){
			ChatUtils.sendMessage(e.getPlayer(), "%red%Vous ne pouvez pas placer de block ici !");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onFillBucket(PlayerBucketFillEvent e){
		if(cancel(getPlayersManager().getPlayer(e.getPlayer()), e.getBlockClicked())){
			ChatUtils.sendMessage(e.getPlayer(), "%red%Vous ne pouvez pas casser de block ici !");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onExplode(ExplosionPrimeEvent e){
		ChunksManager cm = getChunksManager(e.getEntity().getWorld());
		if(cm == null) return;

		FactionChunk fChunk = cm.getFactionChunk(e.getEntity().getLocation().getChunk());
		if(fChunk == null) return;

		Faction f = getFactionsManager().getFaction(fChunk.getFactionId());
		if(f.isSafezone()){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent e){
		ChunksManager cm = getChunksManager(e.getEntity().getWorld());
		if(cm == null) return;

		FactionChunk fChunk = cm.getFactionChunk(e.getEntity().getLocation().getChunk());
		if(fChunk == null) return;

		Faction f = getFactionsManager().getFaction(fChunk.getFactionId());

		if(f.isSafezone()){
			e.setCancelled(true); return;
		}

		for(int i=0;i<e.blockList().size();i++){
			Block b = e.blockList().get(i);

			FactionChunk fChunkBlock = cm.getFactionChunk(b.getChunk());
			if(fChunkBlock != null) {
				Faction fBlock = getFactionsManager().getFaction(fChunkBlock.getFactionId());
				if(fBlock != null && (fBlock.isWarzone() || (fBlock.isWilderness() && fChunkBlock.isAp()))){
					e.blockList().remove(i);
					i--;
				}
			}
		}
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e){
		if(cancel(getPlayersManager().getPlayer(e.getPlayer()), e.getBlock())){
			System.out.println("good");
			ChatUtils.sendMessage(e.getPlayer(), "%red%Vous ne pouvez pas placer de block ici !");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent e){
		if(cancel(getPlayersManager().getPlayer(e.getPlayer()), e.getBlock())){
			ChatUtils.sendMessage(e.getPlayer(), "%red%Vous ne pouvez pas casser de block ici !");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e){
		if(e.getRightClicked().getType() == EntityType.ARMOR_STAND && cancel(getPlayersManager().getPlayer(e.getPlayer()), e.getRightClicked().getLocation().getBlock())){
			ChatUtils.sendMessage(e.getPlayer(), "%red%Vous ne pouvez pas intéragir avec un armor stand ici !");
			e.setCancelled(true);
		} else if(e.getRightClicked().getType() == EntityType.ITEM_FRAME){
			Material disallowed = null;
			for(String materialStr : FactionConfiguration.getInstance().getDisallowedInteractObject()){
				Material material = Material.matchMaterial(materialStr);
				if(material == Material.ITEM_FRAME){
					disallowed = material;
				}
			}

			if(disallowed == null) return;
			if(cancel(getPlayersManager().getPlayer(e.getPlayer()), e.getRightClicked().getLocation().getBlock())){
				ChatUtils.sendMessage(e.getPlayer(), "%red%Vous ne pouvez pas intéragir avec ce block (" + disallowed.name().toLowerCase().replace("_", " ") + ") ici !");
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPotionSplash(PotionSplashEvent e){
		FactionPlayer player = getPlayersManager().getPlayer(e.getEntity());
		if(player == null) return;

		Player p = (Player) e.getEntity().getShooter();
		if(!getPlayersManager().getPlayer(p).isBypass()){
			ChunksManager cm = getChunksManager(p.getLocation().getChunk().getWorld());
			if(cm != null) {
				FactionChunk fChunk = cm.getFactionChunk(p.getLocation().getChunk());
				if(fChunk != null && fChunk.getFactionId() == Faction.SAFEZONE.getFactionId()){
					ChatUtils.sendMessage(p, "&cVous ne pouvez pas utiliser de potions dans la SafeZone !");
					e.setCancelled(true); return;
				}
			}
		}
	}

	@EventHandler
	public void onPickupArmorStand(PlayerArmorStandManipulateEvent e){
		if(e.isCancelled()) return;

		if(cancel(getPlayersManager().getPlayer(e.getPlayer()), e.getRightClicked().getLocation().getBlock())){
			ChatUtils.sendMessage(e.getPlayer(), "%red%Vous ne pouvez pas intéragir avec un armor stand ici !");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e){
		FactionPlayer damagedFP = getPlayersManager().getPlayer(e.getEntity()),
				damagerFP = getPlayersManager().getPlayer(e.getDamager());

		if(damagerFP != null && damagedFP == null){
			ChunksManager cm = getChunksManager(e.getEntity().getLocation().getWorld());
			if(cm == null) return;
			Faction owner = cm.getFactionAt(e.getEntity()), ownerDamager = cm.getFactionAt(e.getDamager());

			if(owner.isSafezone() || ownerDamager.isSafezone()){
				e.setCancelled(true);
			} else if(e.getEntity().getType() == EntityType.ITEM_FRAME){
				Material disallowed = null;
				for(String materialStr : FactionConfiguration.getInstance().getDisallowedInteractObject()){
					Material material = Material.matchMaterial(materialStr);
					if(material == Material.ITEM_FRAME){
						disallowed = material;
					}
				}

				if(disallowed == null) return;
				if(cancel(damagerFP, e.getEntity().getLocation().getBlock())){
					ChatUtils.sendMessage(damagerFP.getPlayer(), "%red%Vous ne pouvez pas intéragir avec ce block (" + disallowed.name().toLowerCase().replace("_", " ") + ") ici !");
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onShoot(EntityShootBowEvent e){
		if(e.getEntityType() == EntityType.PLAYER){
			if(!getPlayersManager().getPlayer(e.getEntity()).isBypass()){
				ChunksManager cm = getChunksManager(e.getEntity().getLocation().getChunk().getWorld());
				if(cm != null) {
					FactionChunk fChunk = cm.getFactionChunk(e.getEntity().getLocation().getChunk());
					if(fChunk != null && fChunk.getFactionId() == Faction.SAFEZONE.getFactionId()){
						ChatUtils.sendMessage(e.getEntity(), "&cVous ne pouvez pas utiliser les flèches dans la SafeZone !");
						e.setCancelled(true); return;
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if(e.getItem() != null && e.getItem().getType() == Material.ENDER_PEARL){
			if(!getPlayersManager().getPlayer(e.getPlayer()).isBypass()){
				ChunksManager cm = getChunksManager(e.getPlayer().getLocation().getChunk().getWorld());
				if(cm != null) {
					FactionChunk fChunk = cm.getFactionChunk(e.getPlayer().getLocation().getChunk());
					if(fChunk != null && fChunk.getFactionId() == Faction.SAFEZONE.getFactionId()){
						ChatUtils.sendMessage(e.getPlayer(), "&cVous ne pouvez pas utiliser d'EnderPearl dans la SafeZone !");
						e.setCancelled(true); return;
					}
				}
			}
		}

		if ((e.getAction() == Action.PHYSICAL) && e.getClickedBlock().getType() == Material.SOIL){
			e.setCancelled(true); return;
		} else if(e.getClickedBlock() == null || e.getClickedBlock().getType() == Material.AIR) return;

		Material disallowed = null;
		for(String materialStr : FactionConfiguration.getInstance().getDisallowedInteractObject()){
			Material material = Material.matchMaterial(materialStr);
			if(e.getClickedBlock().getType() == material){
				disallowed = material;
			}
		}

		if(disallowed == null) return;

		ChunksManager cm = getChunksManager(e.getClickedBlock().getChunk().getWorld());
		if(cm == null) return;

		FactionChunk fChunk = cm.getFactionChunk(e.getClickedBlock().getChunk());
		if(fChunk == null) return;

		Faction faction = fChunk.getOwner();
		if(fChunk == null || faction == null) return;

		if(faction.isSafezone()) return;

		if(cancel(getPlayersManager().getPlayer(e.getPlayer()), e.getClickedBlock())){
			ChatUtils.sendMessage(e.getPlayer(), "%red%Vous ne pouvez pas intéragir avec ce block (" + disallowed.name().toLowerCase().replace("_", " ") + ") ici !");
			e.setCancelled(true);
		}
	}

	public boolean cancel(FactionPlayer factionPlayer, Block block){
		if(factionPlayer.isBypass()) return false;

		ChunksManager cm = getChunksManager(block.getChunk().getWorld());
		if(cm == null) return false;

		FactionChunk fChunk = cm.getFactionChunk(block.getChunk());
		if(fChunk == null) return false;

		Faction faction = fChunk.getOwner();
		if(fChunk == null || faction == null) return false;

		if(faction.equals(factionPlayer.getFaction()) && !faction.isWilderness()){
			if(fChunk.getAllowedMembers().isEmpty()){
				return factionPlayer.getFactionRank() == FactionRank.NEWBIE && block instanceof InventoryHolder;
			} else return factionPlayer.getFactionRank() != FactionRank.LEADER && !fChunk.getAllowedMembers().contains(factionPlayer.getUniqueId());
		} else if(faction.isWilderness()){
			return fChunk.isAp();
		} else {
			return true;
		}
	}
}
