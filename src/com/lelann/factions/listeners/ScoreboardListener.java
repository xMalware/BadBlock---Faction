package com.lelann.factions.listeners;

import org.bukkit.event.Listener;

import com.lelann.factions.FactionObject;

public class ScoreboardListener extends FactionObject implements Listener {
//	@EventHandler
//	public void onDamage(EntityDamageEvent e){
//		if(e.getEntityType() == EntityType.PLAYER){
//			final UUID uniqueId = ((Player)e.getEntity()).getUniqueId();
//			new FRunnable(1L){
//				@Override
//				public void run(){
//					update(getServer().getPlayer(uniqueId), false);
//				}
//			}.startLater();
//		}
//	}
//
//	@EventHandler
//	public void onRespawn(PlayerRespawnEvent e){
//		final UUID uniqueId = e.getPlayer().getUniqueId();
//		new FRunnable(40L){
//			@Override
//			public void run(){
//				update(getServer().getPlayer(uniqueId), false);
//				cancel();
//			}
//		}.startLater();
//	}
//	
//	@EventHandler(priority = EventPriority.HIGH)
//	public void onJoin(PlayerJoinEvent e){
//		final UUID uniqueId = e.getPlayer().getUniqueId();
//		new FRunnable(20L){
//			@Override
//			public void run(){
//				FactionPlayer player = getPlayersManager().getPlayer(uniqueId);
//				if(player.getPlayer() == null) {
//					return;
//				}
//				
//				update(getServer().getPlayer(uniqueId), true);
//			}
//		}.startLater();
//	}
//
//	@EventHandler
//	public void onGainHealth(EntityRegainHealthEvent e){
//		if(e.getEntityType() == EntityType.PLAYER){
//			final UUID uniqueId = ((Player)e.getEntity()).getUniqueId();
//			new FRunnable(1L){
//				@Override
//				public void run(){
//					update(getServer().getPlayer(uniqueId), false);
//				}
//			}.startLater();
//		}
//	}
//	
//	private void update(final Player concerned, boolean prefix){
//		if(concerned == null) return;
//		FactionPlayer fp = getPlayersManager().getPlayer(concerned);
//		for(final Player p : getServer().getOnlinePlayers()){
//			FactionPlayer player = getPlayersManager().getPlayer(p);
//			if(player.getScoreboard() != null) {
//				player.getScoreboard().change(concerned);
//				if(prefix) player.getScoreboard().updatePlayerPrefix(fp);
//			}
//		}
//	}
}
