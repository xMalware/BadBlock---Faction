package com.lelann.factions;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;

import com.lelann.factions.api.managers.ChunksManager;
import com.lelann.factions.api.managers.FactionsManager;
import com.lelann.factions.api.managers.PlayersManager;
import com.lelann.factions.database.Database;
import com.lelann.factions.permissions.AbstractPermissions;

public class FactionObject {
	public Server getServer(){
		return Bukkit.getServer();
	}
	public Main getMain(){
		return Main.getInstance();
	}
	public Database getDB(){
		return Main.getInstance().getDB();
	}
	public ChunksManager getChunksManager(String name){
		return getMain().getChunksManager(name);
	}
	public ChunksManager getChunksManager(World w){
		return getMain().getChunksManager(w);
	}
	public Collection<ChunksManager> getChunksManagers(){
		return getMain().getChunksManagers();
	}
	public PlayersManager getPlayersManager(){
		return getMain().getPlayersManager();
	}
	public FactionsManager getFactionsManager(){
		return getMain().getFactionsManager();
	}
	public AbstractPermissions getPermissions(){
		return AbstractPermissions.getPermissions();
	}
}
