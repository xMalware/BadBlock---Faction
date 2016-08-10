package com.lelann.factions.permissions;

import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PEXPermissions extends AbstractPermissions{
    private final PermissionManager manager = PermissionsEx.getPermissionManager();

    @SuppressWarnings("deprecation")
    @Override
	public String getGroup(Player base) {
        PermissionUser user = manager.getUser(base);
        if(user == null) {
            return null;
        } else {
            return user.getGroupsNames()[0];
        }
    }
    
    @Override
    public String getPrefix(Player base) {
        PermissionUser user = manager.getUser(base.getName());
        if(user == null) {
            return null;
        } else {
            return user.getPrefix(base.getWorld().getName());
        }
    }

    @Override
    public String getSuffix(Player base) {
        PermissionUser user = manager.getUser(base.getName());
        if(user == null) {
            return null;
        } else {
            return user.getSuffix(base.getWorld().getName());
        }
    }
    
    @Override
	public boolean hasPermission(Player base, String node){
    	 PermissionUser user = manager.getUser(base.getName());
         if(user == null) {
             return base.hasPermission(node);
         } else {
        	 return user.has(node);
         }
	}
}
