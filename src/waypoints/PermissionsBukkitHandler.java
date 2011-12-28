package waypoints;

import org.bukkit.entity.Player;

public class PermissionsBukkitHandler extends PermissionBase {

    public PermissionsBukkitHandler(Waypoints waypoints)
    {
        super(waypoints);
    }

    public boolean handleCommand(Player committingPlayer, String[] args)
    {
        String subCommand = args[0];
        if (subCommand.equalsIgnoreCase("create") || args[0].equalsIgnoreCase("add"))
        {
            if (!committingPlayer.hasPermission("waypoints.admin.create"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return createWaypoint(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("go"))
        {
            if (!committingPlayer.hasPermission("waypoints.basic.go"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return goWaypoint(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("delete"))
        {
            if (!committingPlayer.hasPermission("waypoints.admin.delete"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return deleteWaypoint(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("save"))
        {
            if (!committingPlayer.hasPermission("waypoints.admin.save"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            waypoints.saveConfigurations();
            return waypoints.saveWaypoints(committingPlayer);
        } else if (subCommand.equalsIgnoreCase("list"))
        {
            if (!committingPlayer.hasPermission("waypoints.basic.list"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return listWaypoints(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("help"))
        {
            if (!committingPlayer.hasPermission("waypoints.basic.help"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return printHelp(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("return"))
        {
            if (!committingPlayer.hasPermission("waypoints.basic.return"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return returnToPoint(committingPlayer, args);
        }
        return false;
    }
}
