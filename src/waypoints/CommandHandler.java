package waypoints;

import java.util.Collection;
import org.bukkit.entity.Player;

public class CommandHandler {

    WaypointPlugin plugin;
    private boolean permissionsEnabled;

    public CommandHandler(WaypointPlugin plugin) {
        this.plugin = plugin;
        permissionsEnabled = plugin.getConfigManager().permissionsEnabled();
    }

    /**
     * Teleport a player to a Waypoint. Permissions are not handled in this
     * function!
     *
     * @param p
     * @param waypointName
     */
    public void doGo(Player p, String waypointName) {
        if (plugin.doesWaypointExist(waypointName)) {
            p.sendMessage("Welcome to " + waypointName + "!");
            plugin.getReturnPoints().put(p.getName(), p.getLocation());
            p.teleport(plugin.getWaypoint(waypointName).getLocation());
        } else {
            p.sendMessage("That waypoint doesn't seem to exist!");
        }
    }

    /**
     * List Waypoints by world name. Permissions are not handled in this
     * function!
     *
     * @param p
     * @param worldName
     */
    public void doListWorld(Player p, String worldName) {
        Collection<Waypoint> c = plugin.getSortedWaypoints();
        for (Waypoint w : c) {
            if (w.getWorldName().equalsIgnoreCase(worldName)) {
                p.sendMessage(w.getName());
            }
        }
    }

    /**
     * List Waypoints by world name. Permissions are not handled in this
     * function!
     *
     * @param worldName
     */
    public void doListWorld(String worldName) {
        Collection<Waypoint> c = plugin.getSortedWaypoints();
        for (Waypoint w : c) {
            if (w.getWorldName().equalsIgnoreCase(worldName)) {
                System.out.println(w.getName());
            }
        }
    }

    /**
     * List Waypoints organized in number form. Permissions are not handled in
     * this function!
     *
     * @param p
     * @param pageNumber
     */
    public void doListPage(Player p, int pageNumber) {
        int numPages = (int)Math.ceil((double)plugin.getSortedWaypoints().size() / (double)10);
        if (pageNumber <= 0 || numPages < pageNumber)
        {
            p.sendMessage("There is not enough Waypoints to fill that many pages.");
            return;
        }
        int startIndex = 10 * (pageNumber - 1);
        int endIndex = startIndex + 9;
        Waypoint[] wps = (Waypoint[]) plugin.getSortedWaypoints().toArray();
        p.sendMessage("Page " + pageNumber + " of " + numPages);
        for (int i = startIndex; i <= endIndex; i++)
        {
            p.sendMessage(wps[i].getName());
        }
    }

    /**
     * List Waypoints organized in number form. Permissions are not handled in
     * this function!
     *
     * @param pageNumber
     */
    public void doListPage(int pageNumber) {
        int numPages = (int)Math.ceil((double)plugin.getSortedWaypoints().size() / (double)10);
        if (pageNumber <= 0 || numPages < pageNumber)
        {
            System.out.println("There is not enough Waypoints to fill that many pages.");
            return;
        }
        int startIndex = 10 * (pageNumber - 1);
        int endIndex = startIndex + 9;
        Waypoint[] wps = (Waypoint[]) plugin.getSortedWaypoints().toArray();
        System.out.println("Page " + pageNumber + " of " + numPages);
        for (int i = startIndex; i <= endIndex; i++)
        {
            System.out.println(wps[i].getName());
        }
    }

    /**
     * Create a Waypoint at the current location of the player. Permissions are
     * not handled in this function!
     *
     * @param p
     * @param waypointName
     */
    public void doCreateLocal(Player p, String waypointName) {
        if (!plugin.doesWaypointExist(waypointName)) {
            plugin.getWaypointStorage().put(waypointName.toLowerCase(), new Waypoint(plugin, waypointName, p.getLocation()));
            p.sendMessage("Created waypoint " + waypointName);
            plugin.getFileManager().saveWpsFile();
        } else {
            p.sendMessage("" + waypointName + " seems to already exist!");
        }
    }

    /**
     * Create a Waypoint at a remote destination using the given coordinates.
     * Permissions are not handled in this function!
     *
     * @param p
     * @param waypointName
     * @param worldName
     * @param x
     * @param y
     * @param z
     */
    public void doCreateRemote(Player p, String waypointName, String worldName, float x, float y, float z) {
        if (!plugin.doesWaypointExist(waypointName)) {
            plugin.getWaypointStorage().put(waypointName.toLowerCase(), new Waypoint(plugin, waypointName, worldName, x, y, z));
            p.sendMessage("Created waypoint " + waypointName);
            plugin.getFileManager().saveWpsFile();
        } else {
            p.sendMessage("" + waypointName + " seems to already exist!");
        }
    }

    /**
     * Create a Waypoint at a remote destination using the given coordinates.
     * Permissions are not handled in this function!
     *
     * @param waypointName
     * @param worldName
     * @param x
     * @param y
     * @param z
     */
    public void doCreateRemote(String waypointName, String worldName, float x, float y, float z) {
        if (!plugin.doesWaypointExist(waypointName)) {
            plugin.getWaypointStorage().put(waypointName.toLowerCase(), new Waypoint(plugin, waypointName, worldName, x, y, z));
            System.out.println("Created waypoint " + waypointName);
            plugin.getFileManager().saveWpsFile();
        } else {
            System.out.println("" + waypointName + " seems to already exist!");
        }
    }

    /**
     * Deletes the specified Waypoint from the sever. Permissions are not
     * handled in this function!
     *
     * @param p
     * @param waypointName
     */
    public void doDelete(Player p, String waypointName) {
        if (plugin.doesWaypointExist(waypointName)) {
            plugin.getWaypointStorage().remove(waypointName.toLowerCase());
            p.sendMessage("Deleted " + waypointName + "!");
            plugin.getFileManager().saveWpsFile();
        } else {
            p.sendMessage("That waypoint doesn't seem to exist!");
        }
    }

    /**
     * Deletes the specified Waypoint from the sever.
     *
     * @param waypointName
     */
    public void doDelete(String waypointName) {
        if (plugin.doesWaypointExist(waypointName)) {
            plugin.getWaypointStorage().remove(waypointName.toLowerCase());
            System.out.println("Deleted " + waypointName + "!");
            plugin.getFileManager().saveWpsFile();
        } else {
            System.out.println("That waypoint doesn't seem to exist!");
        }
    }

    /**
     * Returns the player to the previous location before the go command was
     * executed. Permissions are not handled in this function!
     *
     * @param p
     */
    public void doReturn(Player p) {
        if (plugin.getReturnPoints().containsKey(p.getName())) {
            p.teleport(plugin.getReturnPoints().get(p.getName()));
            plugin.getReturnPoints().remove(p.getName());
        } else {
            p.sendMessage("You have no return point to return to!");
        }
    }

    /**
     * Handles the command that a player has used Permissions are handled by
     * this method and not inside the execute command methods.
     *
     * @param p the player the command was issued by
     * @param wpsCommand the waypoint command used, eg. add or delete
     * @param additionalArgs the remaining arguments of the command issued
     */
    public void handlePlayerCommand(Player p, String wpsCommand, String[] additionalArgs) {

        if (wpsCommand.equalsIgnoreCase("add") || wpsCommand.equalsIgnoreCase("create")) {
            //p.sendMessage("/wps <add|create> <name> [world] [x] [y] [z]");

            if (permissionsEnabled && !p.hasPermission("waypoints.add") && !p.hasPermission("waypoints.create")) {
                p.sendMessage("You do not have the required permissions to run that command.\n"
                        + "Required permissions node is any of the following:"
                        + "\n\twaypoints.add"
                        + "\n\twaypoints.create");
                return;
            } else if (additionalArgs.length == 5) {
                String name = additionalArgs[0];
                String world = additionalArgs[1];
                float xCoord = Float.parseFloat(additionalArgs[2]);
                float yCoord = Float.parseFloat(additionalArgs[3]);
                float zCoord = Float.parseFloat(additionalArgs[4]);
                doCreateRemote(p, name, world, xCoord, yCoord, zCoord);
            } else if (additionalArgs.length == 1) {
                String name = additionalArgs[0];
                doCreateLocal(p, name);
            } else {
                p.sendMessage("This command was formatted incorrectly.");
                handlePlayerHelp(p, "add");
            }
        }
        if (wpsCommand.equalsIgnoreCase("delete") || wpsCommand.equalsIgnoreCase("remove")) {
            //p.sendMessage("/wps <delete|remove> <name>");
            if (permissionsEnabled && !p.hasPermission("waypoints.delete") && !p.hasPermission("waypoints.remove")) {
                p.sendMessage("You do not have the required permissions to run that command."
                    + "Required permissions node is any of the following:"
                        + "\n\twaypoints.delete"
                        + "\n\twaypoints.remove");
                return;
            } else if (additionalArgs.length == 1) {
                String name = additionalArgs[0];
                doDelete(p, name);
            } else {
                handlePlayerHelp(p, "remove");
            }
        }
        if (wpsCommand.equalsIgnoreCase("list")) {
            //p.sendMessage("/wps list [world <world name> |page <#>]");
            if (permissionsEnabled && !p.hasPermission("waypoints.list")) {
                p.sendMessage("You do not have the required permissions to run that command.\n"
                        + "Required permissions node is any of the following:"
                        + "\n\twaypoints.list");
                return;
            }
            else if (additionalArgs.length == 2) {
                if (additionalArgs[0].equalsIgnoreCase("world")) {
                    String worldName = additionalArgs[1];
                    this.doListWorld(p, worldName);
                } else if (additionalArgs[0].equalsIgnoreCase("page")) {
                    int pageNumber = Integer.parseInt(additionalArgs[1]);
                    this.doListPage(p, pageNumber);
                }
            }
            else if (additionalArgs.length == 0) {
                Collection<Waypoint> c = plugin.getSortedWaypoints();
                for (Waypoint w : c) {
                    p.sendMessage(w.getName());
                }
            } else {
                p.sendMessage("This command was formatted incorrectly.");
                handlePlayerHelp(p, "list");
            }
        }
        if (wpsCommand.equalsIgnoreCase("return")) {
            //p.sendMessage("/wps return");
            if (permissionsEnabled && !p.hasPermission("waypoints.return")) {
                p.sendMessage("You do not have the required permissions to run that command.\n"
                        + "Required permissions node is any of the following:"
                        + "\n\twaypoints.return");
                return;
            }
            if (additionalArgs.length == 0) {
                doReturn(p);
            } else {
                p.sendMessage("This command was formatted incorrectly.");
                handlePlayerHelp(p, "return");
            }
        }
        if (wpsCommand.equalsIgnoreCase("go")) {
            //p.sendMessage("/wps go <name>");
            if (permissionsEnabled && !p.hasPermission("waypoints.go")) {
                p.sendMessage("You do not have the required permissions to run that command.\n"
                        + "Required permissions node is any of the following:"
                        + "\n\twaypoints.go");
                return;
            }
            if (additionalArgs.length == 1) {
                String destination = additionalArgs[0];
                doGo(p, destination);
            } else {
                p.sendMessage("This command was formatted incorrectly.");
                handlePlayerHelp(p, "go");
            }
        }
        if (wpsCommand.equalsIgnoreCase("save")) {
            //p.sendMessage("/wps go <name>");
            if (permissionsEnabled && !p.hasPermission("waypoints.save")) {
                p.sendMessage("You do not have the required permissions to run that command.\n"
                        + "Required permissions node is any of the following:"
                        + "\n\twaypoints.save");
                return;
            }
            if (additionalArgs.length == 0) {
                plugin.getFileManager().saveWpsFile();
                p.sendMessage("Waypoints saved successfully!");
            } else {
                p.sendMessage("This command was formatted incorrectly.");
                handlePlayerHelp(p, "save");
            }
        }
    }

    /**
     * Handles the command that the console has used
     *
     * @param wpsCommand the waypoint command used, eg. add or delete
     * @param additionalArgs the remaining arguments of the command issued
     */
    public void handleConsoleCommand(String wpsCommand, String[] additionalArgs) {

        if (wpsCommand.equalsIgnoreCase("add") || wpsCommand.equalsIgnoreCase("create")) {
            //p.sendMessage("/wps <add|create> <name> [world] [x] [y] [z]");
            if (additionalArgs.length == 5) {
                String name = additionalArgs[0];
                String world = additionalArgs[1];
                float xCoord = Float.parseFloat(additionalArgs[2]);
                float yCoord = Float.parseFloat(additionalArgs[3]);
                float zCoord = Float.parseFloat(additionalArgs[4]);
                doCreateRemote(name, world, xCoord, yCoord, zCoord);
            } else {
                System.out.println("This command was formatted incorrectly.");
                handleConsoleHelp("add");
            }
        }
        if (wpsCommand.equalsIgnoreCase("delete") || wpsCommand.equalsIgnoreCase("remove")) {
            //p.sendMessage("/wps <delete|remove> <name>");
            if (additionalArgs.length == 1) {
                String name = additionalArgs[0];
                doDelete(name);
            } else {
                System.out.println("This command was formatted incorrectly.");
                handleConsoleHelp("remove");
            }
        }
        if (wpsCommand.equalsIgnoreCase("list")) {
            //p.sendMessage("/wps list [world <world name> |page <#>]");
            if (additionalArgs.length == 2) {
                if (additionalArgs[0].equalsIgnoreCase("world")) {
                    String worldName = additionalArgs[1];
                    doListWorld(worldName);
                } else if (additionalArgs[0].equalsIgnoreCase("page")) {
                    int pageNumber = Integer.parseInt(additionalArgs[1]);
                    doListPage(pageNumber);
                }
            }
            if (additionalArgs.length == 0) {
                Collection<Waypoint> c = plugin.getSortedWaypoints();
                for (Waypoint w : c) {
                    System.out.println(w.getName());
                }
            } else {
                System.out.println("This command was formatted incorrectly.");
                handleConsoleHelp("list");
            }
        }
        if (wpsCommand.equalsIgnoreCase("save")) {
            if (additionalArgs.length == 0) {
                plugin.getFileManager().saveWpsFile();
                System.out.println("Waypoints saved successfully!");
            } else {
                System.out.println("This command was formatted incorrectly.");
                handleConsoleHelp("save");
            }
        }
    }

    /**
     * Displays basic help message
     *
     * @param p The player to display the message to
     */
    public void handlePlayerHelp(Player p) {
        String message = "Waypoints additional help:\n";
        message += "type /wps help <command> for more help.\n";
        message += "wps can be substituted with waypoints\n";
        message += "Waypoints uses a set of subcommands as follows:\n";
        message += "/wps create\n";
        message += "/wps add\n";
        message += "/wps delete\n";
        message += "/wps remove\n";
        message += "/wps list\n";
        message += "/wps go\n";
        message += "/wps return\n";
        message += "/wps save";
        p.sendMessage(message);
    }

    /**
     * Prints out extended help based on a subcommand of Waypoints
     *
     * @param p the player the message should go to
     * @param suboption the sub command of Waypoint that was used
     */
    public void handlePlayerHelp(Player p, String suboption) {
        if (suboption.equalsIgnoreCase("add") || suboption.equalsIgnoreCase("create")) {
            p.sendMessage("Additional help for /wps add:\n"
                    + "Parameters in <> are required.\n"
                    + "Parameters in [] are optional.\n"
                    + "A parameter split with a | is an alias,"
                    + "either one can be used (but only one at a time)\n"
                    + "/wps <add|create> <name> [world] [x] [y] [z]"
                    + "Examples:\n"
                    + "/wps add home\n\tCreates a waypoint called home"
                    + " at current location."
                    + "/wps create home homeWorld 0 0 0\n\tCreates a"
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
            p.sendMessage("Examples:");
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
            p.sendMessage("Examples:");
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
            p.sendMessage("Examples:");
            p.sendMessage("/wps go home\n\tTeleports you to the home waypoint.");
        }
        if (suboption.equalsIgnoreCase("return")) {
            p.sendMessage("Additional help for /wps return:");
            p.sendMessage("Parameters in <> are required.\n"
                    + "Parameters in [] are optional.\n"
                    + "A parameter split with a | is an alias,"
                    + "either one can be used (but only one at a time)");
            p.sendMessage("/wps return");
            p.sendMessage("Examples:");
            p.sendMessage("/wps return\n\tReturns you to the location you were"
                    + " at before issuing the last /wps go command.");
        }
        if (suboption.equalsIgnoreCase("save")) {
            p.sendMessage("Additional help for /wps save:");
            p.sendMessage("Parameters in <> are required.\n"
                    + "Parameters in [] are optional.\n"
                    + "A parameter split with a | is an alias,"
                    + "either one can be used (but only one at a time)");
            p.sendMessage("/wps save");
            p.sendMessage("Examples:");
            p.sendMessage("/wps save\n\tSaves the waypoints to the save file.");
        }
    }

    /**
     * Displays basic help message inside the server console
     */
    public void handleConsoleHelp() {
        String message = "Waypoints additional help:\n";
        message += "type /wps help <command> for more help.\n";
        message += "wps can be substituted with waypoints\n";
        message += "Waypoints uses a set of subcommands as follows:\n";
        message += "/wps create\n";
        message += "/wps add\n";
        message += "/wps delete\n";
        message += "/wps remove\n";
        message += "/wps list\n";
        message += "/wps save";
        System.out.println(message);
    }

    /**
     * Displays specific help messages inside the server console
     *
     * @param suboption The sub command to display help for
     */
    public void handleConsoleHelp(String suboption) {
        if (suboption.equalsIgnoreCase("add") || suboption.equalsIgnoreCase("create")) {
            System.out.println("Additional help for /wps add:");
            System.out.println("Parameters in <> are required.\n"
                    + "Parameters in [] are optional.\n"
                    + "A parameter split with a | is an alias,"
                    + "either one can be used (but only one at a time)");
            System.out.println("/wps <add|create> <name> [world] [x] [y] [z]");
            System.out.println("Examples:");
            System.out.println("/wps add home\n\tCreates a waypoint called home"
                    + " at current location.");
            System.out.println("/wps create home homeWorld 0 0 0\n\tCreates a"
                    + "waypoint called home at x:0 y:0 z:0.");
        }
        if (suboption.equalsIgnoreCase("delete")
                || suboption.equalsIgnoreCase("remove")) {
            System.out.println("Additional help for /wps delete:");
            System.out.println("Parameters in <> are required.\n"
                    + "Parameters in [] are optional.\n"
                    + "A parameter split with a | is an alias,"
                    + "either one can be used (but only one at a time)");
            System.out.println("/wps <delete|remove> <name>");
            System.out.println("Examples:");
            System.out.println("/wps delete home\n\tDeletes the waypoint called home.");
            System.out.println("/wps remove home\n\tDeletes the waypoint called home.");
        }
        if (suboption.equalsIgnoreCase("list")) {
            System.out.println("Additional help for /wps list:");
            System.out.println("Parameters in <> are required.\n"
                    + "Parameters in [] are optional.\n"
                    + "A parameter split with a | is an alias,"
                    + "either one can be used (but only one at a time)");
            System.out.println("/wps list [world <world name> | page <#>]");
            System.out.println("Examples:");
            System.out.println("/wps list\n\tLists all waypoints that exist.");
            System.out.println("/wps list world homeworld\n\tLists all waypoints"
                    + "that exist on the world homeworld.");
            System.out.println("/wps list page 2\n\tLists waypoints in a page format"
                    + " starting on page two.");
        }
        if (suboption.equalsIgnoreCase("save")) {
            System.out.println("Additional help for /wps save:");
            System.out.println("Parameters in <> are required.\n"
                    + "Parameters in [] are optional.\n"
                    + "A parameter split with a | is an alias,"
                    + "either one can be used (but only one at a time)");
            System.out.println("/wps save");
            System.out.println("Examples:");
            System.out.println("/wps save\n\tSaves the waypoints to the save file.");
        }
    }
}
