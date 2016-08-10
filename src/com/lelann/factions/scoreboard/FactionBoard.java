package com.lelann.factions.scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.lelann.factions.FactionObject;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.utils.ChatUtils;

public class FactionBoard extends FactionObject {
	private Scoreboard scoreboard;
	private FormattedObjective objective;
	
	private FactionPlayer player;
	
	private List<Score> informations; private String blank = "";
	
	public FactionBoard(FactionPlayer player){
		informations = new ArrayList<Score>();
		scoreboard = getServer().getScoreboardManager().getNewScoreboard();
		
		objective = new FactionObjective(player, scoreboard);
		
//		health = scoreboard.registerNewObjective("health", "dummy");
//		faction = scoreboard.registerNewObjective("faction", "dummy");
		
//		health.setDisplaySlot(DisplaySlot.BELOW_NAME);
//		faction.setDisplaySlot(DisplaySlot.SIDEBAR);
		
//		health.setDisplayName(ChatUtils.colorReplace("&c❤"));
//		setDisplayName("&8«&b-&8» &b&lFactions &8«&b-&8»");
		
		this.player = player;
		
//		new BukkitRunnable(){
//			@Override
//			public void run(){
//				for(Player p : Bukkit.getOnlinePlayers()){
//					change(p);
//					updatePlayerPrefix(getPlayersManager().getPlayer(p));
//				}
//			}
//		}.runTaskLater(Main.getInstance(), 5L);

		generate();
	}
	
	public void setDisplayName(String displayName){
		objective.setDisplayName(displayName); 
//		faction.setDisplayName(ChatUtils.colorReplace(displayName));
	}
	
	public void change(Player player){
//		Score score = health.getScore(player.getName());
//		score.setScore((int)player.getHealth());
	}
	
	public void show(){
		show(player.getPlayer());
	}
	
	public void show(Player player){
		player.setScoreboard(scoreboard);
	}
	
	public void generate(){
//		String name = faction.getName().equals("faction") ? "faction2" : "faction";
//
//		blank = " ";
//		informations.clear();
//
//		try {
//			temp = scoreboard.registerNewObjective(name, "dummy");
//		} catch(Exception e){
//			return;
//		}
//		
//		temp.setDisplayName(faction.getDisplayName());
//		
//		add("", temp);
//		Faction f = player.getFaction();
//		
//		if(f == null){
//			player.leaveFaction();
//			f = Faction.WILDERNESS;
//		}
//		
//		if(!f.isDefault()){
//			add("&a" + player.getFactionRank().getRankName() + " de " + f.getName(), temp);
//			add(" &2> &a" + f.getPlayers().size() + " membres", temp);
//			add(" &2> &a" + f.getChunkNumber() + " &aclaims dont " + f.getApChunkNumber() + " AP", temp);
//			add("", temp);
//		} else {
//			add("&aAucune faction", temp);
//			add("", temp);
//		}
//		
//		add("&aPower : " + MathsUtils.round(player.getPower(), 2) + "&2/&a" + FactionConfiguration.getInstance().getMaxPower(), temp);
//		add("&aMonnaie : " + player.getMoney() + "&2$", temp);
//		
//		temp.setDisplaySlot(DisplaySlot.SIDEBAR);
//		faction.unregister();
//		faction = temp;
//		
//		temp = null;
		objective.generate();
	}
	
	public void add(String information, Objective obj){
		information = ChatUtils.colorReplace(information);
		if(information.isEmpty()){
			blank += " ";
			addScore(blank, obj);
		} else if(information.length() > 16){
			String prefix = information.substring(0, information.length() - 16);
			String other = information.substring(information.length() - 16, information.length());

			Team team = scoreboard.getTeam(prefix);
			if(team == null)
				team = scoreboard.registerNewTeam(prefix);
			
			team.addEntry(other);
			team.setPrefix(prefix);

			addScore(other, obj);
		} else {
			addScore(information, obj);
		}
	}
	
	public void updatePlayerPrefix(FactionPlayer p){
//		String group = p.getGroupCache();
//		String prefix = FactionConfiguration.getInstance().getPrefixByGroup(group);
//		
//		if(prefix == null){
//			return;
//		}
//		
//		String groupTeam = FactionConfiguration.getInstance().getScoreboardGroup(group);
//
//		Team team = scoreboard.getTeam(groupTeam);
//		if(team == null)
//			team = scoreboard.registerNewTeam(groupTeam);
//		
//		if(prefix.length() > 16){
//			prefix = prefix.substring(0, 16);
//		}
//		
//		team.addEntry(p.getLastUsername());
//		team.setPrefix(ChatUtils.colorReplace(prefix));
	}
	
	private void addScore(String information, Objective faction){
		Score score = faction.getScore(information);
		score.setScore(informations.size() * -1 - 1);
		informations.add(score);
	}
}
