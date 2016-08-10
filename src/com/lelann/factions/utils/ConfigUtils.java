package com.lelann.factions.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ConfigUtils {
	public static Location locationFromString(String location){
		try {
			String[] loc = location.split(":");
			return new Location(
					Bukkit.getWorld(loc[0]),
					Double.parseDouble(loc[1]),
					Double.parseDouble(loc[2]),
					Double.parseDouble(loc[3]),
					Float.parseFloat(loc[4]),
					Float.parseFloat(loc[5])
					);
		} catch(Exception e){ return null; }
	}

	public static String locationToString(Location location){
		if(location == null) return "";
		return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY()
				+ ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
	}
}
