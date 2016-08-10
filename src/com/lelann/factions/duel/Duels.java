package com.lelann.factions.duel;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.lelann.factions.Main;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.utils.ChatUtils;
import com.lelann.factions.utils.MathsUtils;

import fr.devhill.socketinventory.json.bukkit.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class Duels {
	@Getter private static Duels instance;

	@Getter private List<Location> arenas;
	private List<Location> allArenas;

	@Getter private Map<UUID, Duel> duelsByPlayer = new HashMap<>();

	@Getter private Map<UUID, PreDuel> preparing = new HashMap<>();
	@Getter private Map<UUID, PreDuel> propositions = new HashMap<>();

	private Map<UUID, DuelStuff> cache = new HashMap<>();

	private FileConfiguration config;
	private File configFile;

	public Duels(FileConfiguration config, File configFile){
		instance = this;

		this.config = config;
		this.configFile = configFile;

		if(!config.contains("arenas")){
			config.createSection("arenas");
		}

		arenas = new ArrayList<>();
		allArenas = new ArrayList<>();

		for(String key : config.getConfigurationSection("arenas").getKeys(false)){
			Location loc = new Location(Bukkit.getWorld(config.getString("arenas." + key + ".world"))
					, config.getDouble("arenas." + key + ".x")
					, config.getDouble("arenas." + key + ".y")
					, config.getDouble("arenas." + key + ".z")
					, (float) config.getDouble("arenas." + key + ".yaw")
					, (float) config.getDouble("arenas." + key + ".pitch"));

			arenas.add(loc.clone());
			allArenas.add(loc);
		}

		try {
			config.save(configFile);
		} catch (IOException e) {}
	}

	public void keepStuff(DuelStuff stuff, UUID player){
		stuff.write().save(getPlayerFile(player));
		cache.put(player, stuff);
	}

	public DuelStuff getKeepedStuff(UUID player){
		DuelStuff stuff = cache.get(player);
		if(stuff != null){
			getPlayerFile(player).delete();
			cache.remove(player);
		} else if(getPlayerFile(player).exists()){
			stuff = new DuelStuff(JSON.load(getPlayerFile(player)));
			getPlayerFile(player).delete();
		}

		return stuff;
	}

	private File getPlayerFile(UUID player){
		File parent = new File(Main.getInstance().getDataFolder(), "duelStuffs");
		if(!parent.exists()) parent.mkdirs();

		return new File(parent, player.toString());
	}

	private void saveArenas(){
		config.set("arenas", null);

		int i=0;
		for(Location loc : allArenas){
			config.set("arenas." + i + ".world", loc.getWorld().getName());
			config.set("arenas." + i + ".x", loc.getX());
			config.set("arenas." + i + ".y", loc.getY());
			config.set("arenas." + i + ".z", loc.getZ());
			config.set("arenas." + i + ".yaw", loc.getYaw());
			config.set("arenas." + i + ".pitch", loc.getPitch());

			i++;
		}

		try {
			config.save(configFile);
		} catch (IOException e) {}
	}

	public void onCommand(Player player, FactionPlayer fPlayer, String[] args){
		if(args.length == 0){
			if(player.hasPermission("duel.play")){
				fPlayer.sendMessage("&4/&cduel <player> %gold%pour envoyer une demande à quelqu'un");
				fPlayer.sendMessage("&4/&cduel accept <player> %gold%pour accepter un duel");
				fPlayer.sendMessage("&4/&cduel stats <player> %gold%pour afficher les stats d'un joueur");
				fPlayer.sendMessage("&4/&cduel top %gold%pour afficher les meilleurs duellistes");
			}
			if(player.hasPermission("duel.admin"))
				fPlayer.sendMessage("&4/&cduel addarena %gold%pour ajouter une arène");
		} else if(args[0].equalsIgnoreCase("accept") && args.length > 1){
			Player to = Bukkit.getPlayer(args[1]);
			if(to == null){
				fPlayer.sendMessage("&cLe joueur spécifié est introuvable !"); return;
			}

			PreDuel proposition = propositions.get(to.getUniqueId());
			if(proposition == null || !proposition.getTo().equals(player.getUniqueId())){
				fPlayer.sendMessage("&cCe joueur ne vous a pas proposé de Duel.");
			} else {
				propositions.remove(to.getUniqueId());
				if(arenas.size() == 0){
					fPlayer.sendMessage("&cDésolé, plus aucun arène n'est disponible ! Duel annulé.");
					ChatUtils.sendMessage(to, "&cDésolé, plus aucun arène n'est disponible ! Duel annulé.");
				} else {
					Duel newDuel = new Duel(arenas.get(0), proposition);
					arenas.remove(0);
					duelsByPlayer.put(proposition.getFrom(), newDuel);
					duelsByPlayer.put(proposition.getTo(), newDuel);
				}
			}
		} else if(args[0].equalsIgnoreCase("stats")){
			Player to = Bukkit.getPlayer(args.length > 1 ? args[1] : player.getName());
			if(to == null){
				fPlayer.sendMessage("&cLe joueur spécifié est introuvable !"); return;
			}

			FactionPlayer fTo = Main.getInstance().getPlayersManager().getPlayer(to);

			double ratio = (double) fTo.getDuelWins() / (double) (fTo.getDuelLooses() == 0 ? 1 : fTo.getDuelLooses());
			ratio = MathsUtils.round(ratio, 2);

			ChatUtils.sendMessage(player, "&8&l«&b&l-&8&l»&m---------&f&8&l«&b&l-&8&l»&b &b&lDuel &8&l«&b&l-&8&l»&m---------&f&8&l«&b&l-&8&l»");
			ChatUtils.sendMessage(player, "%red%> %gold%Duels gagnés : %red%" + fTo.getDuelWins());
			ChatUtils.sendMessage(player, "%red%> %gold%Duels perdus : %red%" + fTo.getDuelLooses());
			ChatUtils.sendMessage(player, "%gold%Le ratio de " + fTo.getLastUsername() + " est donc de %red%" + ratio + " %gold%!");
			ChatUtils.sendMessage(player, "&8&l«&b&l-&8&l»&m-----------------------------&f&8&l«&b&l-&8&l»&b");
		} else if(args[0].equalsIgnoreCase("top")){
			ChatUtils.sendMessage(player, "%red%Patientez pendant la recherche du top ...");
			new Thread(){
				@Override
				public void run(){
					try {
						ResultSet set = Main.getInstance().getDB().querySQL("SELECT * FROM fPlayers ORDER BY duelWins / IF(duelLooses = 0, 1, duelLooses) DESC LIMIT 0,5 ");

						ChatUtils.sendMessage(player, "&8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»&b &b&lDuel &8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»");
						ChatUtils.sendMessage(player, "%gold%Les meilleurs joueurs au Duel sont :");
						int i=1;
						while(set.next()){
							int wins = set.getInt("duelWins");
							int looses = set.getInt("duelLooses");

							double ratio = (double) wins / (double) looses == 0 ? 1 : looses;
							ratio = MathsUtils.round(ratio, 2);

							ChatUtils.sendMessage(player, "%gold%" + i + ") " + set.getString("lastUsername") + " %red%> %gold%Ratio de %red%" + ratio);
							i++;
						}
						ChatUtils.sendMessage(player, "&8&l«&b&l-&8&l»&m-----------------------------&f&8&l«&b&l-&8&l»&b");
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
					}
				}
			}.start();
		} else if(args[0].equalsIgnoreCase("addarena") && player.hasPermission("duel.admin")){
			arenas.add(player.getLocation());
			allArenas.add(player.getLocation());

			saveArenas();
			fPlayer.sendMessage("&aL'arène a bien été ajoutée !");
		} else {
			Player to = Bukkit.getPlayer(args[0]);
			if(to == null){
				fPlayer.sendMessage("&cLe joueur spécifié est introuvable !");
			} else if(to.getUniqueId().equals(player.getUniqueId())){
				fPlayer.sendMessage("&cC'est du suicide ou du masochisme ?");
			} else {
				PreDuel pre = propositions.get(player.getUniqueId());

				if(pre != null && pre.getTo().equals(to.getUniqueId())){
					fPlayer.sendMessage("&cVous avez déjà envoyé une demande à ce joueur, patientez.");
				} else {				
					PreDuel duel = new PreDuel(player.getUniqueId(), to.getUniqueId(), false);
					preparing.put(player.getUniqueId(), duel);
					player.openInventory(createDuelInventory());
				}
			}
		}
	}

	private Inventory createDuelInventory(){
		Inventory inv = Bukkit.createInventory(null, 9, ChatUtils.colorReplace("%red%Choisit le type de duel :"));
		ItemStack flower = new ItemStack(Material.YELLOW_FLOWER, 1),
				sword = new ItemStack(Material.DIAMOND_SWORD, 1);

		ItemMeta metaFlower = flower.getItemMeta(),
				metaSword = sword.getItemMeta();

		metaFlower.setDisplayName(ChatUtils.colorReplace("&aDuel amical"));
		metaFlower.setLore(Arrays.asList(new String[]{
				ChatUtils.colorReplace("%dgreen%> %green%Pas de perte de stuff (items, xp)"),
				ChatUtils.colorReplace("%dgreen%> %green%Pas de perte de power"),
				ChatUtils.colorReplace("%dgreen%> %green%Que du fun ! :D")
		}));

		metaSword.setDisplayName(ChatUtils.colorReplace("&cDuel à mort"));
		metaSword.setLore(Arrays.asList(new String[]{
				ChatUtils.colorReplace("%dred%> %red%Perte de stuff (items, xp) ... ou gain :p"),
				ChatUtils.colorReplace("%dred%> %red%Pas de perte de power"),
				ChatUtils.colorReplace("%dred%> %red%Que du fun ! :D")
		}));

		flower.setItemMeta(metaFlower);
		sword.setItemMeta(metaSword);

		inv.setItem(0, flower);
		inv.setItem(8, sword);

		return inv;
	}

	@Data@AllArgsConstructor public static class PreDuel {
		private UUID from, to;
		private boolean keepStuff;
	}
}
