package com.lelann.factions.scoreboard;

import java.lang.reflect.Method;

import static com.lelann.factions.scoreboard.ReflectionUtils.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketUtils {
	public static void sendPacket(final Player p, final Object packet){
		try {
			Object connection = getFieldValue(getHandle(p), "playerConnection");
			Method method = getMethod(connection.getClass(), "sendPacket", getNMSClass("Packet"));

			method.invoke(connection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void broadcastPacket(final Object packet){
		for(final Player p : Bukkit.getOnlinePlayers()){
			sendPacket(p, packet);
		}
	}
}
