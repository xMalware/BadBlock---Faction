package com.lelann.factions.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.lelann.factions.Main;

public class AntiDupliUtils {
	public static ItemStack[] replace(ItemStack[] items, Material type, ItemStack item){
		for(int i=0;i<items.length;i++){
			if(items[i] != null && items[i].getType() == type)
				items[i] = item.clone();
		}

		return items;
	}
	
	public static void cleanInventory(Inventory inv, String who, String where, int mul){
		ItemStack[] contents = inv.getContents();

		int count = 0;
		
		if((count = count(inv.getContents(), Material.MONSTER_EGG)) > 12 * mul){
			ItemStack item = new ItemStack(Material.EGG, 1);

			contents = replace(contents, Material.MONSTER_EGG, item);
			logDupli(who, where, "Oeufs x" + count);
		}
		
		if((count = count(inv.getContents(), Material.MOB_SPAWNER)) > 4 * mul){
			ItemStack item = new ItemStack(Material.ROTTEN_FLESH, 1);

			contents = replace(contents, Material.MOB_SPAWNER, item);
			logDupli(who, where, "Spawners x" + count);
		}
		
		if((count = count(inv.getContents(), Material.GOLDEN_APPLE)) > 64 * mul){
			ItemStack item = new ItemStack(Material.APPLE, 1);

			contents = replace(contents, Material.GOLDEN_APPLE, item);
			logDupli(who, where, "Pommes Cheats x" + count);
		}
		
		if((count = countAnormalItem(inv.getContents())) > 0){
			contents = removeAnormalItem(contents);
			logDupli(who, where, "Items 'vides' x" + count);
		}
		
		try {
			inv.setContents(contents);
		} catch(Exception e){}
	}
	
	public static ItemStack[] removeAnormalItem(ItemStack[] items){
		for(int i=0;i<items.length;i++){
			if(items[i] != null && items[i].getType() != Material.AIR && items[i].getAmount() <= 0)
				items[i] = new ItemStack(Material.APPLE, 1);
		}
		
		return items;
	}
	
	public static int countAnormalItem(ItemStack[] items){
		int count = 0;
		
		for(ItemStack item : items){
			if(item != null && item.getType() != Material.AIR && item.getAmount() <= 0)
				count++;
		}
		
		return count;
	}
	
	public static int count(ItemStack[] items, Material type){
		int count = 0;

		for(ItemStack item : items){
			if(item != null && item.getType() == type)
				count += item.getAmount();
		}

		return count;
	}
	
	public static String cleanBlockInventory(String who, Inventory inventory){
		String where = null;
		
		if(inventory.getHolder() instanceof BlockState){
			BlockState block = ((BlockState) inventory.getHolder());
			
			InventoryHolder bs = inventory.getHolder();
			cleanInventory(bs.getInventory(), who, buildWhere(block.getBlock()), 1);
		
			where = buildWhere(block.getBlock());
			
			for(BlockFace bf : BlockFace.values()){
				Block b = block.getBlock().getRelative(bf);
				
				if(b.getState() instanceof InventoryHolder){
					Inventory inv = ((InventoryHolder) b.getState()).getInventory();
					cleanInventory(inv, who, buildWhere(b), inv.getType() == InventoryType.ENDER_CHEST ? 2 : 1);
				}
			}
		}
		
		return where;
	}
	
	public static String buildWhere(Block block){
		return "Coffre X=" + block.getX() + " - Y=" + block.getY() + " - Z=" + block.getZ();
	}

	public static void logDupli(String who, String where, String what){
		Main.getInstance().getDB().updateAsynchrounously("INSERT INTO `dupli`(`who`, `where`, `what`, `date`) VALUES ('" + who + "','" + where + "','" + what + "',NOW())");
	}
}
