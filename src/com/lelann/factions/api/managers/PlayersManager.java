package com.lelann.factions.api.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.player.PlayerEvent;

import com.lelann.factions.FactionObject;
import com.lelann.factions.api.FactionPlayer;

public class PlayersManager extends FactionObject {
	private Map<UUID, FactionPlayer> playersInCache;

	public PlayersManager(){
		playersInCache = new HashMap<UUID, FactionPlayer>();

		try {
			ResultSet result = getDB().querySQL("SELECT * FROM fPlayers");
			while(result.next()){
				try {
					FactionPlayer player = new FactionPlayer(result);
					playersInCache.put(player.getUniqueId(), player);
				} catch(Exception e){}
			}
		} catch (Exception unused) {}
	}

	public FactionPlayer getPlayer(final UUID uniqueId){
		return playersInCache.get(uniqueId);
	}

	public FactionPlayer getPlayer(CommandSender sender){
		if(sender instanceof Player)
			return getPlayer((Player) sender);
		return null;
	}
	
	public FactionPlayer getPlayer(final PlayerEvent playerEvent){
		return playersInCache.get(playerEvent.getPlayer());
	}
	
	public FactionPlayer getPlayer(final Entity player){
		if(player instanceof Player)
			return getPlayer(((Player)player).getUniqueId());
		else if(player instanceof Projectile)
			return getPlayer((Entity)((Projectile)player).getShooter());
		else return null;
	}

	public FactionPlayer getPlayer(final String player){
		Player p = getServer().getPlayer(player);
		if(p != null)
			return getPlayer(p.getUniqueId());
		else {
			for(FactionPlayer fp : playersInCache.values()){
				if(fp.getLastUsername().equalsIgnoreCase(player)){
					return fp;
				}
			}
		}
		return null;
	}
	
	public FactionPlayer addPlayer(Player player){
		FactionPlayer fplayer = new FactionPlayer(player);
		playersInCache.put(player.getUniqueId(), fplayer);

		return fplayer;
	}

	public void savePlayers(final boolean synchronously){
		for(FactionPlayer player : playersInCache.values())
			savePlayer(player, synchronously);
	}

	public void savePlayer(final FactionPlayer player, final boolean synchronously){
		try {
			if(!synchronously)
				getDB().updateAsynchrounously(player.getSQLUpdate());
			else getDB().updateSQL(player.getSQLUpdate());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
