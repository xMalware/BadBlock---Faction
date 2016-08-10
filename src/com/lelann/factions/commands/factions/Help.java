package com.lelann.factions.commands.factions;

import org.bukkit.command.CommandSender;

import com.lelann.factions.utils.ChatUtils;

public class Help extends AbstractCommand {
	public Help() {
		super("help", "faction.play.help", "%gold%/factions help %red%type", "%gold%D'façon on l'verra pas :3", "/f help", null);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		if(args.length == 0){
			general(sender);
		} else if(args[0].equalsIgnoreCase("autre")){
			ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»&b &b&lAutre &8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»");
			CommandsManager.getInstance().get("create").sendHelp(sender);
			CommandsManager.getInstance().get("join").sendHelp(sender);
			CommandsManager.getInstance().get("leave").sendHelp(sender);
			CommandsManager.getInstance().get("info").sendHelp(sender);
			CommandsManager.getInstance().get("power").sendHelp(sender);
			CommandsManager.getInstance().get("map").sendHelp(sender);
			ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m-----------------------------&f&8&l«&b&l-&8&l»&b");
		} else if(args[0].equalsIgnoreCase("membre")){
			ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»&b &b&lMembre &8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»");
			CommandsManager.getInstance().get("chat").sendHelp(sender);
			CommandsManager.getInstance().get("home").sendHelp(sender);
			CommandsManager.getInstance().get("leave").sendHelp(sender);
			ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m-----------------------------&f&8&l«&b&l-&8&l»&b");
		} else if(args[0].equalsIgnoreCase("modo")){
			if(args.length == 1 || !args[1].equalsIgnoreCase("2")){
				ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»&b &b&lModo - 1/2 &8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»");
				CommandsManager.getInstance().get("claim").sendHelp(sender);
				CommandsManager.getInstance().get("unclaim").sendHelp(sender);
				CommandsManager.getInstance().get("autoclaim").sendHelp(sender);
				CommandsManager.getInstance().get("autounclaim").sendHelp(sender);
				CommandsManager.getInstance().get("kick").sendHelp(sender);
				CommandsManager.getInstance().get("invite").sendHelp(sender);
				CommandsManager.getInstance().get("deinvite").sendHelp(sender);
				CommandsManager.getInstance().get("sethome").sendHelp(sender);
				ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m-----------------------------&f&8&l«&b&l-&8&l»&b");
			} else if(args[1].equalsIgnoreCase("2")){
				ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»&b &b&lModo - 2/2 &8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»");
				CommandsManager.getInstance().get("delhome").sendHelp(sender);
				CommandsManager.getInstance().get("enemy").sendHelp(sender);
				CommandsManager.getInstance().get("ally").sendHelp(sender);
				CommandsManager.getInstance().get("neutral").sendHelp(sender);
				CommandsManager.getInstance().get("tag").sendHelp(sender);
				CommandsManager.getInstance().get("description").sendHelp(sender);
				CommandsManager.getInstance().get("title").sendHelp(sender);
				ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m-----------------------------&f&8&l«&b&l-&8&l»&b");
			}
		} else if(args[0].equalsIgnoreCase("chef")){
			ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»&b &b&lChef &8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»");
			CommandsManager.getInstance().get("disband").sendHelp(sender);
			CommandsManager.getInstance().get("owner").sendHelp(sender);
			CommandsManager.getInstance().get("rank").sendHelp(sender);
			CommandsManager.getInstance().get("unclaimall").sendHelp(sender);
			ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m-----------------------------&f&8&l«&b&l-&8&l»&b");
		} else if(args[0].equalsIgnoreCase("admin") && hasPermission(sender, "faction.admin.help")){
			ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»&b &b&lAdmin &8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»");
			CommandsManager.getInstance().get("bypass").sendHelp(sender);
			CommandsManager.getInstance().get("default").sendHelp(sender);
			CommandsManager.getInstance().get("defaultwe").sendHelp(sender);
			CommandsManager.getInstance().get("default").sendHelp(sender);
			CommandsManager.getInstance().get("resetpower").sendHelp(sender);
			ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m-----------------------------&f&8&l«&b&l-&8&l»&b");
		} else {
			general(sender);
		}
	}
	public void general(CommandSender sender){
		ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»&b &b&lBadBlock - Factions &8&l«&b&l-&8&l»&m------&f&8&l«&b&l-&8&l»");
		ChatUtils.sendMessage(sender, "%red%> %gold%/f help %aqua%autre %gold%: affiche l'aide pour les sans faction");
		ChatUtils.sendMessage(sender, "%red%> %gold%/f help %aqua%membre %gold%: affiche l'aide pour les membres");
		ChatUtils.sendMessage(sender, "%red%> %gold%/f help %aqua%modo %gold%: affiche l'aide pour les modos");
		ChatUtils.sendMessage(sender, "%red%> %gold%/f help %aqua%chef %gold%: affiche l'aide pour les chefs");
		if(hasPermission(sender, "faction.admin.help"))
			ChatUtils.sendMessage(sender, "%red%> %gold%/f help %aqua%admin %gold%: affiche l'aide pour les admins");
		ChatUtils.sendMessage(sender, "&8&l«&b&l-&8&l»&m-----------------------------&f&8&l«&b&l-&8&l»&b");
	}
}