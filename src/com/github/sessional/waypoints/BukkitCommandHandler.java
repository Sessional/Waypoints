package com.github.sessional.waypoints;

import org.bukkit.entity.Player;

public class BukkitCommandHandler extends CommandHandler
{
    /**
     * 
     * @param plugin 
     */
    public BukkitCommandHandler(WpsPlugin plugin)
    {
        super(plugin);
    }

    /**
     * 
     * @param player
     * @param command
     * @param args
     * @return 
     */
    @Override
    public boolean handleCommand(Player player, String command, String[] args)
    {
        if (command.equalsIgnoreCase("create") || command.equalsIgnoreCase("add"))
        {
            if (!player.hasPermission("waypoints.admin.create"))
            {
                player.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return doCreate(player, args);
        } else if (command.equalsIgnoreCase("go"))
        {
            if (!player.hasPermission("waypoints.basic.go"))
            {
                player.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return doGo(player, args);
        } else if (command.equalsIgnoreCase("delete") || command.equalsIgnoreCase("remove"))
        {
            if (!player.hasPermission("waypoints.admin.delete"))
            {
                player.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return doDelete(player, args);
        } else if (command.equalsIgnoreCase("list"))
        {
            if (!player.hasPermission("waypoints.basic.list"))
            {
                player.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return doList(player, args);
        } else if (command.equalsIgnoreCase("return"))
        {
            if (!player.hasPermission("waypoints.basic.return"))
            {
                player.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return doReturn(player, args);
        } else if (command.equalsIgnoreCase("save"))
        {
            if (!player.hasPermission("waypoints.admin.save"))
            {
                player.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return doSave(player, args);
        } else if (command.equalsIgnoreCase("load"))
        {
            if (!player.hasPermission("waypoints.admin.save"))
            {
                player.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return doLoad(player, args);
        } else if (command.equalsIgnoreCase("version"))
        {
            return showVersion(player, args);
        }
        return false;
    }
}
