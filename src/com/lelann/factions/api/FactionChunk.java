package com.lelann.factions.api;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;

import com.lelann.factions.FactionObject;
import com.lelann.factions.utils.StringUtils;

import lombok.Getter;
import lombok.Setter;

public class FactionChunk extends FactionObject{
	@Getter @Setter private boolean ap;
	@Getter @Setter private int factionId;
	@Getter private int x, z;
	@Getter private String world;
	@Getter private List<UUID> allowedMembers;
	
	private boolean mustCreate;
	
	public FactionChunk(ResultSet chunk) throws Exception{
		this.ap = "true".equalsIgnoreCase(chunk.getString("ap"));
		this.factionId = chunk.getInt("factionId");
		this.world = chunk.getString("world");
		this.x = chunk.getInt("x");
		this.z = chunk.getInt("z");
		
		String allowed = chunk.getString("allowedMembers");
		
		this.allowedMembers = new ArrayList<UUID>();
		
		if(allowed != null){
			String[] allowedSplit = allowed.split(";");
			for(String uuid : allowedSplit){
				try {
					allowedMembers.add(UUID.fromString(uuid));
				} catch(Exception unused){}
			}
		}
		this.mustCreate = false;
	}
	
	public FactionChunk(Chunk c){
		this.world = c.getWorld().getName();
		this.x = c.getX();
		this.z = c.getZ();
		this.ap = false;
		this.factionId = Faction.WILDERNESS.getFactionId();
		this.allowedMembers = new ArrayList<UUID>();
		this.mustCreate = true;
	}
	
	public Faction getOwner(){
		return getFactionsManager().getFaction(factionId);
	}
	
	public void save(boolean sync){
		getChunksManager(world).saveChunk(this, sync);
	}
	
	public String getSQLUpdate(){
		if(factionId == Faction.WILDERNESS.getFactionId() && !isAp()){
			mustCreate = true;
			return "DELETE FROM fChunks WHERE x=" + x + " AND z=" + z + " AND world='" + world + "'";
		} else if(mustCreate){
			mustCreate = false;
			return "INSERT INTO fChunks(x, z, world, ap, factionId, allowedMembers) VALUES("
					+ x + ", " + z + ", '" + world + "', '" + ap + "', " + factionId + ", '" + StringUtils.join(allowedMembers, ";") + "')";
		} else {
			return "UPDATE fChunks SET "
					+ "ap='" + ap + "'"
					+ ", factionId=" + factionId
					+ ", allowedMembers='" + StringUtils.join(allowedMembers, ";") + "'"
						+ " WHERE x=" + x + " AND z=" + z + " AND world='" + world + "'";
		}
	}
}
