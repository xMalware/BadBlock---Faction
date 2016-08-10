package com.lelann.factions.api;

import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.FactionObject;
import com.lelann.factions.api.Faction.FactionRank;
import com.lelann.factions.runnables.PowerRunnable;
import com.lelann.factions.scoreboard.FactionBoard;
import com.lelann.factions.utils.ChatUtils;

import lombok.Getter;
import lombok.Setter;

public class FactionPlayer extends FactionObject {
	public static final long MS_AWAY = 15 * 24 * 3600 * 1000;
	
	@Getter private final UUID uniqueId;
	@Getter private String lastUsername;
	@Getter @Setter private int factionId;
	@Getter @Setter private double power;
	@Getter @Setter private FactionRank factionRank;
	@Getter @Setter private String title;
	@Getter @Setter private FactionChat chat;
	@Getter @Setter private boolean autoclaim, autounclaim, map, bypass;
	@Getter @Setter private long lastConnection;
	@Getter private BukkitTask powerTask;
	@Getter@Setter private long money;
	@Getter private FactionBoard scoreboard;
	
	@Getter@Setter private int duelWins, duelLooses;
	
	@Getter private String groupCache, displayGroupCache;
	
	private boolean mustCreate;
	
	
	public FactionPlayer(ResultSet result) throws Exception {
		this.uniqueId = UUID.fromString(result.getString("uniqueId"));
		this.lastUsername = result.getString("lastUsername");
		this.title = result.getString("title");
		this.factionId = result.getInt("factionId");
		this.power = result.getDouble("power");
		this.factionRank = FactionRank.matchRank(result.getString("rank"));
		this.chat = FactionChat.PUBLIC;
		this.lastConnection = result.getLong("lastConnection");
		this.money = result.getLong("money");
		this.duelWins = result.getInt("duelWins");
		this.duelLooses = result.getInt("duelLooses");
		
		if((lastConnection + MS_AWAY) < new Date().getTime()){
			Faction faction = getFactionsManager().getFaction(factionId);
			if(faction != null && !faction.isWilderness()){
				leaveFaction();
				
				faction.getPlayers().remove(uniqueId);
				if(faction.getPlayers().size() == 0){
					faction.delete();
				}
				
				save(true);
			}
		}
		
		this.mustCreate = false;
	}
	
	public FactionPlayer(Player player){
		this.uniqueId = player.getUniqueId();
		this.lastUsername = player.getName();
		this.factionId = Faction.WILDERNESS.getFactionId();
		this.power = FactionConfiguration.getInstance().getMaxPower();
		this.chat = FactionChat.PUBLIC;
		this.lastConnection = new Date().getTime();
		this.money = 100L;
		
		this.mustCreate = true;
		
		update(player);
	}
	
	public void reloadGroupCache(Player p){
		this.displayGroupCache = getPermissions().getPrefix(p);
		this.displayGroupCache += getPermissions().getSuffix(p);
		
		if(!displayGroupCache.endsWith(" ") && !displayGroupCache.isEmpty()){
			displayGroupCache += " ";
		}
		if(displayGroupCache.startsWith(" ") && displayGroupCache.length() > 1){
			displayGroupCache = displayGroupCache.substring(2, displayGroupCache.length());
		}
		
		this.groupCache = getPermissions().getGroup(p);
	}
	
	public Player getPlayer(){
		return getServer().getPlayer(uniqueId);
	}
	
	public String getDisplayName(){
		String result = factionRank.getRankName();
		if(title != null && !title.equals("null")){
			result += "-" + title;
		}
		result += " " + lastUsername;
		return result;
	}
	
	public void sendMessage(String... messages){
		Player player = getPlayer();
		if(player != null){
			for(String message : messages)
				ChatUtils.sendMessage(player, message);
		}
	}
	
	public void update(Player player){
		this.lastUsername = player.getName();
		powerTask = new PowerRunnable(this).start();
		scoreboard = new FactionBoard(this); scoreboard.show(player);
		
		reloadGroupCache(player);
	}

	public Faction getFaction(){
		return getFactionsManager().getFaction(factionId);
	}
	
	public void addPower(double power){
		double maxPower = FactionConfiguration.getInstance().getMaxPower();
		if(power + this.power > maxPower)
			power = maxPower - this.power;
		this.power += power;
		
		if(scoreboard != null)
			scoreboard.generate();
	}
	
	public void removePower(double power){
		power = - Math.abs(power);
		double minPower = FactionConfiguration.getInstance().getMinPower();
		if(this.power + power < minPower)
			power = minPower - this.power;
		this.power += power;

		if(scoreboard != null)
			scoreboard.generate();
	}
	
	public void addMoney(long money){
		if(money < 0) this.money -= money;
		else this.money += money;
		
		if(scoreboard != null)
			scoreboard.generate();
	}
	
	public void removeMoney(long money){
		if(money < 0) this.money += money;
		else this.money -= money;
		
		if(scoreboard != null)
			scoreboard.generate();
	}

	public boolean hasEnough(long money){
		return this.money >= Math.abs(money);
	}
	
	public void leaveFaction(){
		this.factionRank = null;
		this.factionId = Faction.WILDERNESS.getFactionId();
		this.title = null;
		
		if(scoreboard != null)
			scoreboard.generate();
	}
	
	public void save(boolean sync){
		getPlayersManager().savePlayer(this, sync);
		
		if(scoreboard != null)
			scoreboard.generate();
	}
	
	public void disconnect(){
		lastConnection = new Date().getTime();
	}
	
	public String getSQLUpdate(){
		if(mustCreate){
			mustCreate = false;
			return "INSERT INTO fPlayers(uniqueId, lastUsername, factionId, power, rank, title, lastConnection, money, duelWins, duelLooses) "
					+ "VALUES('" + uniqueId + "', '" + lastUsername + "', " + factionId + ", " + power + ", '" + factionRank 
					+ "', '" + title 
					+ "', " + lastConnection + ", " + money 
					+ ", " + duelWins + ", " + duelLooses + ")";
		} else {
			return "UPDATE fPlayers SET "
					+ "lastUsername='" + lastUsername + "'"
					+ ", factionId=" + factionId
					+ ", power=" + power 
					+ ", rank='" + factionRank + "'"
					+ ", title='" + title + "'"
					+ ", lastConnection='" + lastConnection + "'"
					+ ", money='" + money + "'"
					+ ", duelWins='" + duelWins + "'"
					+ ", duelLooses='" + duelLooses + "'"
					+ " WHERE uniqueId='" + uniqueId + "'";
		}
	}
	
	public static enum FactionChat {
		PUBLIC("p", "public"),
		FACTION("f", "faction"),
		ALLY("a", "alliés");
		
		@Getter private final String start;
		private final String word;
		
		@Override
		public String toString(){
			return word;
		}
		
		private FactionChat(String start, String word){
			this.start = start;
			this.word = word;
		}
		
		public static FactionChat matchChat(String c){
			c = c.toLowerCase();
			for(FactionChat chat : values())
				if(c.startsWith(chat.getStart()))
					return chat;
			
			return null;
		}
	}
}
