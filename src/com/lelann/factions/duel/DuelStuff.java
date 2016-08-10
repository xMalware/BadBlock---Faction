package com.lelann.factions.duel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.devhill.socketinventory.data.DataProviders;
import fr.devhill.socketinventory.json.bukkit.JSON;
import fr.devhill.socketinventory.json.elements.JObject;
import lombok.Getter;

public class DuelStuff {
	@Getter private JObject  stuff;
	@Getter private Location location;
	
	public DuelStuff(JObject read){
		if(read.contains("stuff")){
			stuff = read.getObject("stuff");
		}
		
		location = new Location(Bukkit.getWorld(read.getString("loc.world")),
				read.getDouble("loc.x"),
				read.getDouble("loc.y"),
				read.getDouble("loc.z"),
				read.getFloat("loc.yaw"),
				read.getFloat("loc.pitch"));
	}
	
	public DuelStuff(Player player, boolean keepStuff){
		if(keepStuff){
			stuff = DataProviders.write(player, DataProviders.INVENTORY_PROVIDER, DataProviders.GAME_PROVIDER);
		}
		
		location = player.getLocation();
	}
	
	public void give(Player p){
		if(stuff != null){
			DataProviders.read(p, stuff);
		}
	}
	
	public JObject write(){
		JObject object = JSON.loadFromString("{}");
		object.set("stuff", "stuff");
		object.set("loc.world", location.getWorld().getName());
		object.set("loc.x", location.getX());
		object.set("loc.y", location.getY());
		object.set("loc.z", location.getZ());
		object.set("loc.yaw", location.getYaw());
		object.set("loc.pitch", location.getPitch());
		return object;
	}
}
