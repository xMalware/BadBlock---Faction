package com.lelann.factions.runnables;

import org.bukkit.entity.Player;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.api.FactionPlayer;

public class PowerRunnable extends FRunnable{
	private FactionPlayer fPlayer;
	private int time = 5;
	private boolean first = true;
	
	public PowerRunnable(FactionPlayer fPlayer){
		super(20L * 60 * 1); // each minutes
		this.fPlayer = fPlayer;
	}

	@Override
	public void run(){
		if(first){ first = false; return; }
		Player player = fPlayer.getPlayer();
		if(player == null || !player.isOnline()){
			cancel();
			return;
		}
		time--;
		
		double power = FactionConfiguration.getInstance().getPowerEarnedByTime();
		fPlayer.addPower(power / FactionConfiguration.getInstance().getTimeBetweenPowerRegen()); // add power o.o
		if(time == 0){
			time = 5;
			fPlayer.save(false);
		}
	}
}
