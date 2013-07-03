/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package waypoints;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.bukkit.entity.Player;

/**
 *
 * @author AKRUSE
 */
public class CommandHandler {

    WaypointPlugin plugin;
    private boolean permissionsEnabled;

    public CommandHandler(WaypointPlugin plugin) {
        this.plugin = plugin;
        permissionsEnabled = plugin.getConfigManager().permissionsEnabled();
    }

    /**
     * Teleport a player to a Waypoint. Permissions are not handled
     * in this function!
     * @param p
     * @param waypointName 
     */
    public void doGo(Player p, String waypointName)
    {
        if (plugin.doesWaypointExist(waypointName))
        {
            p.sendMessage("Welcome to " + waypointName + "!");
            p.teleport(plugin.getWaypoint(waypointName).getLocation());
        }
        else
        {
            p.sendMessage("That waypoint doesn't seem to exist!");
        }
    }
    
    /**
     * List Waypoints by world name. Permissions are not handled
     * in this function!
     * @param p
     * @param worldName 
     */
    public void doListWorld(Player p, String worldName)
    {
        Collection<Waypoint> c = plugin.getSortedWaypoints();
        for (Waypoint w : c)
        {
            if (w.getWorldName().equalsIgnoreCase(worldName))
            {
                p.sendMessage(w.getName());
            }
        }
    }
    
    /**
     * List Waypoints organized in number form. Permissions are not handled
     * in this function!
     * @param p
     * @param pageNumber 
     */
    public void doListPage(Player p, int pageNumber)
    {
        
    }
    
    /**
     * Create a Waypoint at the current location of the player. Permissions are not handled
     * in this function!
     * @param p
     * @param waypointName 
     */
    public void doCreateLocal(Player p, String waypointName)
    {
        if (!plugin.doesWaypointExist(waypointName))
        {
            plugin.getWaypointStorage().put(waypointName, new Waypoint(plugin, waypointName, p.getLocation()));
            p.sendMessage("Created waypoint " + waypointName);
        }
        else
        {
            p.sendMessage("" + waypointName + " seems to already exist!");
        }
    }
    
    /**
     * Create a Waypoint at a remote destination using the given coordinates. Permissions are not handled
     * in this function!
     * @param p
     * @param waypointName
     * @param worldName
     * @param x
     * @param y
     * @param z 
     */
    public void doCreateRemote(Player p, String waypointName, String worldName, float x, float y, float z)
    {
        if (!plugin.doesWaypointExist(waypointName))
        {
            plugin.getWaypointStorage().put(waypointName, new Waypoint(plugin, waypointName, worldName, x, y, z));
            p.sendMessage("Created waypoint " + waypointName);
        }
        else
        {
            p.sendMessage("" + waypointName + " seems to already exist!");
        }
    }
    
    /**
     * Deletes the specified Waypoint from the sever. Permissions are not handled
     * in this function!
     * @param p
     * @param waypointName 
     */
    public void doDelete(Player p, String waypointName)
    {
        if (plugin.doesWaypointExist(waypointName))
        {
            plugin.getWaypointStorage().remove(waypointName);
            p.sendMessage("Deleted " + waypointName + "!");
        }
        else
        {
            p.sendMessage("That waypoint doesn't seem to exist!");
        }
    }
    
    /**
     * Returns the player to the previous location before the go command was
     * executed.  Permissions are not handled in this function!
     * @param p 
     */
    public void doReturn(Player p)
    {
        if (plugin.getReturnPoints().containsKey(p.getName()))
        {
            p.teleport(plugin.getReturnPoints().get(p.getName()));
            plugin.getReturnPoints().remove(p.getName());
        }
        else
        {
            p.sendMessage("You have no return point to return to!");
        }
    }
    
    /**
     * Handles the command that a player has used
     * Permissions are handled by this method and not inside the execute command
     * methods.
     * 
     * @param p the player the command was issued by
     * @param wpsCommand the waypoint command used, eg. add or delete
     * @param additionalArgs the remaining arguments of the command issued
     * @return
     */
    public boolean handlePlayerCommand(Player p, String wpsCommand,
            String[] additionalArgs) {
        
        if (wpsCommand.equalsIgnoreCase("add") || wpsCommand.equalsIgnoreCase("create")) {
            //p.sendMessage("/wps <add|create> <name> [world] [x] [y] [z]");

            if (permissionsEnabled && !p.hasPermission("waypoints.add") && !p.hasPermission("waypoints.create")) {
                p.sendMessage("You do not have the required permissions to run that command.");
                p.sendMessage("Required permissions node is any of the following:"
                        + "\n\twaypoints.add"
                        + "\n\twaypoints.create");
                return true;
            }

            if (additionalArgs.length == 5) {
                String name = additionalArgs[0];
                String world = additionalArgs[1];
                float xCoord = Float.parseFloat(additionalArgs[2]);
                float yCoord = Float.parseFloat(additionalArgs[3]);
                float zCoord = Float.parseFloat(additionalArgs[4]);
                doCreateRemote(p, name, world, xCoord, yCoord, zCoord);
                return true;
            } else if (additionalArgs.length == 1) {
                String name = additionalArgs[0];
                doCreateLocal(p, name);
                return true;
            } else {
                handlePlayerHelp(p, "add");
                return true;
            }
        }
        if (wpsCommand.equalsIgnoreCase("delete") || wpsCommand.equalsIgnoreCase("remove")) {
            //p.sendMessage("/wps <delete|remove> <name>");
            if (permissionsEnabled && !p.hasPermission("waypoints.delete") && !p.hasPermission("waypoints.remove")) {
                p.sendMessage("You do not have the required permissions to run that command.");
                p.sendMessage("Required permissions node is any of the following:"
                        + "\n\twaypoints.delete"
                        + "\n\twaypoints.remove");
                return true;
            }

            if (additionalArgs.length == 1) {
                String name = additionalArgs[0];
                doDelete(p, name);
                return true;
            } else {
                handlePlayerHelp(p, "remove");
                return true;
            }
        }
        if (wpsCommand.equalsIgnoreCase("list")) {
            //p.sendMessage("/wps list [world <world name> |page <#>]");
            if (permissionsEnabled && !p.hasPermission("waypoints.list")) {
                p.sendMessage("You do not have the required permissions to run that command.");
                p.sendMessage("Required permissions node is any of the following:"
                        + "\n\twaypoints.list");
                return true;
            }
            if (additionalArgs.length == 2) {
                if (additionalArgs[0].equalsIgnoreCase("world")) {
                    String worldName = additionalArgs[1];
                    this.doListWorld(p, worldName);
                } else if (additionalArgs[0].equalsIgnoreCase("page")) {
                    int pageNumber = Integer.parseInt(additionalArgs[1]);
                    this.doListPage(p, pageNumber);
                }
                return true;
            } if (additionalArgs.length == 0)
            {
                Collection<Waypoint> c = plugin.getSortedWaypoints();
                for (Waypoint w : c)
                {
                    p.sendMessage(w.getName());
                }
                return true;
            } else {
                handlePlayerHelp(p, "list");
                return true;
            }
        }
        if (wpsCommand.equalsIgnoreCase("return")) {
            //p.sendMessage("/wps return");
            if (permissionsEnabled && !p.hasPermission("waypoints.return")) {
                p.sendMessage("You do not have the required permissions to run that command.");
                p.sendMessage("Required permissions node is any of the following:"
                        + "\n\twaypoints.return");
                return true;
            }
            if (additionalArgs.length == 0) {
                return true;
            } else {
                handlePlayerHelp(p, "return");
                return true;
            }
        }
        if (wpsCommand.equalsIgnoreCase("go")) {
            //p.sendMessage("/wps go <name>");
            if (permissionsEnabled && !p.hasPermission("waypoints.go")) {
                p.sendMessage("You do not have the required permissions to run that command.");
                p.sendMessage("Required permissions node is any of the following:"
                        + "\n\twaypoints.go");
                return true;
            }
            if (additionalArgs.length == 1) {
                String destination = additionalArgs[0];
                doGo(p, destination);
                return true;
            } else {
                handlePlayerHelp(p, "go");
                return true;
            }
        }
        return false;
    }

    /**
     * Displays basic help message
     *
     * @param p The player to display the message to
     */
    public void handlePlayerHelp(Player p) {

        p.sendMessage("Waypoints additional help:");
        p.sendMessage("type /wps help <command> for more help.");
        p.sendMessage("wps can be substituted with waypoints");
        p.sendMessage("Waypoints uses a set of subcommands as follows:");
        p.sendMessage("/wps create");
        p.sendMessage("/wps add");
        p.sendMessage("/wps delete");
        p.sendMessage("/wps remove");
        p.sendMessage("/wps list");
        p.sendMessage("/wps go");
        p.sendMessage("/wps return");
    }

    /**
     * Prints out extended help based on a subcommand of Waypoints
     *
     * @param p the player the message should go to
     * @param suboption the sub command of Waypoint that was used
     */
    public void handlePlayerHelp(Player p, String suboption) {
        if (suboption.equalsIgnoreCase("add") || suboption.equalsIgnoreCase("create")) {
            p.sendMessage("Additional help for /wps add:");
            p.sendMessage("Parameters in <> are required.\n"
                    + "Parameters in [] are optional.\n"
                    + "A parameter split with a | is an alias,"
                    + "either one can be used (but only one at a time)");
            p.sendMessage("/wps <add|create> <name> [world] [x] [y] [z]");
            p.sendMessage("Examples:\n");
            p.sendMessage("/wps add home\n\tCreates a waypoint called home"
                    + " at current location.");
            p.sendMessage("/wps create home homeWorld 0 0 0\n\tCreates a"
                    + "waypoint called home at x:0 y:0 z:0.");
        }
        if (suboption.equalsIgnoreCase("delete")
                || suboption.equalsIgnoreCase("remove")) {
            p.sendMessage("Additional help for /wps delete:");
            p.sendMessage("Parameters in <> are required.\n"
                    + "Parameters in [] are optional.\n"
                    + "A parameter split with a | is an alias,"
                    + "either one can be used (but only one at a time)");
            p.sendMessage("/wps <delete|remove> <name>");
            p.sendMessage("Examples:\n");
            p.sendMessage("/wps delete home\n\tDeletes the waypoint called home.");
            p.sendMessage("/wps remove home\n\tDeletes the waypoint called home.");
        }
        if (suboption.equalsIgnoreCase("list")) {
            p.sendMessage("Additional help for /wps list:");
            p.sendMessage("Parameters in <> are required.\n"
                    + "Parameters in [] are optional.\n"
                    + "A parameter split with a | is an alias,"
                    + "either one can be used (but only one at a time)");
            p.sendMessage("/wps list [world <world name> |page <#>]");
            p.sendMessage("Examples:\n");
            p.sendMessage("/wps list\n\tLists all waypoints that exist.");
            p.sendMessage("/wps list world homeworld\n\tLists all waypoints"
                    + "that exist on the world homeworld.");
            p.sendMessage("/wps list page 2\n\tLists waypoints in a page format"
                    + " starting on page two.");
        }
        if (suboption.equalsIgnoreCase("go")) {
            p.sendMessage("Additional help for /wps go:");
            p.sendMessage("Parameters in <> are required.\n"
                    + "Parameters in [] are optional.\n"
                    + "A parameter split with a | is an alias,"
                    + "either one can be used (but only one at a time)");
            p.sendMessage("/wps go <name>");
            p.sendMessage("Examples:\n");
            p.sendMessage("/wps go home\n\tTeleports you to the home waypoint.");
        }
        if (suboption.equalsIgnoreCase("return")) {
            p.sendMessage("Additional help for /wps go:");
            p.sendMessage("Parameters in <> are required.\n"
                    + "Parameters in [] are optional.\n"
                    + "A parameter split with a | is an alias,"
                    + "either one can be used (but only one at a time)");
            p.sendMessage("/wps return");
            p.sendMessage("Examples:\n");
            p.sendMessage("/wps return\n\tReturns you to the location you were"
                    + " at before issuing the last /wps go command.");
        }
    }
}
