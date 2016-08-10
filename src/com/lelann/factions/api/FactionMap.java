package com.lelann.factions.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import com.lelann.factions.FactionObject;
import com.lelann.factions.api.managers.ChunksManager;
import com.lelann.factions.listeners.MoveListener;
import com.lelann.factions.utils.ChatUtils;
import com.lelann.factions.utils.StringUtils;

public class FactionMap extends FactionObject {
	private int[][] map = new int[5][41];
	private char[] carac = new char[]{'\\', '/', '%', '#', '$', '<', '>', '!', '*', '^', '?', '€', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	private Map<Integer, Character> factions;
	private Map<Integer, String> factionsName;
	private Map<Integer, String> factionsColor;

	private boolean activated = false;

	public FactionMap(Chunk c, Faction factionFor){
		ChunksManager manager = getChunksManager(c.getWorld());
		if(manager == null) return;

		factions = new HashMap<Integer, Character>();
		factionsName = new HashMap<Integer, String>();
		factionsColor = new HashMap<Integer, String>();
		
		int currentChar = 0;

		factions.put(Faction.WILDERNESS.getFactionId(), '-'); factionsName.put(Faction.WILDERNESS.getFactionId(), Faction.WILDERNESS.getName());
		factionsColor.put(Faction.WILDERNESS.getFactionId(), MoveListener.color(factionFor, Faction.WILDERNESS));

		for(int x=0;x<41;x++){
			for(int z=0;z<5;z++){
				Chunk chunk = c.getWorld().getChunkAt(c.getX() - (x - 20), c.getZ() - (z - 2));
				Faction factionAt = manager.getFactionAt(chunk);

				map[z][x] = factionAt.getFactionId();

				if(!factionAt.isWilderness() && !factions.containsKey(factionAt.getFactionId())){
					factions.put(factionAt.getFactionId(), carac[currentChar]);
					factionsName.put(factionAt.getFactionId(), factionAt.getName());
					factionsColor.put(factionAt.getFactionId(), MoveListener.color(factionFor, factionAt));

					currentChar++;
					if(currentChar == carac.length) currentChar = 0;
				}
			}
		}
		activated = true;
	}
	public void send(Player player){
		if(!activated) {
			ChatUtils.sendMessage(player, "%red%Map indisponible dans ce monde !");
			return;
		}
		String[] result = new String[map.length];

		for(int z=0;z<map.length;z++){
			result[z] = "";
			for(int x=0;x<map[0].length;x++){
				if(x == 20 && z == 2){
					result[z] += "%dred%0";
				} else result[z] += factionsColor.get(map[z][x]) + factions.get(map[z][x]);
			}
		}

		List<String> factionsStrings = new ArrayList<String>();
		for(int id : factions.keySet()){
			factionsStrings.add(
					factionsColor.get(id) + factions.get(id) + " = " + factionsName.get(id)
					);
		}
		
		ChatUtils.sendMessage(player, "&8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»&b &b&lInformations &8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»");

		ChatUtils.sendMessage(player, result);
		ChatUtils.sendMessage(player, StringUtils.join(factionsStrings, "%default%, "));
	
		ChatUtils.sendMessage(player, "&8&l«&b&l-&8&l»&m-----------------------------&f&8&l«&b&l-&8&l»&b");
	}
}
