package com.lelann.factions.utils;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;

@Data @AllArgsConstructor
public class Title {
	private String title, subTitle;
	private int fadeIn, stay, fadeOut;
	
	public void send(Player player){
		if(player == null) return;
		CraftPlayer craftplayer = (CraftPlayer)player;
		PlayerConnection connection = craftplayer.getHandle().playerConnection;
		
		IChatBaseComponent titleJSON = ChatSerializer.a("{\"text\": \"" + ChatUtils.colorReplace(title) + "\"}");
		IChatBaseComponent subtitleJSON = ChatSerializer.a("{\"text\": \"" + ChatUtils.colorReplace(subTitle) + "\"}");	
		PacketPlayOutTitle length = new PacketPlayOutTitle(EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
		PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
		PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
		
		connection.sendPacket(titlePacket);
		connection.sendPacket(length);
		connection.sendPacket(subtitlePacket);
	}
	
	public void send(Player... players){
		for(Player player : players)
			send(player);
	}
	
	public void broadcast(){
		for(Player player : Bukkit.getOnlinePlayers()){
			send(player);
		}
	}
	
	public void clear(Player player){
		CraftPlayer craftplayer = (CraftPlayer)player;
		PlayerConnection connection = craftplayer.getHandle().playerConnection;

		PacketPlayOutTitle clearPacket = new PacketPlayOutTitle(EnumTitleAction.CLEAR, null, 0, 0, 0);
		connection.sendPacket(clearPacket);
	}
	
	public void clearAll(){
		for(Player player : Bukkit.getOnlinePlayers()){
			clear(player);
		}
	}

	public Title(String title, String subTitle) {
		this(title, subTitle, 5, 20, 5);
	}
}
