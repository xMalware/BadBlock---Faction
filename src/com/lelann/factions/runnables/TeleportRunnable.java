	package com.lelann.factions.runnables;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.earth2me.essentials.Essentials;
import com.lelann.factions.utils.ChatUtils;

public class TeleportRunnable extends FRunnable{
	private final Player player;
	private final double fromHeal;
	private final Location fromLocation, toLocation;
	private int time;
	
	public TeleportRunnable(final Player player, final Location location, final int time){
		super(5L);
		ChatUtils.sendMessage(player, "%gray%Téléportation dans " + time + " secondes, ne bougez pas.");
		this.time = time * 4;
		this.player = player;
		this.fromHeal = player.getHealth();
		this.fromLocation = player.getLocation();
		this.toLocation = location;
	}
	
	@Override
	public void run(){
		if(player == null || !player.isOnline() || !player.isValid()){
			cancel();
			return;
		}
		
		if(!fromLocation.getBlock().equals(player.getLocation().getBlock())){
			cancel();
			ChatUtils.sendMessage(player, "%red%Téléportation annulée, vous avez bougé.");
		} else if(fromHeal > player.getHealth()){
			cancel();
			ChatUtils.sendMessage(player, "%red%Téléportation annulée, vous êtes en combat");
		}
		
		time--;
		if(time == 0){
			cancel();
			Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
			ess.getUser(player).setLastLocation(player.getLocation());

			player.teleport(toLocation, TeleportCause.PLUGIN);
		}
	}
}
