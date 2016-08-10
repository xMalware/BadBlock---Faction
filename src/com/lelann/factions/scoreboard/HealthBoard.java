package com.lelann.factions.scoreboard;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.lelann.factions.FactionObject;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.runnables.FRunnable;
import com.lelann.factions.utils.ChatUtils;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;

public class HealthBoard extends FactionObject implements Listener {
	@Getter private static HealthBoard instance;
	
	private Scoreboard handler;
	private Objective health;
	private String objectiveName = "health";
	
	/*
	 * 0 : List
	 * 1 : Sidebar
	 * 2 : Below name
	 */
	private int displaySlot = 2;
	
	public HealthBoard(){
		instance = this;
		
		handler = Bukkit.getScoreboardManager().getNewScoreboard();
		health = handler.registerNewObjective(objectiveName, "dummy");
		
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);
		health.setDisplayName(ChatUtils.colorReplace("&c❤"));
	}
	
	public void setHealth(Player player){
		Score score = health.getScore(player.getName());
		score.setScore((int) player.getHealth());
		sendScore(score);
	}
	
	public void sendObjective(Player player){
		CraftScoreboard bukkit = (CraftScoreboard) handler;
		ScoreboardObjective objective = bukkit.getHandle().getObjective(objectiveName);
		
		PacketUtils.sendPacket(player, new PacketPlayOutScoreboardObjective(objective, 1));
		PacketUtils.sendPacket(player, new PacketPlayOutScoreboardObjective(objective, 0));
	
		for(Score score : handler.getScores(objectiveName)){
			sendScore(player, score);
		}
		
		PacketUtils.sendPacket(player, new PacketPlayOutScoreboardDisplayObjective(displaySlot, objective));
	}
	
	public void sendScore(Player player, Score score){
		CraftScoreboard bukkit = (CraftScoreboard) handler;
		ScoreboardObjective objective = bukkit.getHandle().getObjective(objectiveName);
		
		PacketUtils.sendPacket(player, new PacketPlayOutScoreboardScore(bukkit.getHandle().getPlayerScoreForObjective(score.getEntry(), objective)));
	}
	
	public void sendScore(Score score){
		CraftScoreboard bukkit = (CraftScoreboard) handler;
		ScoreboardObjective objective = bukkit.getHandle().getObjective(objectiveName);
		
		PacketUtils.broadcastPacket(new PacketPlayOutScoreboardScore(bukkit.getHandle().getPlayerScoreForObjective(score.getEntry(), objective)));
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e){
		if(e.getEntityType() == EntityType.PLAYER){
			final UUID uniqueId = ((Player)e.getEntity()).getUniqueId();
			new FRunnable(1L){
				@Override
				public void run(){
					setHealth(Bukkit.getPlayer(uniqueId));
				}
			}.startLater();
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		final UUID uniqueId = e.getPlayer().getUniqueId();
		new FRunnable(40L){
			@Override
			public void run(){
				setHealth(Bukkit.getPlayer(uniqueId));
				cancel();
			}
		}.startLater();
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent e){
		sendObjective(e.getPlayer());
		
		final UUID uniqueId = e.getPlayer().getUniqueId();
		new FRunnable(20L){
			@Override
			public void run(){
				FactionPlayer player = getPlayersManager().getPlayer(uniqueId);
				if(player.getPlayer() == null) {
					return;
				}
				
				setHealth(Bukkit.getPlayer(uniqueId));
			}
		}.startLater();
	}

	@EventHandler
	public void onGainHealth(EntityRegainHealthEvent e){
		if(e.getEntityType() == EntityType.PLAYER){
			final UUID uniqueId = ((Player)e.getEntity()).getUniqueId();
			new FRunnable(1L){
				@Override
				public void run(){
					setHealth(Bukkit.getPlayer(uniqueId));
				}
			}.startLater();
		}
	}
}
