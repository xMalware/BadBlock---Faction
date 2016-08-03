package me.archimede67.FactionUtils;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandsSpawner implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Vous devez être un joueur pour executer cette commande !");
		} else {
			Player p = (Player) sender;
		if(args.length == 0) {
			sender.sendMessage("§4-------------------------------------------------");
			sender.sendMessage("");
			sender.sendMessage("                  §bFaction§4-§aUtils§b by archimede67");
			sender.sendMessage("");
			sender.sendMessage("§4• §6Commandes :");
			sender.sendMessage("");
			sender.sendMessage("§4• §e/" + label + "§7 - Commande spawner.");
			sender.sendMessage("§4• §e/" + label + " give <mob>§7 - Se donner 1 spawner avec le mob <mob>.");
			sender.sendMessage("§4• §e/" + label + " set <mob>§7 - Définir le <mob> sur le spawner que vous regardez.");
			sender.sendMessage("§4• §e/" + label + " list§7 - Liste de tous les mobs.");
			sender.sendMessage("");
			sender.sendMessage("§4-------------------------------------------------");
		} else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("give")) {
	    if (sender.hasPermission("spawner.give"))
	    {
	      EntityType mob = Main.getEntityType(args[1]);
	      if(Main.isValide(mob)) {
	    	  ItemStack spawner = new ItemStack(Material.MOB_SPAWNER, 1);
	    	  ItemMeta smeta = (ItemMeta) spawner.getItemMeta();
	    	  String cap = mob.toString().substring(0,1).toUpperCase() + mob.toString().substring(1).toLowerCase();
	    	  smeta.setDisplayName(ChatUtils.colorReplace("%yellow%" + cap + "%green% spawner"));
	    	  spawner.setItemMeta(smeta);
	    	  
	    	  p.getInventory().addItem(spawner);
	      } else {
	    	  p.sendMessage("§cCe mob n'existe pas !");
	      }
	    }
	    return true;
		} else if(args[0].equalsIgnoreCase("set")) {
		    if (sender.hasPermission("spawner.set"))
		    {
		      EntityType mob = Main.getEntityType(args[1]);
		      if(Main.isValide(mob)) {		    	  
		    	  Block b = Main.getSpawner(p);
		    	  if(b != null) {
						CreatureSpawner entitySpawner = (CreatureSpawner) b.getState();
						entitySpawner.setSpawnedType(mob);
						entitySpawner.update();
						ChatUtils.sendMessagePlayer(p, "%green%Vous avez changer le spawner en %gold%" + mob.toString().toLowerCase());
		    	  } else {
		    		  ChatUtils.sendMessagePlayer(p, "%red%Vous devez regarder un spawner pour executer cette commande !");
		    		  return true;
		    	  }
		      } else {
		    	  p.sendMessage("§cCe mob n'existe pas !");
		      }
		    }
		    return true;
			} else {
			sender.sendMessage("§cCommande invalide !");
		}
	    return true;
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("list")) {
				if (sender.hasPermission("spawner.list"))
                {
                    String result = "";
                    boolean first  = true; 
                    for(EntityType type : EntityType.values()) {
                    	if(Main.isValide(type)) {
                        if(first == false) result += "%red%, "; else first = false;
                        result += "%gold%" + type.toString().toLowerCase();
                    	}
                    }
                    ChatUtils.sendMessagePlayer(p, ChatUtils.colorReplace("%red%Voici la liste de toutes les entitées : " + result + "%red%."));
                }
			    return true;
				} else {
			ChatUtils.sendMessagePlayer(p, ChatUtils.colorReplace("%red%Merci de préciser un mob !"));
				}
		} else {
			sender.sendMessage("§cErreur: arguments invalides !");
		}
	  }
		return true;
	}

}
