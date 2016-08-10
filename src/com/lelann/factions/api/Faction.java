package com.lelann.factions.api;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.FactionObject;
import com.lelann.factions.api.managers.ChunksManager;
import com.lelann.factions.utils.ChatUtils;
import com.lelann.factions.utils.ConfigUtils;

import lombok.Getter;
import lombok.Setter;

public class Faction extends FactionObject {
	private static int NEXT_ID = 0;
	public final static Faction SAFEZONE, WARZONE, WILDERNESS;

	static {
		FactionConfiguration f = FactionConfiguration.getInstance();

		SAFEZONE = new Faction(f.getSafezoneFactionName(), -1);
		SAFEZONE.setDescription(f.getSafezoneFactionDescription());

		WARZONE = new Faction(f.getWarzoneFactionName(), -2);
		WARZONE.setDescription(f.getWarzoneFactionDescription());

		WILDERNESS = new Faction(f.getWildernessFactionName(), -3);
		WILDERNESS.setDescription(f.getWildernessFactionDescription());
	}

	public static String defaultColor(Faction faction){
		if(faction.getFactionId() == WILDERNESS.getFactionId())
			return FactionConfiguration.getInstance().getWildernessFactionColor();
		else if(faction.getFactionId() == WARZONE.getFactionId())
			return FactionConfiguration.getInstance().getWarzoneFactionColor();
		else if(faction.getFactionId() == SAFEZONE.getFactionId())
			return FactionConfiguration.getInstance().getSafezoneFactionColor();
		else return FactionConfiguration.getInstance().getWildernessFactionColor();
	}

	@Getter
	private Map<UUID, FactionRank> players;
	@Getter@Setter 	private int factionId, chunkNumber, apChunkNumber;

	@Getter @Setter
	private String name, description;
	@Getter @Setter Location home;
	@Getter private List<UUID> invitedPlayers;
	@Getter private List<Integer> neutral, ally;
	
	@Getter private Map<Integer, FactionChunk> chunksChunk = new HashMap<>();
	
	private boolean toRemove = false, toCreate;

	public double getPower(){
		double result = 0d;
		
		for(UUID uniqueId : players.keySet()){
			FactionPlayer player = getPlayersManager().getPlayer(uniqueId);
			
			result += player.getPower();
		}
		
		return result;
	}
	
	public double getMaxPower(){
		return FactionConfiguration.getInstance().getMaxPower() * players.size();
	}
	
	public void delete(){
		toRemove = true;
		save(false);
		unclaimAll();
		getFactionsManager().remove(factionId);
	}
	
	public void unclaimAll(){
		for(ChunksManager manager : getChunksManagers()){
			manager.removeAll(this);
		}
	}

	private void init(){
		players = new HashMap<UUID, FactionRank>();
		invitedPlayers = new ArrayList<UUID>();
		neutral = new ArrayList<Integer>();
		ally = new ArrayList<Integer>();
	}

	public Faction(String name){
		init();
		this.description = "Description de faction par défaut.";
		this.factionId = NEXT_ID; NEXT_ID++;
		this.name = name;
		
		toCreate = true;
	}

	public Faction(String name, int factionId){
		init();
		this.factionId = factionId;
		this.name = name;
		this.description = "Description de faction par défaut.";
		toCreate = true;
	}

	public Faction(ResultSet faction, ResultSet members, ResultSet chunks) throws Exception{
		init();
		this.factionId = faction.getInt("factionId");
		this.name = faction.getString("displayName");
		this.description = faction.getString("description");
		this.home = ConfigUtils.locationFromString(faction.getString("home"));

		if(factionId >= NEXT_ID){
			NEXT_ID = factionId + 1;
		}

		while(members.next()){
			try {
				FactionPlayer fPlayer = new FactionPlayer(members);

				FactionRank rank = fPlayer.getFactionRank();
				if(rank == null){
					rank = FactionRank.PLAYER;
				}

				players.put(fPlayer.getUniqueId(), rank);
			} catch(Exception unused){}
		}
		while(chunks.next()){
			try {
				FactionChunk chunk = new FactionChunk(chunks);
				if(chunk.isAp())
					apChunkNumber++;
				chunkNumber++;
			} catch(Exception unused){}
		}
		toCreate = false;
	}

	public void sendMessage(String... messages){
		for(UUID uniqueId : players.keySet()){
			Player player = getServer().getPlayer(uniqueId);
			if(player == null) continue;

			ChatUtils.sendMessage(player, messages);
		}
	}

	public boolean isWarzone(){
		return WARZONE.equals(this);
	}

	public boolean isSafezone(){
		return SAFEZONE.equals(this);
	}

	public boolean isWilderness(){
		return WILDERNESS.equals(this);
	}

	public boolean isDefault(){
		return isWarzone() || isSafezone() || isWilderness();
	}

	public void addMember(FactionPlayer player){
		FactionRank rank = players.size() == 0 ?FactionRank.LEADER : FactionRank.NEWBIE;
		players.put(player.getUniqueId(), rank);
		player.setFactionId(factionId);
		player.setFactionRank(rank);
		player.save(false);
		
		save(false);
	}
	
	public void removeMember(FactionPlayer player){
		players.remove(player.getUniqueId());
		
		if(players.size() == 0)
			delete();
	}

	public void setRank(FactionPlayer player, FactionRank rank){
		removeMember(player);
		players.put(player.getUniqueId(), rank);
		player.setFactionRank(rank);
	}

	public String getSQLUpdate(){
		if(toRemove){
			toCreate = true;
			return "DELETE FROM fFactions WHERE factionId=" + factionId;
		} else if(toCreate){
			toCreate = false;
			return "INSERT INTO fFactions(factionId, displayName, description, home) VALUES(" + factionId + ", '" + name + "', '" + description + "', '" + ConfigUtils.locationToString(home) + "')";
		} else {
			return "UPDATE fFactions SET "
					+ "displayName='" + name.replace("'", "\\'") + "', "
					+ "description='" + description.replace("'", "\\'") + "', "
					+ "home='" + ConfigUtils.locationToString(home) + "'"
					+ " WHERE factionId=" + factionId;
		}
	}

	public void save(boolean synchronously){
		getFactionsManager().saveFaction(this, synchronously);
	}

	public void updateScoreboard(){
		for(UUID uniqueId : players.keySet()){
			FactionPlayer player = getPlayersManager().getPlayer(uniqueId);
			if(player.getPlayer() != null)
				player.getScoreboard().generate();
		}
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Faction){
			return ((Faction) o).getFactionId() == getFactionId();
		}

		return false;
	}

	public static enum FactionRank {
		LEADER("Chef", 3),
		MODERATOR("Modo", 2),
		PLAYER("Membre", 1),
		NEWBIE("Nouveau", 0);

		@Getter private final String rankName;
		@Getter private final int power;

		private FactionRank(String rankName, int power){
			this.rankName = rankName;
			this.power = power;
		}

		public static FactionRank matchRank(String rankName){
			if(rankName == null) return null;

			FactionRank theRank = null;
			try {
				theRank = valueOf(rankName);
			} catch(Exception e){}
			if(theRank != null)
				return theRank;

			for(FactionRank rank : values())
				if(rank.getRankName().equalsIgnoreCase(rankName))
					return rank;

			return null;
		}
	}
}
