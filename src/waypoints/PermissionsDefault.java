package waypoints;

import org.bukkit.entity.Player;

public class PermissionsDefault extends PermissionBase
{

    public PermissionsDefault(Waypoints waypoints)
    {
        super(waypoints);
    }

    public boolean handleCommand(Player committingPlayer, String[] args)
    {
        String subCommand = args[0];
        if (subCommand.equalsIgnoreCase("create") || args[0].equalsIgnoreCase("add"))
        {
            return createWaypoint(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("go"))
        {
            return goWaypoint(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("delete"))
        {
            return deleteWaypoint(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("save"))
        {
            waypoints.saveConfigurations();
            return waypoints.saveWaypoints(committingPlayer);
        } else if (subCommand.equalsIgnoreCase("list"))
        {
            return listWaypoints(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("help"))
        {
            return printHelp(committingPlayer, args);
        } else if (subCommand.equalsIgnoreCase("return"))
        {
            return returnToPoint(committingPlayer, args);
        }
        return false;
    }
}
