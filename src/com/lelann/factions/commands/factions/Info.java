package com.lelann.factions.commands.factions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.api.FactionRelationship.FactionRelationshipType;
import com.lelann.factions.listeners.MoveListener;
import com.lelann.factions.utils.ChatUtils;
import com.lelann.factions.utils.MathsUtils;
import com.lelann.factions.utils.StringUtils;

public class Info extends AbstractCommand {

	public Info() {
		super("info", "faction.play.info", "%gold%/factions info %red%(<nom>)", "%gold%Affiche les informations de la faction %red%<nom> %gold%ou de la votre", "/f info <nom>", null);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			sendMessage(sender, NO_CONSOLE);
			return;
		}
		
		FactionPlayer player = getPlayersManager().getPlayer(sender);
		Faction faction = player.getFaction();
		
		if(faction.isDefault() && args.length < 1){
			sendMessage(sender, "%red%Vous n'avez pas de faction, impossible de voir les informations.");
		} else {
			if(args.length >= 1)
				faction = getFactionsManager().matchFaction(args[0]);
			if(faction == null || faction.isDefault()){
				sendMessage(sender, "%red%La faction spécifiée est introuvable !");
			} else {
				ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»&b &b&lInformations &8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»");
				ChatUtils.sendMessage(sender, MoveListener.color(player.getFaction(), faction) + faction.getName() + " ~ " + faction.getDescription());
				ChatUtils.sendMessage(sender, "%gold%Power : &a" + MathsUtils.round(faction.getPower(), 2) + "/" + faction.getMaxPower() + " %gold%pour %red%" + faction.getChunkNumber() + " claims");
				
				List<String> online = new ArrayList<String>();
				List<String> offline = new ArrayList<String>();
				
				for(UUID uniqueId : faction.getPlayers().keySet()){
					FactionPlayer fPlayer = getPlayersManager().getPlayer(uniqueId);
					if(fPlayer.getPlayer() != null && fPlayer.getPlayer().isOnline()){
						online.add(fPlayer.getDisplayName());
					} else offline.add(fPlayer.getDisplayName());
				}
				
				ChatUtils.sendMessage(sender, "%green%Joueur connectés (" + online.size() + ") : %gold%" + StringUtils.join(online, ", "));
				ChatUtils.sendMessage(sender, "%red%Joueur déconnectés (" + offline.size() + ") : %gold%" + StringUtils.join(offline, ", "));
				
				List<Faction> allies = getFactionsManager().getAll(faction, FactionRelationshipType.ALLY);
				List<String> alliesString = new ArrayList<String>();
				
				for(Faction f : allies){
					if(f != null)
						alliesString.add(f.getName());
				}
				
				List<Faction> enemies = getFactionsManager().getAll(faction, FactionRelationshipType.ENEMY);
				List<String> enemiesString = new ArrayList<String>();
				
				for(Faction f : enemies){
					if(f != null)
						enemiesString.add(f.getName());
				}
				
				ChatUtils.sendMessage(sender, FactionRelationshipType.ALLY.getColor() + "Alliés (" + allies.size() + ") : %gold%" + StringUtils.join(alliesString, ", "));
				ChatUtils.sendMessage(sender, FactionRelationshipType.ENEMY.getColor() + "Enemis (" + enemies.size() + ") : %gold%" + StringUtils.join(enemiesString, ", "));

				ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m-----------------------------&f&8&l«&b&l-&8&l»&b");
			}
		}
	}
}