package me.archimede67.FactionUtils;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class CommandClearLag implements CommandExecutor {

	public SettingsManager config = SettingsManager.getInstance();
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Vous devez être un joueur pour executer cette commande !");
		} else {
			Player p = (Player) sender;
		if(args.length == 0) {
			List<Entity> entity = null;
			List<String> worlds = config.getCl().getStringList("ClearLag.Worlds");
			HashMap<Entity, String> en = new HashMap<Entity, String>();
			HashMap<Integer, String> items = new HashMap<Integer, String>();
			HashMap<String, String> mobs = new HashMap<String, String>();
			for(String world : Main.worlds) {
				entity = Bukkit.getWorld(world).getEntities();
				try {
				for(Entity entities : entity) {
					if(entities instanceof Item) {
						Item i = (Item) entities;
						for(int witems : config.getCl().getIntegerList("ClearLag.WhitelistedItems")) {
							items.put(witems, "whitelisted");
						}
						if(!items.containsKey(i.getItemStack().getTypeId())) {
							en.put(entities, "removed");
							entities.remove();
						}
					} else if(entities instanceof Monster) {
						Monster m = (Monster) entities;
						for(String wmobs : config.getCl().getStringList("ClearLag.WhitelistedMonsters")) {
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
                    result += "%gold%" + worlds.toString().toLowerCase();
                    ChatUtils.sendMessagePlayer(p, ChatUtils.colorReplace("%green%Vous avez supprimé%gold% " + en.size() + " %green%créatures dans les mondes " + result.replace("[", "").replace(",", "§a,§6").replace("]", "") + "%green%."));
                    mobs.clear();
                    items.clear();
                    en.clear();
			
		} else {
			p.sendMessage("§cErreur: arguments invalides !");
		}
	  }
		return true;
	}

}
