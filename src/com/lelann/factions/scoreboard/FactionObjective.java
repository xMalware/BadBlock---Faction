package com.lelann.factions.scoreboard;

import org.bukkit.scoreboard.Scoreboard;

import com.lelann.factions.FactionConfiguration;
import com.lelann.factions.api.Faction;
import com.lelann.factions.api.FactionPlayer;
import com.lelann.factions.utils.MathsUtils;

public class FactionObjective extends FormattedObjective {
	private FactionPlayer player;
	
	public FactionObjective(FactionPlayer player, Scoreboard handler) {
		super(handler, "faction", "&b&lFactions");
		this.player = player;
	}

	@Override
	public void generateInformations() {
		if(player == null) return;
		
		Faction f = player.getFaction();
		
		if(f == null){
			player.leaveFaction();
			f = Faction.WILDERNESS;
		}
		
		if(!f.isDefault()){
		      addInformation("&8&m---------------------- ");
		      addInformation("&7Faction : &b"+ f.getName());
		      addInformation("");
		      addInformation("&7Titre : &b" + this.player.getFactionRank().getRankName());
		      addInformation("");
		      addInformation("&7Nombre de Membre : &b" + f.getPlayers().size());
		      addInformation("");
		      addInformation("&7Claims : &b" + f.getChunkNumber() + " (" + f.getApChunkNumber() + " AP)");
		} else {
		      addInformation("&8&m---------------------- ");
		      addInformation("&7Faction : &cAucune");
		}
		
		addInformation("");
	    addInformation("&7Power : &b" + MathsUtils.round(this.player.getPower(), 2) + "&7/&b" + FactionConfiguration.getInstance().getMaxPower());
	    addInformation("");
	    addInformation("&7Monnaie : &b" + this.player.getMoney() + "&7$");
	    addInformation("&8&m----------------------");
	    addInformation("&7Site : &b&lBadBlock.fr");	
	}
}
