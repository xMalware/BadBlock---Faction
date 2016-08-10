package com.lelann.factions.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.devhill.socketinventory.json.bukkit.JSON;
import fr.devhill.socketinventory.json.elements.JArray;
import fr.devhill.socketinventory.json.elements.JObject;
import lombok.Getter;

public class JRawMessage {
	@Getter private JObject message;
	
	public JRawMessage(String base){
		message = serialize(base);
	}
	
	public void add(JRawMessage... messages){
		for(JRawMessage message : messages)
			this.message.getArray("extra").add(message.message);
	}
	
	public void setColor(String color){
		if(color == null || color.isEmpty()) return;
		color = ChatUtils.colorReplace(color);
		if(color.length() > 1){
			ChatColor c = ChatColor.getByChar(color.toCharArray()[1]);
			JArray extra = message.getArray("extra");
			if(extra.getValues().size() > 0){
				extra.getObjectList().get(0).set("color", c.name().toLowerCase());
				message.set("extra", extra);
			}
		}
	}
	
	public void addHoverEvent(HoverEventType type, String value, boolean parseValue){
		if(type == null || value == null){
			message.set("hoverEvent", null);
		} else {
			message.set("hoverEvent.action", type.name().toLowerCase());
			message.set("hoverEvent.value", parseValue ? serialize(value) : value);
		}
	}
	
	public void addHoverEvent(HoverEventType type, String... values){
		if(type == null || values == null){
			message.set("hoverEvent", null);
		} else {
			message.set("hoverEvent.action", type.name().toLowerCase());
//			JObject[] objects = new JObject[values.length];
//			for(int i=0;i<values.length;i++){
//				objects[i] = serialize("\n" + values[i]);
//			}
			message.set("hoverEvent.value", serialize(StringUtils.join(values, "\n")));
		}
	}
	
	public void addHoverEvent(HoverEventType type, String value){
		addHoverEvent(type, value, true);
	}
	
	public void addClickEvent(ClickEventType type, String value, boolean parseValue){
		if(type == null || value == null){
			message.set("clickEvent", null);
		} else {
			message.set("clickEvent.action", type.name().toLowerCase());
			message.set("clickEvent.value", parseValue ? serialize(value) : value);
		}
	}
	
	public void addClickEvent(ClickEventType type, String value){
		addClickEvent(type, value, true);
	}
	
	public void send(Player p){
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + p.getName() + " " + message.toString());
	}
	
	public void broadcast(){
		for(final Player p : Bukkit.getOnlinePlayers()){
			send(p);
		}
	}
	
	private JObject serialize(String string){
		string += " ";
		string = ChatUtils.colorReplace(string);
		JObject json = JSON.loadFromString("{}");
		json.set("text", "");

		JArray texts = new JArray(new String[]{});

		final List<String> colors = new ArrayList<String>();
		for (int i = 0; i < string.length() - 1; i++) {
			String region = string.substring(i, i + 2);
			if (region.matches("(" + '\247' + "([a-fk-or0-9]))")) {
				colors.add(region);
			}
		}
		final String[] split = string.split("(" + '\247' + "([a-fk-or0-9]))");
		for (int i = 0; i < colors.size(); i++) {
			JObject raw = JSON.loadFromString("{}");
			if(split.length <= i)
				raw.set("text", "");
			else raw.set("text", split[i + 1]);
			ChatColor color = ChatColor.getByChar(colors.get(i).substring(1));
			if(color == ChatColor.UNDERLINE){
				raw.set("underlinded", true);
				raw.set("color", texts.getObjectList().get(texts.getList().size() - 1).getString("color"));
			} else if(color == ChatColor.BOLD){
				raw.set("bold", true);
				raw.set("color", texts.getObjectList().get(texts.getList().size() - 1).getString("color"));
			} else if(color == ChatColor.STRIKETHROUGH){
				raw.set("strikethrough", true);
				raw.set("color", texts.getObjectList().get(texts.getList().size() - 1).getString("color"));
			} else if(color == ChatColor.MAGIC){
				raw.set("obfuscated", true);
				raw.set("color", texts.getObjectList().get(texts.getList().size() - 1).getString("color"));
			} else raw.set("color", color.name().toLowerCase());
			texts.add(raw);
		}

		json.set("extra", texts);
		return json;
	}
	
	public enum HoverEventType{
		SHOW_TEXT,
		SHOW_ACHIEVEMENT,
		SHOW_ITEM;
	}
	
	public enum ClickEventType{
		OPEN_URL,
		OPEN_FILE,
		RUN_COMMAND,
		SUGGEST_COMMAND;
	}
}
