package com.lelann.factions.api.managers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.lelann.factions.FactionObject;
import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.FactionRelationship;
import com.lelann.factions.api.FactionRelationship.FactionRelationshipType;

public class FactionsManager extends FactionObject{
	private Map<Integer, Faction> loadedFactions;
	private Map<String, Integer> displayNames;
	private List<FactionRelationship> relationships;

	public FactionsManager(){
		loadedFactions = new HashMap<Integer, Faction>();
		displayNames = new HashMap<String, Integer>();

		try {
			ResultSet allFactions = getDB().querySQL("SELECT * FROM fFactions");
			while(allFactions.next()){
				try {
					int factionId = allFactions.getInt("factionId");
					ResultSet allPlayers = getDB().querySQL("SELECT * FROM fPlayers WHERE factionId=" + factionId);
					ResultSet allChunks = getDB().querySQL("SELECT * FROM fChunks WHERE factionId=" + factionId);

					Faction f = new Faction(allFactions, allPlayers, allChunks);
					loadedFactions.put(factionId, f);
					displayNames.put(f.getName().toLowerCase(), factionId);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			relationships = new ArrayList<FactionRelationship>();
			ResultSet allRelationships = getDB().querySQL("SELECT * FROM fRelationship");
			while(allRelationships.next()){
				try {
					FactionRelationship relationship = new FactionRelationship(allRelationships);
					relationships.add(relationship);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		} catch(Exception unused){}

		loadedFactions.put(Faction.WILDERNESS.getFactionId(), Faction.WILDERNESS);
		loadedFactions.put(Faction.WARZONE.getFactionId(), Faction.WARZONE);
		loadedFactions.put(Faction.SAFEZONE.getFactionId(), Faction.SAFEZONE);
	}

	public void removeClearedFactions(){
		Collection<Faction> values = Collections.unmodifiableCollection(loadedFactions.values());
		List<Faction> toDelete = new ArrayList<Faction>();

		for(Faction faction : values){
			if(faction.getPlayers().size() == 0 && !faction.isDefault()){
				toDelete.add(faction);
			}
		}

		for(Faction faction : toDelete){
			faction.delete();
		}
	}

	public void remove(int id){
		for(FactionRelationship relationship : relationships){
			if(relationship.getFirst() == id || relationship.getSecond() == id){
				relationship.setType(FactionRelationshipType.NEUTRAL);
				relationship.save(false);
			}
		}
		loadedFactions.remove(id);
		displayNames.values().remove(id);
	}
	public void changeTag(int id, String tag){
		displayNames.values().remove(id);
		displayNames.put(tag, id);
	}
	public Faction matchFaction(String faction){
		Faction f = getFaction(faction);
		if(f != null){
			return f;
		}

		Player player = getServer().getPlayer(faction);
		if(player != null){
			FactionPlayer fplayer = getPlayersManager().getPlayer(player);
			if(fplayer != null)
				return getFaction(fplayer);
		}

		return null;
	}

	public void createFaction(FactionPlayer player, String name){
		Faction f = new Faction(name);

		loadedFactions.put(f.getFactionId(), f);
		displayNames.put(name.toLowerCase(), f.getFactionId());
		saveFaction(f, false);

		f.addMember(player);
	}

	public FactionRelationshipType getRelationship(Faction first, Faction second){
		if(first.getFactionId() == second.getFactionId())
			return FactionRelationshipType.SAME;
		for(FactionRelationship relationship : relationships){
			if(first.getFactionId() == relationship.getFirst() && second.getFactionId() == relationship.getSecond()){
				return relationship.getType();
			} else if(second.getFactionId() == relationship.getFirst() && first.getFactionId() == relationship.getSecond()){
				return relationship.getType();
			}
		}

		return FactionRelationshipType.NEUTRAL;
	}

	public List<Faction> getAll(Faction first, FactionRelationshipType relation){
		List<Faction> factions = new ArrayList<Faction>();
		for(FactionRelationship relationship : relationships){
			if(relationship.getFirst() == first.getFactionId()){
				if(relation == relationship.getType())
					factions.add(getFaction(relationship.getSecond()));
			} else if(relationship.getSecond() == first.getFactionId()){
				if(relation == relationship.getType())
					factions.add(getFaction(relationship.getFirst()));
			}
		}

		return factions;
	}

	public void setRelationship(Faction first, Faction second, FactionRelationshipType type){
		first.getAlly().remove((Integer)second.getFactionId());
		first.getNeutral().remove((Integer)second.getFactionId());

		second.getAlly().remove((Integer)first.getFactionId());
		second.getNeutral().remove((Integer)first.getFactionId());

		for(FactionRelationship relationship : relationships){
			if(first.getFactionId() == relationship.getFirst() && second.getFactionId() == relationship.getSecond()){
				relationship.setType(type);
				relationship.save(false);
				return;
			} else if(second.getFactionId() == relationship.getFirst() && first.getFactionId() == relationship.getSecond()){
				relationship.setType(type);
				relationship.save(false);
				return;
			}
		}
		FactionRelationship relation = new FactionRelationship(first, second, type);
		relation.save(false);
		relationships.add(relation);
	}

	public Faction getFaction(String name){
		Integer id = displayNames.get(name.toLowerCase());
		return id == null ? null : getFaction(id);
	}

	public Faction getFaction(int id){
		return loadedFactions.get(id);
	}

	public Faction getFaction(FactionPlayer player){
		return loadedFactions.get(player.getFactionId());
	}

	public void saveFactions(boolean synchronously){
		for(FactionRelationship relationship : relationships){
			saveRelation(relationship, synchronously);
		}
		for(Faction faction : loadedFactions.values()){
			saveFaction(faction, synchronously);
		}
	}

	public void saveRelation(FactionRelationship relationship, boolean synchronously){
		try {
			if(!synchronously)
				getDB().updateAsynchrounously(relationship.getSQLUpdate());
			else getDB().updateSQL(relationship.getSQLUpdate());
		} catch(Exception e){}
	}
	public void saveFaction(Faction faction, boolean synchronously){
		try {
			if(faction.isDefault()) return;
			if(!synchronously)
				getDB().updateAsynchrounously(faction.getSQLUpdate());
			else getDB().updateSQL(faction.getSQLUpdate());
		} catch(Exception e){}
	}
}
