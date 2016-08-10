package com.lelann.factions.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.earth2me.essentials.Essentials;
import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.FactionObject;
import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.FactionPlayer.FactionChat;
import com.lelann.factions.api.FactionRelationship.FactionRelationshipType;
import com.lelann.factions.utils.ChatUtils;
import com.lelann.factions.utils.JRawMessage;
import com.lelann.factions.utils.JRawMessage.ClickEventType;
import com.lelann.factions.utils.JRawMessage.HoverEventType;
import com.lelann.factions.utils.MathsUtils;

public class ChatListener extends FactionObject implements Listener{
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onSpeak(AsyncPlayerChatEvent e){
		if(e.isCancelled()) return;
		try {
//			changePlayerTabListName(e.getPlayer());
			
			FactionPlayer player = getPlayersManager().getPlayer(e.getPlayer());
			Faction f = player.getFaction();

			if(f.isDefault() || player.getChat() == FactionChat.PUBLIC){
				String faction = "";

				if(!f.isDefault()){
					faction += "&a";
					faction += player.getFactionRank().getRankName();
					faction += "-" + player.getFaction().getName();
					faction += " ";
				}
				
				String permission = player.getDisplayGroupCache();

				Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
				permission += ess.getUser(e.getPlayer()).getNick(true);
				
				e.getPlayer().setDisplayName(ChatUtils.colorReplace(permission));
				
				faction += permission;
				
				String message = "&r%gray% : %white%";
				if(!e.getPlayer().hasPermission("faction.admin.chatcolor"))
					message += ChatUtils.colorDelete(e.getMessage());
				else message += e.getMessage();
				
				JRawMessage factionRaw = new JRawMessage(faction);
				factionRaw.addClickEvent(ClickEventType.RUN_COMMAND, "/factions info " + f.getName(), false);
				
				List<String> lore = new ArrayList<String>();
				if(!f.isDefault()){
					lore.add("&6Faction : &c" + f.getName());
				}
				lore.add("&6Money : &c" + player.getMoney() + "$");
				lore.add("&6Power : &c" + MathsUtils.round(player.getPower(), 2) + "&4/&c" + FactionConfiguration.getInstance().getMaxPower());

				factionRaw.addHoverEvent(HoverEventType.SHOW_TEXT, lore.toArray(new String[0]));
				JRawMessage messageRaw = new JRawMessage(message);
				
				for(final Player p : getServer().getOnlinePlayers()){
					final FactionPlayer fp = getPlayersManager().getPlayer(p);
					JRawMessage general = new JRawMessage("");
					if(!f.isDefault()){
						factionRaw.setColor(MoveListener.color(fp.getFaction(), f));
					}
					general.add(factionRaw);
					
					general.add(messageRaw); 
					general.send(p);
				}
			} else {
				Faction faction = player.getFaction();
				String message = player.getDisplayName() + " : ";
				if(!e.getPlayer().hasPermission("faction.admin.chatcolor"))
					message += ChatUtils.colorDelete(e.getMessage());
				else message += e.getMessage();

				if(player.getChat() == FactionChat.ALLY){
					message = FactionRelationshipType.ALLY.getColor() + message;
					List<Faction> allies = getFactionsManager().getAll(faction, FactionRelationshipType.ALLY);

					for(Faction fac : allies)
						fac.sendMessage(message);
					faction.sendMessage(message);
				} else if(player.getChat() == FactionChat.FACTION){
					message = FactionRelationshipType.SAME.getColor() + message;
					faction.sendMessage(message);
				}
			}
			e.setCancelled(true);
		} catch(Exception exception){
			exception.printStackTrace();
		}
	}
	
//	@EventHandler
//	public void onGroupChange(PermissionEntityEvent e){
//		if(e.getAction() == Action.INHERITANCE_CHANGED && e.getEntity() instanceof PermissionUser){
//			PermissionUser user = (PermissionUser) e.getEntity();
//			new BukkitRunnable(){
//				@Override
//				public void run(){
//					if(user.getPlayer() == null) return;
//					FactionPlayer fp = getPlayersManager().getPlayer(user.getPlayer().getUniqueId());
//					fp.reloadGroupCache(fp.getPlayer());
//
//					for(final Player p : Bukkit.getOnlinePlayers()){
//						FactionPlayer player = getPlayersManager().getPlayer(p.getUniqueId());
//						if(player == null) continue;
//						if(player.getScoreboard() != null)
//							player.getScoreboard().updatePlayerPrefix(fp);
//					}
//				}
//			}.runTaskLater(Main.getInstance(), 5L);
//		}
//	}
//	
//	public void changePlayerTabListName(Player concerned){
////		if(p.getName().length() <= 14){
////			String lastColor = getLastColor(getPermissions().getPrefix(p));
////			p.setPlayerListName(ChatUtils.colorReplace(lastColor) + p.getName());
////		}
//		FactionPlayer fp = getPlayersManager().getPlayer(concerned);
//		fp.reloadGroupCache(concerned);
//		
//		for(final Player p : getServer().getOnlinePlayers()){
//			FactionPlayer player = getPlayersManager().getPlayer(p);
//			if(player == null) continue;
//			if(player.getScoreboard() != null)
//				player.getScoreboard().updatePlayerPrefix(fp);
//		}
//	}
//	
//	public String getLastColor(String str){
//		String result = null;
//		for(int i=0;i<str.length();i++){
//			char c = str.charAt(i);
//			if(c == '&'){
//				ChatColor cc = ChatColor.getByChar(str.charAt(i + 1));
//				if(cc != null){
//					result = "&" + str.charAt(i + 1);
//				}
//			}
//		}
//		
//		return result;
//	}
}
