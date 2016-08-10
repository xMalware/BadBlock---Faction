package com.lelann.factions.api;

import java.sql.ResultSet;

import lombok.Getter;
import lombok.Setter;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.FactionObject;

public class FactionRelationship extends FactionObject{
	@Getter private int first, second;
	@Getter @Setter private FactionRelationshipType type;
	private boolean mustCreate;

	public FactionRelationship(ResultSet result) throws Exception {
		first = result.getInt("firstFactionId");
		second = result.getInt("secondFactionId");
		type = FactionRelationshipType.matchRelationship(result.getString("type"));
		mustCreate = false;
	}
	public FactionRelationship(Faction first, Faction second, FactionRelationshipType type){
		this.first = first.getFactionId();
		this.second = second.getFactionId();
		this.type = type;
		
		mustCreate = true;
	}
	
	public String getSQLUpdate(){
		if(type == FactionRelationshipType.NEUTRAL){
			mustCreate = true;
			return "DELETE FROM fRelationship WHERE firstFactionId=" + first + " AND secondFactionId=" + second;
		} else if(mustCreate){
			mustCreate = false;
			return "INSERT INTO fRelationship(firstFactionId, secondFactionId, type) VALUES("
					+ first + ", " + second + ", '" + type + "')";
		} else {
			return "UPDATE fRelationship SET "
					+ "type='" + type + "'"
					+ " WHERE firstFactionId=" + first + " AND secondFactionId=" + second;
		}
	}

	public void save(boolean sync){
		getFactionsManager().saveRelation(this, sync);
	}
		
	public static enum FactionRelationshipType {
		ENEMY("enemy"),
		ALLY("ally"),
		NEUTRAL("neutral"),
		SAME("same");

		private 	String recognizer;
		private FactionRelationshipType(String recognizer){
			this.recognizer = recognizer;
		}

		public String getName(){
			return recognizer;
		}

		public String getColor(){
			FactionConfiguration config = FactionConfiguration.getInstance();
			switch(this){
				case ENEMY: return config.getEnemyColor();
				case ALLY: return config.getAllyColor();
				case NEUTRAL: return config.getNeutralColor();
				case SAME: return config.getSameColor();
			}
			return null;
		}

		public static FactionRelationshipType matchRelationship(String recognizer){
			FactionRelationshipType type = null;
			try {
				type = valueOf(recognizer);
			} catch(Exception e){}
			
			if(type != null) return type;

			for(FactionRelationshipType rs : values()){
				if(rs.toString().equalsIgnoreCase(recognizer))
					return rs;
			}
			return NEUTRAL; // default
		}
	}
}
