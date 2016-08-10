package com.lelann.factions.duel;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.Essentials;
import com.lelann.factions.FactionObject;
import com.lelann.factions.Main;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.duel.Duels.PreDuel;
import com.lelann.factions.utils.ChatUtils;
import com.lelann.factions.utils.Title;

import lombok.Getter;

public class Duel extends FactionObject {
	@Getter private UUID firstPlayer, secondPlayer;
	@Getter private Location duelLocation;
	@Getter private DuelState state;

	@Getter private DuelStuff stuffP1, stuffP2;

	@Getter private boolean keepStuff;

	public Duel(Location duelLocation, PreDuel proposotion){
		this.firstPlayer = proposotion.getFrom();
		this.secondPlayer = proposotion.getTo();

		this.duelLocation = duelLocation;

		this.keepStuff = proposotion.isKeepStuff();

		beforeTeleportThread();
	}

	public Player getOtherPlayer(UUID uniqueId){
		if(uniqueId.equals(firstPlayer)){
			return Bukkit.getPlayer(secondPlayer);
		} else return Bukkit.getPlayer(firstPlayer);
	}

	private void deleteDuelFromDB(){
		state = DuelState.ENDED;
		Duels.getInstance().getDuelsByPlayer().remove(firstPlayer);
		Duels.getInstance().getDuelsByPlayer().remove(secondPlayer);

		Duels.getInstance().getArenas().add(duelLocation);
	}

	public void win(Player player, Player vs){
		String message = "%red%" + player.getDisplayName() + " %gold%a vaincu %red%" + vs.getDisplayName() + " %gold%en " + (keepStuff ? "&aduel amical" : "&cduel à mort") + "%gold%.";
		ChatUtils.broadcast(message);

		heal(keepStuff, player);

		FactionPlayer fp = getPlayersManager().getPlayer(player);
		fp.setDuelWins(fp.getDuelWins() + 1);
		fp.save(false);
	}

	public void loose(Player player){
		FactionPlayer fp = getPlayersManager().getPlayer(player);
		fp.setDuelLooses(fp.getDuelLooses() + 1);
		fp.save(false);
	}

	public void endDuel(Player looser){
		state = DuelState.ENDED;

		Player p1 = (looser != null && looser.getUniqueId().equals(firstPlayer)) ?
				looser : Bukkit.getPlayer(firstPlayer);
		
		if(p1 != null && p1.isValid() && p1.isOnline() && !p1.isDead()){
			if(keepStuff){
				for(Entity e : p1.getNearbyEntities(20.0d, 20.0d, 20.0d)){
					if(e.getType() == EntityType.DROPPED_ITEM)
						e.remove();
				}
			}
			stuffP1.give(p1);
			ChatUtils.sendMessage(p1, "%gold%Téléportation dans %red%10 secondes %gold%!");

			if(!p1.equals(looser)){
				new BukkitRunnable(){
					@Override
					public void run(){
						p1.teleport(stuffP1.getLocation());
						
						new BukkitRunnable(){
							@Override
							public void run(){
								Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
								ess.getUser(p1).setLastLocation(stuffP1.getLocation());
							}
						}.runTaskLater(Main.getInstance(), 1L);
					}
				}.runTaskLater(Main.getInstance(), 20L * 10);
			} else p1.teleport(stuffP1.getLocation());
		} else {
			Duels.getInstance().keepStuff(stuffP1, firstPlayer);
		}

		Player p2 = (looser != null && looser.getUniqueId().equals(secondPlayer)) ?
				looser : Bukkit.getPlayer(secondPlayer);
				
		if(p2 != null && p2.isValid() && p2.isOnline() && !p2.isDead()){
			if(keepStuff){
				for(Entity e : p2.getNearbyEntities(20.0d, 20.0d, 20.0d)){
					if(e.getType() == EntityType.DROPPED_ITEM)
						e.remove();
				}
			}
			stuffP2.give(p2);
			ChatUtils.sendMessage(p2, "%gold%Téléportation dans %red%10 secondes %gold%!");
			if(!p2.equals(looser)){
				new BukkitRunnable(){
					@Override
					public void run(){
						p2.teleport(stuffP2.getLocation());
						
						new BukkitRunnable(){
							@Override
							public void run(){
								Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
								ess.getUser(p2).setLastLocation(stuffP2.getLocation());
							}
						}.runTaskLater(Main.getInstance(), 1L);
					}
				}.runTaskLater(Main.getInstance(), 20L * 10);
			} else p1.teleport(stuffP1.getLocation());
		} else {
			Duels.getInstance().keepStuff(stuffP2, secondPlayer);
		}

		this.deleteDuelFromDB();
	}

	private void heal(boolean removeAllEffects, Player... who){
		for(Player p : who){
			if(p == null) continue;

			if(!removeAllEffects) {
				p.removePotionEffect(PotionEffectType.INVISIBILITY);
				p.removePotionEffect(PotionEffectType.POISON);
				p.removePotionEffect(PotionEffectType.WEAKNESS);
				p.removePotionEffect(PotionEffectType.SLOW);
			} else {
				for(PotionEffectType type : PotionEffectType.values()){
					if(type != null)
						p.removePotionEffect(type);
				}
			}

			p.setFireTicks(0);
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
		}
	}

	private void beforeFightThread(){
		this.state = DuelState.BEFORE_FIGHT;

		new BukkitRunnable(){
			private int time = 10;

			@Override
			public void run(){
				if(time == 0){
					if(!isOnlineP1() || !isOnlineP2()){
						sendMessage("&cL'un d'entre vous s'est déconnecté ! Duel annulé.");
						endDuel(null);
					} else {
						Title title = new Title("&cAttention !", "%red%Le combat commence !", 5, 40, 5);
						title.send(Bukkit.getPlayer(firstPlayer), Bukkit.getPlayer(secondPlayer));

						heal(false, Bukkit.getPlayer(firstPlayer), Bukkit.getPlayer(secondPlayer));
						state = DuelState.FIGHT;
					}

					cancel();
				} else {
					Title title = new Title(time + "", "%gold%Le combat va commencer ...");
					title.send(Bukkit.getPlayer(firstPlayer), Bukkit.getPlayer(secondPlayer));
				}

				time--;
			}
		}.runTaskTimer(Main.getInstance(), 0, 20L);
	}

	private void beforeTeleportThread(){
		this.state = DuelState.BEFORE_TELEPORTATION;

		final Location initialLocationP1, initialLocationP2;

		if(!isOnlineP1() || !isOnlineP2()){
			sendMessage("&cL'un d'entre vous s'est déconnecté ! Duel annulé."); 
			deleteDuelFromDB(); return;
		} else {
			initialLocationP1 = Bukkit.getPlayer(firstPlayer).getLocation();
			initialLocationP2 = Bukkit.getPlayer(secondPlayer).getLocation();
		}

		new BukkitRunnable(){
			private int time = 5;

			@Override
			public void run(){
				if(time == 0){
					if(!isOnlineP1() || !isOnlineP2()){
						sendMessage("&cL'un d'entre vous s'est déconnecté ! Duel annulé.");
						deleteDuelFromDB();
					} else {
						if(!initialLocationP1.getWorld().equals(Bukkit.getPlayer(firstPlayer).getLocation().getWorld())
								|| !initialLocationP2.getWorld().equals(Bukkit.getPlayer(secondPlayer).getLocation().getWorld())
								|| initialLocationP1.distance(Bukkit.getPlayer(firstPlayer).getLocation()) > 0.5d
								|| initialLocationP2.distance(Bukkit.getPlayer(secondPlayer).getLocation()) > 0.5d){
							sendMessage("&cL'un d'entre vous s'est déplacé avant la téléportation ! Duel annulé.");
							deleteDuelFromDB();
						} else {
							sendMessage("&4[&cBadBlock-Duel&4] %gold%Téléportation !");

							Bukkit.getPlayer(firstPlayer).closeInventory();
							Bukkit.getPlayer(secondPlayer).closeInventory();
							
							stuffP1 = new DuelStuff(Bukkit.getPlayer(firstPlayer), keepStuff);
							stuffP2 = new DuelStuff(Bukkit.getPlayer(secondPlayer), keepStuff);

							
							Bukkit.getPlayer(firstPlayer).teleport(duelLocation);
							Bukkit.getPlayer(secondPlayer).teleport(duelLocation);

							for(Entity e : Bukkit.getPlayer(firstPlayer).getNearbyEntities(32.0d, 32.0d, 32.0d)){
								if(e.getType() != EntityType.PLAYER){
									e.remove();
								}
							}

							beforeFightThread();
						}
					}

					cancel();
				} else {
					sendMessage("&4[&cBadBlock-Duel&4] %gold%Téléportation dans %red%" + time + " seconde" + (time > 1 ? "s" : ""));
				}

				time--;
			}
		}.runTaskTimer(Main.getInstance(), 0, 20L);
	}

	public boolean isOnlineP1(){
		return Bukkit.getPlayer(firstPlayer) != null;
	}

	public boolean isOnlineP2(){
		return Bukkit.getPlayer(secondPlayer) != null;
	}

	public void sendMessage(String message){
		Player p = Bukkit.getPlayer(firstPlayer);
		Player p2 = Bukkit.getPlayer(secondPlayer);

		if(p != null)
			ChatUtils.sendMessage(p, message);
		if(p2 != null)
			ChatUtils.sendMessage(p2, message);
	}

	public static enum DuelState {
		BEFORE_TELEPORTATION,
		BEFORE_FIGHT,
		FIGHT,
		ENDED;
	}
}
