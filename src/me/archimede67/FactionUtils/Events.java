package me.archimede67.FactionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Events implements Listener {
	
	public SettingsManager config = SettingsManager.getInstance();
	static List<String> hasReachedLimits = new ArrayList<String>();
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
		SkullMeta sm = (SkullMeta) head.getItemMeta();
		sm.setOwner(e.getEntity().getPlayer().getName());
		sm.setDisplayName("§a" + e.getEntity().getPlayer().getName());
		head.setItemMeta(sm);
		
		double chanceDouble = config.getDr().getDouble("Drop.DropChancePlayer"); 
				
		int chance = (int) chanceDouble;
		if(chance > 100) {
			if(e.getEntity().getPlayer().isOp()) {
				e.getEntity().sendMessage("§cUne erreur s'est produite: le taux de chance est supérieur à 100 !");
			}
		} else if(chance == 0) {
			
		} else if(chance > 0 && chance <= 100) {
			Random random = new Random();
			int c = random.nextInt(100) + 1;
			if(c <= chance) {
				e.getDrops().add(head);
			} else {
				
			}
		}
	}
	
	@EventHandler
	public void onDeathMobs(EntityDeathEvent e) {
		EntityType entity = e.getEntityType();
		if(e.getEntity() instanceof Creature) {
		if(entity == EntityType.ZOMBIE) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)2);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setDisplayName("§aZombie");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.CREEPER) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)4);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setDisplayName("§aCreeper");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.SKELETON) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setDisplayName("§aSkelette");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.BLAZE) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Blaze");
			sm.setDisplayName("§aBlaze");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.CAVE_SPIDER) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_CaveSpider");
			sm.setDisplayName("§aCave Spider");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.CHICKEN) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Chicken");
			sm.setDisplayName("§aChicken");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.COW) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Cow");
			sm.setDisplayName("§aCow");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.ENDERMAN) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Enderman");
			sm.setDisplayName("§aEnderman");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.GHAST) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Ghast");
			sm.setDisplayName("§aGhast");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.IRON_GOLEM) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Golem");
			sm.setDisplayName("§aGolem");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.SLIME) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Slime");
			sm.setDisplayName("§aSlime");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.MUSHROOM_COW) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_MushroomCow");
			sm.setDisplayName("§aMushroomCow");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.OCELOT) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Ocelot");
			sm.setDisplayName("§aOcelot");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.PIG) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Pig");
			sm.setDisplayName("§aPig");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.PIG_ZOMBIE) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_PigZombie");
			sm.setDisplayName("§aPig Zombie");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.SHEEP) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Sheep");
			sm.setDisplayName("§aSheep");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.SPIDER) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Spider");
			sm.setDisplayName("§aSpider");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.SQUID) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Squid");
			sm.setDisplayName("§aSquid");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.VILLAGER) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Villager");
			sm.setDisplayName("§aVillager");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		} else if(entity == EntityType.WITHER) {
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm = (SkullMeta) head.getItemMeta();
			sm.setOwner("MHF_Wither");
			sm.setDisplayName("§aWither");
			head.setItemMeta(sm);
			
			double chanceDouble = config.getDr().getDouble("Drop.DropChanceMobs"); 
			int chance = (int) chanceDouble;
			if(chance > 100) {

			} else if(chance == 0) {
				
			} else if(chance > 0 && chance <= 100) {
				Random random = new Random();
				int c = random.nextInt(100) + 1;
				if(c <= chance) {
					e.getDrops().add(head);
				} else {
					
				}
			}
		}
	  }
	}

	public void onPlace(BlockPlaceEvent e) {
		try {
		if(hasReachedLimits.contains(e.getPlayer().getName())) {
			e.setCancelled(true);
		}
		} catch (NullPointerException ex) {
			
		}
		if(e.getPlayer().getItemInHand() == null) {
			return;
		} else {
		ItemStack item = e.getPlayer().getItemInHand();
		if(item.getItemMeta() == null) {
			return;
		} else {
			if(item.getType().equals(Material.MOB_SPAWNER)) {
		String spawner = item.getItemMeta().getDisplayName();
		String leMob = spawner.split(" ")[0];
		EntityType mob = Main.getEntityType(ChatUtils.colorDelete(leMob));
		if(mob != null) {
			Block b = e.getBlock();
			if(b.getState() instanceof CreatureSpawner) {
				CreatureSpawner entitySpawner = (CreatureSpawner) b.getState();
				entitySpawner.setSpawnedType(mob);
			}
		}
		} else {
			return;
		}
		return;
		}
	}
			
  }

	public void onBreack(BlockBreakEvent e) {
		try {
		if(hasReachedLimits.contains(e.getPlayer().getName())) {
			e.setCancelled(true);
		}
		} catch (NullPointerException ex) {
			
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', "&7[&c+&7] &e" + e.getPlayer().getName()));
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		e.setQuitMessage("");
	}
	
}
