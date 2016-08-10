package com.lelann.factions.runnables;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.lelann.factions.Main;

public abstract class FRunnable extends BukkitRunnable{
	private long delay;
	public FRunnable(long delay){
		this.delay = delay;
	}
	
	public BukkitTask start(){
		return runTaskTimer(Main.getInstance(), 0, delay);
	}
	
	public BukkitTask startLater(){
		return runTaskLater(Main.getInstance(), delay);
	}
}
