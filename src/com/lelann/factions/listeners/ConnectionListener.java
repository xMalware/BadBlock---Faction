package com.lelann.factions.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.FactionObject;
import com.lelann.factions.Main;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.utils.ChatUtils;
import com.lelann.factions.utils.Title;

import net.md_5.bungee.api.ChatColor;

public class ConnectionListener extends FactionObject implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		FactionPlayer player = getPlayersManager().getPlayer(e.getPlayer());
		
		e.setJoinMessage(ChatUtils.colorReplace("&7[&c+&7] &e" + e.getPlayer().getName()));
		
		new Title(FactionConfiguration.getInstance().getWelcomeTitle(), FactionConfiguration.getInstance().getWelcomeSubTitle(), 10, 60, 10).send(e.getPlayer());
		
		if(player == null){
			player = getPlayersManager().addPlayer(e.getPlayer());
		} else player.update(e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		FactionPlayer player = getPlayersManager().getPlayer(e.getPlayer());
		e.setQuitMessage(null);
		
		if(player != null){
			if(player.getPowerTask() != null) player.getPowerTask().cancel();
			player.disconnect();
			player.save(false);
		}
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e){
		e.getPlayer().sendMessage(ChatColor.RED + "Vous avez été éjecté : " + e.getReason());
		Main.getInstance().kick(e.getPlayer());
		
		e.setCancelled(true);
	}
}
