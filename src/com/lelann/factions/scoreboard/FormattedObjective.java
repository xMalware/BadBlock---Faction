package com.lelann.factions.scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Represent an objective which has big fields (32 characters) and can change quickly
 * 
 * You can use and modify this class, give sell and use plug-in created with it, but
 * you can't distribute it without quoting my name and this post (bukkit.fr)
 * 
 * @author DevHill
 */
public abstract class FormattedObjective {
	private final Scoreboard handler;
	
	private Objective used, cache;
	private final String objectiveName;
	
	private final List<String> informations;
	private String blank;
	
	private boolean generating;
	
	private String displayName;
	
	public FormattedObjective(Scoreboard handler, String objectiveName, String displayName) {
		this.handler = handler;
		this.objectiveName = objectiveName;
		
		this.informations = new ArrayList<String>();
		this.displayName = displayName;
	}
	
	/**
	 * Change the display name (you can use ChatColor and & codes)
	 * @param displayName The display name (max 32 characters)
	 */
	public void setDisplayName(final String displayName){
		this.displayName = displayName;
		used.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
	}
	
	/**
	 * Add informations in the objective (only while generating)
	 * You can use ChatColor and & codes. Max information length : 32 characters
	 * @param informations The informations (string array)
	 */
	public void addInformations(final String... informations){
		for(final String information : informations)
			addInformation(information);
	}
	
	/**
	 * Add a information in the objective (only while generating)
	 * You can use ChatColor and & codes. Max information length : 32 characters
	 * @param information The information (a string)
	 */
	public void addInformation(final String information){
		if(!generating)
			throw new RuntimeException("Can not add a score when the scoreboard isn't generating !");
		
		informations.add(information);
	}
	
	/**
	 * Create or update the objective
	 */
	public void generate(){
		informations.clear(); blank = "";
		
		cache = used;
		used = handler.registerNewObjective(cache == null || !cache.getName().equals(objectiveName) ? objectiveName : objectiveName + "2", "dummy");
		
		this.generating = true;
		generateInformations();
		this.generating = false;

		int line = informations.size() - 1;
		for(int i=0;i<informations.size();i++){
			setScore(informations.get(i), line);
			line--;
		}
		
		setDisplayName(displayName);
		used.setDisplaySlot(DisplaySlot.SIDEBAR);
		if(cache != null) cache.unregister();
	}
	
	private void setScore(String information, final int score){
		information = ChatColor.translateAlternateColorCodes('&', information);
		
		if(information.isEmpty()){
			blank += " ";
			setScoreInHandler(blank, score);
		} else if(information.length() > 16){
			final String prefix = information.substring(0, information.length() - 16);
			final String other = information.substring(information.length() - 16, information.length());
			
			Team team = handler.getTeam(prefix);
			if(team == null)
				team = handler.registerNewTeam(prefix);
			
			team.addEntry(other);
			team.setPrefix(prefix);

			setScoreInHandler(other, score);
		} else {
			setScoreInHandler(information, score);
		}
	}
	
	private void setScoreInHandler(final String name, final int score){
		Score scoreHandler = used.getScore(name);
		scoreHandler.setScore(score);
	}
	
	/**
	 * Call when the Objective is generating
	 */
	public abstract void generateInformations();
}
