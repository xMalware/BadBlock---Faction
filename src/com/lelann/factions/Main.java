package com.lelann.factions;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lelann.factions.api.managers.ChunksManager;
import com.lelann.factions.api.managers.FactionsManager;
import com.lelann.factions.api.managers.PlayersManager;
import com.lelann.factions.commands.factions.CommandsManager;
import com.lelann.factions.database.Database;
import com.lelann.factions.database.MySQL;
import com.lelann.factions.duel.DuelListener;
import com.lelann.factions.duel.Duels;
import com.lelann.factions.listeners.AntiDupliListener;
import com.lelann.factions.listeners.ChatListener;
import com.lelann.factions.listeners.ConnectionListener;
import com.lelann.factions.listeners.DestroyChunkListener;
import com.lelann.factions.listeners.FightListener;
import com.lelann.factions.listeners.InfiniteAnvilListener;
import com.lelann.factions.listeners.MoveListener;
import com.lelann.factions.listeners.ScoreboardListener;
import com.lelann.factions.mojang.StrengthPotionNerfer_v1_8_R3;
import com.lelann.factions.permissions.AbstractPermissions;
import com.lelann.factions.scoreboard.HealthBoard;
import com.lelann.factions.scoreboard.RankBoard;

public class Main extends JavaPlugin {
	private static Main instance;
	public static Main getInstance(){
		return instance;
	}
	private Database database;
	private FactionsManager factionsManager = null;
	private PlayersManager playersManager = null;
	private Map<String, ChunksManager> chunksManagers = null;

	public Database getDB(){
		return database;
	}

	public FactionsManager getFactionsManager(){
		return factionsManager;
	}

	public PlayersManager getPlayersManager(){
		return playersManager;
	}

	public ChunksManager getChunksManager(World w){
		return w == null ? null : getChunksManager(w.getName());
	}

	public Collection<ChunksManager> getChunksManagers(){
		return chunksManagers.values();
	}

	public ChunksManager getChunksManager(String name){
		return name == null ? null : chunksManagers.get(name.toLowerCase());
	}

	@Override
	public void onEnable(){
		try {
			instance = this;
			AbstractPermissions.init();

			loadConfiguration();

			playersManager = new PlayersManager();
			factionsManager = new FactionsManager();
			chunksManagers = new HashMap<String, ChunksManager>();

			new CommandsManager();

			for(String world : FactionConfiguration.getInstance().getAllowedWorlds()){
				chunksManagers.put(world, new ChunksManager(world));
			}

			File file = new File(getDataFolder(), "duelArenas.yml");

			new Duels(YamlConfiguration.loadConfiguration(file), file);


			getServer().getPluginManager().registerEvents(new ChatListener(), this);
			getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
			getServer().getPluginManager().registerEvents(new DestroyChunkListener(), this);
			getServer().getPluginManager().registerEvents(new FightListener(), this);
			getServer().getPluginManager().registerEvents(new MoveListener(), this);
			getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
			getServer().getPluginManager().registerEvents(new InfiniteAnvilListener(), this);
			getServer().getPluginManager().registerEvents(new DuelListener(), this);
			getServer().getPluginManager().registerEvents(new AntiDupliListener(getDataFolder()), this);

			getServer().getPluginManager().registerEvents(new HealthBoard(), this);
			getServer().getPluginManager().registerEvents(new RankBoard(), this);

			saveConfig();

			factionsManager.removeClearedFactions();
			StrengthPotionNerfer_v1_8_R3.nerf();
			
			getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		} catch(Exception e){
			e.printStackTrace();
			System.out.println("\n\n\n\n");
			System.out.println("Une erreur grave a été détéctée pendant le chargement. Par mesure de sécurité, le serveur va s'éteindre (le Faction gère la protection de tout le monde principal).");
			System.out.println("Corrigez l'erreur pour lancer le faction.");
			
			Bukkit.shutdown();
		}
	}

	public void kick(Player p){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ConnectOther");
		out.writeUTF(p.getName());
		out.writeUTF("lobby");
		p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
	}
	
	public void loadConfiguration(){
		if(getConfig().getKeys(false).isEmpty()){
			File file = new File(getDataFolder(), "config.yml");
			if(file.exists() && file.length() > 128){
				File save = new File(getDataFolder(), "config.yml.broken." + System.nanoTime());

				System.out.println("[WARNING] BROKED CONFIGURATION FOR FACTION, SAVE AS " + save.getName());

				try {
					Runtime.getRuntime().exec("cp " + file.getAbsolutePath() + " " + save.getAbsolutePath());
				} catch (IOException e) {}
			}
		}

		new FactionConfiguration(getConfig());

		if(database != null)
			try {
				database.closeConnection();
			} catch (Exception e) {}

		database = new MySQL(FactionConfiguration.getInstance().getIp()
				, FactionConfiguration.getInstance().getPort()
				, FactionConfiguration.getInstance().getUsername()
				, FactionConfiguration.getInstance().getPassword()
				, FactionConfiguration.getInstance().getDatabase());
	}	
	@Override
	public void onDisable(){
		boolean whitelist = Bukkit.hasWhitelist();
		Bukkit.setWhitelist(true);
		
		for(Player p : Bukkit.getOnlinePlayers()){
			kick(p);
		}
		
		getPlayersManager().savePlayers(true);
		getFactionsManager().saveFactions(true);

		for(ChunksManager cm : chunksManagers.values()){
			cm.saveChunks(true);
		}
		
		Bukkit.setWhitelist(whitelist);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(label.equalsIgnoreCase("duel")){
			Duels.getInstance().onCommand((Player) sender, getPlayersManager().getPlayer(sender), args);
		} else CommandsManager.getInstance().useCommand(sender, args);

		return true;
	}
}		
