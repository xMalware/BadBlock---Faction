package com.lelann.factions.api.managers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import com.lelann.factions.FactionObject;
import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionChunk;

public class ChunksManager extends FactionObject {
	private Map<String, FactionChunk> chunks;
	
	public ChunksManager(String world){
		world = world.toLowerCase();
		chunks = new HashMap<String, FactionChunk>();
		try {
			ResultSet allChunks = getDB().querySQL("SELECT * FROM fChunks WHERE world='" + world + "'");
			while(allChunks.next()){
				try {
					FactionChunk chunk = new FactionChunk(allChunks);
					chunks.put(asString(chunk.getX(), chunk.getZ()), chunk);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void removeAll(Faction f){
		for(FactionChunk fc : chunks.values()){
			if(f.equals(fc.getOwner())){
				fc.setFactionId(Faction.WILDERNESS.getFactionId());
				fc.getAllowedMembers().clear();
				fc.save(false);
			}
		}
	}
	
	public FactionChunk getFactionChunk(Chunk c){
		return chunks.get(asString(c));
	}
	
	public Faction getFactionAt(Chunk c){
		FactionChunk fc = chunks.get(asString(c));
		if(fc != null){
			Faction owner = fc.getOwner();
			if(owner != null) return owner;
		} 
		return Faction.WILDERNESS;
	}
	public Faction getFactionAt(Entity e){
		return getFactionAt(e.getLocation());
	}
	public Faction getFactionAt(Block b){
		return getFactionAt(b.getLocation());
	}
	public Faction getFactionAt(Location l){
		return getFactionAt(l.getChunk());
	}
	
	public FactionChunk claim(Faction f, Chunk c){
		String toString = asString(c);
		FactionChunk fc = chunks.get(toString);
		if(fc == null)
			fc = new FactionChunk(c);

		fc.setFactionId(f.getFactionId());
		fc.save(false);
		
		chunks.put(toString, fc);
		
		return fc;
	}
	
	
	public void setAp(Chunk c, boolean ap, Faction owner){
		FactionChunk fc = chunks.get(asString(c));
		if(fc == null) {
			fc = new FactionChunk(c);
			chunks.put(asString(c), fc);
		}
		
		fc.setAp(ap);
		fc.setFactionId(owner.getFactionId());
		fc.save(false);
	}
	public void unclaim(Chunk c){}
	
	public List<FactionChunk> listClaims(Faction faction, boolean addAP){
		List<FactionChunk> result = new ArrayList<FactionChunk>();
		for(FactionChunk fc : chunks.values()){
			if(faction.equals(fc.getOwner()) && (addAP || !fc.isAp()))
				result.add(fc);
		}
		return result;
	}
	
	public List<FactionChunk> listClaims(Faction faction){
		return listClaims(faction, true);
	}
	public List<FactionChunk> listAP(Faction faction){
		List<FactionChunk> result = new ArrayList<FactionChunk>();
		for(FactionChunk fc : chunks.values()){
			if(faction.equals(fc.getOwner()) && fc.isAp())
				result.add(fc);
		}
		return result;
	}
	
	private String asString(Chunk c){
		return c.getX() + "," + c.getZ();
	}
	private String asString(int x, int z){
		return x + "," + z;
	}
	
	public void saveChunks(boolean synchronously){
		for(final FactionChunk chunk : chunks.values()){
			saveChunk(chunk, synchronously);
		}
	}
	
	public void saveChunk(FactionChunk chunk, boolean synchronously){
		try {
			if(!synchronously)
				getDB().updateAsynchrounously(chunk.getSQLUpdate());
			else getDB().updateSQL(chunk.getSQLUpdate());
		} catch(Exception e){}
	}
}
