package com.lelann.factions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.lelann.factions.utils.CreatureType;

import lombok.Getter;

public class FactionConfiguration {
	private ConfigurationSection config;
	
	@Getter private static FactionConfiguration instance;
	@Getter private double minPower, maxPower, powerLostByDeath, powerEarnedByTime;
	@Getter private int timeBetweenPowerRegen;
	
	@Getter private List<String> allowedWorlds, disallowedInteractObject, allowedApNumberByPlayers, prefixsByGroups;
	
	@Getter private int timeBeforeTeleportation;
	
	@Getter private List<String> stuffItems, disallowedCommands;
	@Getter private int fightTime;
	@Getter private String canNotAttackWhenNostuff = "&cVous ne pouvez pas taper un joueur en étant sans stuff !",
			canNotAttackANostuff = "&cVous ne vous pouvez pas tuer un joueur no stuff !",
			beginFight = "&aVous êtes maintenant en combat ! Ne vous déconnectez pas !",
			endFight = "&aVous n'êtes plus en combat. Vous pouvez vous déconnecter !",
			disconnectWhilstFighting = "&c%player% a déconnecté en combat !",
			cannotUseCommandWhilstFighting = "&cVous n'avez pas le droit d'utiliser cette commande en combat !";
	
	@Getter private Map<CreatureType, Integer> mobValues;
	@Getter private String welcomeTitle, welcomeSubTitle;
	
	public String getPrefixByGroup(String group){
		for(String s : prefixsByGroups){
			String[] split = s.split(":");
			if(split[0].equalsIgnoreCase(group))
				return split[1];
		}
		
		return null;
	}
	
	public String getScoreboardGroup(String group){
		for(String s : prefixsByGroups){
			String[] split = s.split(":");
			if(split[0].equalsIgnoreCase(group))
				return split[2];
		}
		
		return null;
	}
	
	public int allowedApNumber(int players){
		int actu = 0, allowed = 0;
		for(String apNumber : allowedApNumberByPlayers){
			try {
				String[] splitted = apNumber.split(":");
				int playersNumber = Integer.parseInt(splitted[0]);
				if(playersNumber <= players && playersNumber > actu){
					allowed = Integer.parseInt(splitted[1]);
					actu = playersNumber;
				}
			} catch(Exception unused){}
		}
		
		return allowed;
	}
	@Getter 
	private String wildernessFactionName,
				   wildernessFactionDescription,
				   wildernessFactionColor,

				   apFactionName,
				   apFactionDescription,
				   apFactionColor,

				   safezoneFactionName,
				   safezoneFactionDescription,
				   safezoneFactionColor,
				   
				   warzoneFactionName,
				   warzoneFactionDescription,
				   warzoneFactionColor,
				   
				   neutralColor,
				   enemyColor,
				   allyColor,
				   sameColor,

				   leaderPrefix,
				   moderatorPrefix,

				   ip,
				   port,
				   username,
				   password,
				   database;
	
	@SuppressWarnings("serial")
	public FactionConfiguration(ConfigurationSection config){
		instance = this;
		this.config = config;
		
		ip = get("ip", "127.0.0.1");
		port = get("port", "3306");
		username = get("username", "root");
		password = get("password", "password");
		database = get("database", "factions");
		
		timeBeforeTeleportation = get("timeBeforeTeleportation", 5);
		
		minPower = get("minPower", -10.0d);
		maxPower = get("maxPower", 10.0d);
		powerLostByDeath = get("powerLostByDeath", 3.0d);
		timeBetweenPowerRegen = get("timeBetweenPowerRegen", 60);
		powerEarnedByTime = get("powerEarnedByTime", 3.0d);
		
		allowedWorlds = get("allowedWorlds", new ArrayList<String>(){{add("world");}});
		disallowedInteractObject = get("disallowedInteractObject", new ArrayList<String>(){{add("LEVER");}});
		allowedApNumberByPlayers = get("allowedApNumberByPlayers", new ArrayList<String>(){{add("1:1");add("3:2");add("7:3");add("10:4");add("15:5");}});

		wildernessFactionName = get("wildernessFactionName", "WilderNess");
		wildernessFactionDescription = get("wildernessFactionDescription", "Une description");
		wildernessFactionColor = get("wildernessFactionColor", "&3");
		
		apFactionName = get("apFactionName", "AP");
		apFactionDescription = get("apFactionDescription", "/f claim");
		apFactionColor = get("apFactionColor", "&6");

		safezoneFactionName = get("safezoneFactionName", "SafeZone");
		safezoneFactionDescription = get("safezoneFactionDescription", "Une description");
		safezoneFactionColor = get("safezoneFactionColor", "&4");
		
		warzoneFactionName = get("warzoneFactionName", "WarZone");
		warzoneFactionDescription = get("warzoneFactionDescription", "Une description");
		warzoneFactionColor = get("warzoneFactionColor", "&5");
		
		welcomeTitle = get("welcomeTitle", "&cFaction BadBlock");
		welcomeSubTitle = get("welcomeSubTitle", "&cBienvenue sur le Faction");
		
		neutralColor = get("neutralColor", "&a");
		enemyColor = get("enemyColor", "&b");
		allyColor = get("allyColor", "&c");
		sameColor = get("sameColor", "&d");

		leaderPrefix = get("leaderPrefix", "Chef-");
		moderatorPrefix = get("moderatorPrefix", "Modo-");
		
		prefixsByGroups = get("prefixsByGroups", new ArrayList<String>(){{add("administrateur:&4[&cAdmin&4] :0");}});

		stuffItems = get("fairpvp.stuffItems", new ArrayList<String>(){{add("1");add("2");}});
		disallowedCommands = get("fairpvp.disallowedCommands", new ArrayList<String>(){{add("spawn");add("tpa");}});

		canNotAttackWhenNostuff = get("fairpvp.canNotAttackWhenNostuff", canNotAttackWhenNostuff);
		canNotAttackANostuff = get("fairpvp.canNotAttackANostuff", canNotAttackANostuff);
		fightTime = get("fairpvp.fightTime", fightTime);
		beginFight = get("fairpvp.beginFight", beginFight);
		endFight = get("fairpvp.endFight", endFight);
		disconnectWhilstFighting = get("fairpvp.disconnectWhilstFighting", disconnectWhilstFighting);
		cannotUseCommandWhilstFighting = get("fairpvp.cannotUseCommandWhilstFighting", cannotUseCommandWhilstFighting);
	
		mobValues = new HashMap<>();
		for(CreatureType ct : CreatureType.values()){
			mobValues.put(ct, get("mobs." + ct.getName(), 2));
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key, T defaultValue){
		if(!config.contains(key))
			config.set(key, defaultValue);
		if(!(config.get(key).getClass().isInstance(defaultValue)))
			config.set(key, defaultValue);
		return (T) config.get(key);
	}
}
