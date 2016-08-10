package com.lelann.factions.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.FactionObject;
import com.lelann.factions.Main;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.runnables.FRunnable;
import com.lelann.factions.utils.ChatUtils;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.events.PermissionEntityEvent;
import ru.tehkode.permissions.events.PermissionEntityEvent.Action;

public class RankBoard extends FactionObject implements Listener {
	@Getter private static RankBoard instance;
	private Map<String, String> displayRankByName, internalRankByName;


	private Scoreboard handler;

	public RankBoard(){
		instance = this;

		handler = Bukkit.getScoreboardManager().getNewScoreboard();

		displayRankByName = new HashMap<>();
		internalRankByName = new HashMap<>();

		for(String rank : FactionConfiguration.getInstance().getPrefixsByGroups()){
			String[] splitted = rank.split(":");
			displayRankByName.put(splitted[0], splitted[1]);
			internalRankByName.put(splitted[0], splitted[2]);
		}

		addInScoreboard(handler);
	}

	private void addInScoreboard(Scoreboard scoreboard){
		for(String key : displayRankByName.keySet()){
			String iKey = internalRankByName.get(key);
			Team team = scoreboard.getTeam(iKey) == null ? scoreboard.registerNewTeam(iKey) : scoreboard.getTeam(iKey);
			team.setPrefix(ChatUtils.colorReplace(displayRankByName.get(key)));
		}
	}

	public void setRank(FactionPlayer player, boolean connect){
		if(!connect) disconnect(player);
		
		Team team = handler.getTeam(internalRankByName.get(player.getGroupCache()));

		if(team != null) {
			team.addEntry(player.getLastUsername());
			updateTeam(team, false);
		}
	}

	public void disconnect(FactionPlayer player){
		Team team = handler.getEntryTeam(player.getLastUsername());
		if(team != null) {
			team.removeEntry(player.getLastUsername());
		}
	}

	public void createTeams(Player player){
		CraftScoreboard bukkit = (CraftScoreboard) handler;

		for(Team team : handler.getTeams()){
			bukkit.getHandle().getTeam(team.getName());
			PacketUtils.sendPacket(player, new PacketPlayOutScoreboardTeam(bukkit.getHandle().getTeam(team.getName()), 0));
		}
	}

	public void updateTeam(Team team, boolean remove){
		CraftScoreboard bukkit = (CraftScoreboard) handler;
		PacketUtils.broadcastPacket(new PacketPlayOutScoreboardTeam(bukkit.getHandle().getTeam(team.getName()), team.getEntries(), remove ? 4 : 3));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e){
		createTeams(e.getPlayer());

		final UUID uniqueId = e.getPlayer().getUniqueId();
		new FRunnable(20L){
			@Override
			public void run(){
				FactionPlayer player = getPlayersManager().getPlayer(uniqueId);
				if(player == null || player.getPlayer() == null) {
					return;
				}

				setRank(player, true);
			}
		}.startLater();
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent e){
		disconnect(getPlayersManager().getPlayer(e.getPlayer()));
	}

	@EventHandler
	public void onGroupChange(PermissionEntityEvent e){
		if(e.getAction() == Action.INHERITANCE_CHANGED && e.getEntity() instanceof PermissionUser){
			PermissionUser user = (PermissionUser) e.getEntity();
			new BukkitRunnable(){
				@Override
				public void run(){
					if(user.getPlayer() == null) return;
					FactionPlayer fp = getPlayersManager().getPlayer(user.getPlayer().getUniqueId());
					fp.reloadGroupCache(fp.getPlayer());

					setRank(fp, false);
				}
			}.runTaskLater(Main.getInstance(), 5L);
		}
	}
}