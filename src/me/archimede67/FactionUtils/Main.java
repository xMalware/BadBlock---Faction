package me.archimede67.FactionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public static List<String> worlds = new ArrayList<String>();

	public static Main instance;
	public static Main getInstance(){ return instance; }
	public static SettingsManager settings = SettingsManager.getInstance();

	public static String HAVE_REACHED_EDGE = null;
	public static String DropChancePlayer = null;
	public static String DropChanceMobs = null;
	public static int Rayon = 0;
	public static List<String> MessageClear = null;
	public static List<String> MessageBeforeClear = null;



	int Countdown;
	static int count;

	public void onEnable() {
		instance = this;
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		getCommand("factionutils").setExecutor(this);
		getCommand("spawner").setExecutor(new CommandsSpawner());
		getCommand("clearlag").setExecutor(new CommandClearLag());

		Countdown = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				count--;

				if(count == 60) {  
					for (String a : MessageBeforeClear) Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', a));
				}

				if(count == 0){
					List<Entity> entity = null;
					List<String> worlds = settings.getCl().getStringList("ClearLag.Worlds");
					HashMap<Integer, String> items = new HashMap<Integer, String>();
					HashMap<String, String> mobs = new HashMap<String, String>();
					HashMap<Entity, String> en = new HashMap<Entity, String>();
					for(String world : Main.worlds) {
						entity = Bukkit.getWorld(world).getEntities();
						try {
							for(Entity entities : entity) {
								if(entities instanceof Item && entities.getType().equals(EntityType.DROPPED_ITEM)) {
									Item i = (Item) entities;

									for(int witems : settings.getCl().getIntegerList("ClearLag.WhitelistedItems")) {
										items.put(witems, "whitelisted");
									}
									if(!items.containsKey(i.getItemStack().getTypeId())) {
										en.put(entities, "removed");
										entities.remove();
									}

								} else if(entities instanceof Monster) {
									Monster m = (Monster) entities;
									for(String wmobs : settings.getCl().getStringList("ClearLag.WhitelistedMonsters")) {
										mobs.put(wmobs, "whitelisted");
									}
									if(!mobs.containsKey(m.getType().getName())) {
										en.put(entities, "removed");
										entities.remove();
									}
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					String result = "";
					result += "§6" + worlds.toString().toLowerCase();
					for (String a : MessageClear) Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', a.replace("<items>", ""+en.size()).replace("<time>", settings.getCl().getInt("ClearLag.TimeClearLag") + "m").replace("<mobs>", Integer.toString(mobs.size()))));
					count = settings.getCl().getInt("ClearLag.TimeClearLag") * 60 + 1;
				}
			}
		}, 0L, 20L);


		settings.setup(this);
		settings.saveCl();
		settings.saveDr();
		settings.saveNt();
		settings.reloadCl();
		settings.reloadDr();
		settings.reloadNt();
	}

	public void onDisable() {

	}

	public static void loadConfig() {

		settings.saveCl();settings.saveDr();
		settings.reloadCl();settings.reloadDr();

		if(!settings.dfile.exists()) {
			settings.getDr().set("Drop.DropChancePlayer", 100);
			settings.getDr().set("Drop.DropChanceMobs", 100);
			settings.saveDr();
		}
		if(!settings.cfile.exists()) {
			settings.getCl().set("ClearLag.TimeClearLag", 1);
			settings.getCl().set("ClearLag.MessageBeforeClear", "&7[&cInfos&7] &aPour éviter toute source de &6lagg&a, tous les&b monstres néfastes&a et&b tous les items au sol&a vont être supprimés dans &c1 minute&a !");
			settings.getCl().set("ClearLag.MessageClear", "&7[&cInfos&7] &6<nombre> &acréatures ont été supprimées dans les mondes <world>.");
			settings.getCl().set("ClearLag.Worlds", Arrays.asList("world", "monde1"));
			settings.getCl().set("ClearLag.WhitelistedMonsters", Arrays.asList("Slime"));

			settings.saveCl();
		}
		// if(!settings.ntfile.exists()) {
		//	settings.getNt().set("NameTag.Legende", "&5[&dLegende&5] ");
		//	
		//	settings.saveNt();
		//}

		for(String worldsList : settings.getCl().getStringList("ClearLag.Worlds")) {
			worlds.add(worldsList);
		}
		DropChancePlayer = settings.getDr().getString("Drop.DropChancePlayer");
		DropChanceMobs = settings.getDr().getString("Drop.DropChanceMobs");
		MessageBeforeClear = settings.getCl().getStringList("ClearLag.MessageBeforeClear");
		MessageClear = settings.getCl().getStringList("ClearLag.MessageClear");
		count = settings.getCl().getInt("ClearLag.TimeClearLag") * 60 + 1;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage("§4-------------------------------------------------");
			sender.sendMessage("");
			sender.sendMessage("                  §bFaction§4-§aUtils§b by archimede67");
			sender.sendMessage("");
			sender.sendMessage("§4• §6Commandes :");
			sender.sendMessage("");
			sender.sendMessage("§4• §e/" + label + "§7 - Commande principale");
			sender.sendMessage("§4• §e/" + label + " reload§7 - Recharger le plugin");
			sender.sendMessage("");
			sender.sendMessage("§4-------------------------------------------------");
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("factionutils.reload"))
				{
					sender.sendMessage("§aDesactivation du plugin...");
					getServer().getPluginManager().disablePlugin(this);
					sender.sendMessage("§aActivation du plugin...");
					getServer().getPluginManager().enablePlugin(this);
					loadConfig();
					sender.sendMessage("§aLe plugin a été rechargé !");
				}
				return true;
			} else {
				sender.sendMessage("§cCommande invalide !");
			}
			return true;
		} else {
			sender.sendMessage("§cErreur: arguments invalides !");
		}
		return true;
	}

	public static EntityType getEntityType(String name){
		name = name.replace(" ", ""); name = name.replace("_", "");
		for(EntityType type : EntityType.values()){
			String typeName = type.name();
			typeName = typeName.replace("_", "");
			if(typeName.equalsIgnoreCase(name)){
				return type;
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static Block getSpawner(Player p){
		Block blockTarget = null;
		for (Block b : p.getLineOfSight(new HashSet<Material>(), 200)) {
			if (!b.getType().equals(Material.AIR)) { blockTarget = b; break; }
		}
		if(blockTarget == null || (blockTarget.getType() != Material.MOB_SPAWNER)){
			return null;
		}

		return blockTarget;
	}

	public static boolean isValide(EntityType type){
		if(type == EntityType.EXPERIENCE_ORB || type == EntityType.LEASH_HITCH || type == EntityType.PAINTING
				|| type == EntityType.ARROW || type == EntityType.SNOWBALL || type == EntityType.FIREBALL
				|| type == EntityType.SMALL_FIREBALL || type == EntityType.ENDER_PEARL || type == EntityType.ENDER_SIGNAL
				|| type == EntityType.THROWN_EXP_BOTTLE || type == EntityType.ITEM_FRAME || type == EntityType.WITHER_SKULL
				|| type == EntityType.PRIMED_TNT || type == EntityType.MINECART_COMMAND || type == EntityType.BOAT
				|| type == EntityType.MINECART || type == EntityType.MINECART_CHEST || type == EntityType.MINECART_FURNACE 
				|| type == EntityType.MINECART_TNT || type == EntityType.MINECART_HOPPER || type == EntityType.MINECART_MOB_SPAWNER
				|| type == EntityType.CREEPER || type == EntityType.SKELETON || type == EntityType.SPIDER
				|| type == EntityType.GIANT || type == EntityType.ZOMBIE || type == EntityType.SLIME
				|| type == EntityType.GHAST || type == EntityType.PIG_ZOMBIE || type == EntityType.ENDERMAN
				|| type == EntityType.CAVE_SPIDER || type == EntityType.SILVERFISH || type == EntityType.BLAZE
				|| type == EntityType.MAGMA_CUBE || type == EntityType.ENDER_DRAGON || type == EntityType.WITHER
				|| type == EntityType.BAT || type == EntityType.WITCH || type == EntityType.PIG
				|| type == EntityType.SHEEP || type == EntityType.COW || type == EntityType.CHICKEN
				|| type == EntityType.SQUID || type == EntityType.WOLF || type == EntityType.MUSHROOM_COW
				|| type == EntityType.SNOWMAN || type == EntityType.OCELOT || type == EntityType.IRON_GOLEM
				|| type == EntityType.HORSE || type == EntityType.VILLAGER || type == EntityType.ENDER_CRYSTAL)
			return true;
		else return false;
	}



}
