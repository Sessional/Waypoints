package waypoints;

import com.nijiko.permissions.PermissionHandler;

import org.bukkit.entity.Player;

public class PermissionsHandler extends PermissionBase
{

    PermissionHandler permissionHandler;

    public PermissionsHandler(Waypoints waypoints, PermissionHandler permissionsHandler)
    {
        super(waypoints);
        this.permissionHandler = permissionsHandler;
    }

    public boolean handleCommand(Player committingPlayer, String[] args)
    {
        String subCommand = args[0];
        if (subCommand.equalsIgnoreCase("create") || args[0].equalsIgnoreCase("add"))
        {
            if (!permissionHandler.has(committingPlayer, "waypoints.admin.create"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return createWaypoint(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("go"))
        {
            if (!permissionHandler.has(committingPlayer, "waypoints.basic.go"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return goWaypoint(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("delete"))
        {
            if (!permissionHandler.has(committingPlayer, "waypoints.admin.delete"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return deleteWaypoint(args);
        } else if (subCommand.equalsIgnoreCase("save"))
        {
            if (!permissionHandler.has(committingPlayer, "waypoints.admin.save"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            waypoints.saveConfigurations();
            return waypoints.saveWaypoints(committingPlayer);
        } else if (subCommand.equalsIgnoreCase("list"))
        {
            if (!permissionHandler.has(committingPlayer, "waypoints.basic.list"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return listWaypoints(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("help"))
        {
            if (!permissionHandler.has(committingPlayer, "waypoints.basic.help"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return printHelp(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("return"))
        {
            if (!permissionHandler.has(committingPlayer, "waypoints.basic.return"))
            {
                committingPlayer.sendMessage("You do not have permissions to do that.");
                return true;
            }
            return returnToPoint(committingPlayer, args);
        }

        return false;
    }
}
