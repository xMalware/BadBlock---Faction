package me.archimede67.FactionUtils;

import java.io.File;
import java.io.IOException;
 
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
 
public class SettingsManager {
 
        private SettingsManager() { }
       
        static SettingsManager instance = new SettingsManager();
       
        public static SettingsManager getInstance() {
                return instance;
        }
       
        Plugin p;
       
        FileConfiguration clearlag;
        File cfile;
       
        FileConfiguration drop;
        File dfile;
        
        FileConfiguration worldlimit;
        File wlfile;
       
        FileConfiguration nametag;
        File ntfile;
        
        public void setup(Plugin p) {
               
                if (!p.getDataFolder().exists()) {
                        p.getDataFolder().mkdir();
                }
               
                dfile = new File(p.getDataFolder(), "drop.yml");
               
                if (!dfile.exists()) {
                        try {
                                dfile.createNewFile();
                        }
                        catch (IOException e) {
                                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create data.yml!");
                        }
                }
               
                drop = YamlConfiguration.loadConfiguration(dfile);
                
                cfile = new File(p.getDataFolder(), "clearlag.yml");
                
                if (!cfile.exists()) {
                        try {
                        	cfile.createNewFile();
                        }
                        catch (IOException e) {
                                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create data.yml!");
                        }
                }
               
                clearlag = YamlConfiguration.loadConfiguration(cfile);
                
               
                
                ntfile = new File(p.getDataFolder(), "nametag.yml");
                
                if (!ntfile.exists()) {
                        try {
                        	ntfile.createNewFile();
                        }
                        catch (IOException e) {
                                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create data.yml!");
                        }
                }
               
                nametag = YamlConfiguration.loadConfiguration(ntfile);
                
                Main.loadConfig();
                
        }
       
        public FileConfiguration getDr() {
                return drop;
        }
       
        public void saveDr() {
                try {
                	drop.save(dfile);
                }
                catch (IOException e) {
                        Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save data.yml!");
                }
        }
       
        public void reloadDr() {
        	drop = YamlConfiguration.loadConfiguration(dfile);
        }
       
        public FileConfiguration getCl() {
                return clearlag;
        }
       
        public void saveCl() {
                try {
                	clearlag.save(cfile);
                }
                catch (IOException e) {
                        Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save config.yml!");
                }
        }
       
        public void reloadCl() {
        	clearlag = YamlConfiguration.loadConfiguration(cfile);
        }
        
        public FileConfiguration getNt() {
            return nametag;
        }
   
        public void saveNt() {
            try {
            	nametag.save(ntfile);
            }
            catch (IOException e) {
                    Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save config.yml!");
            }
        }
   
        public void reloadNt() {
    	nametag = YamlConfiguration.loadConfiguration(ntfile);
        }
       
        public PluginDescriptionFile getDesc() {
                return p.getDescription();
        }
}