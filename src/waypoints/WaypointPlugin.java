package waypoints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WaypointPlugin extends JavaPlugin {

    private Map<String, Waypoint> waypointStorage;
    private Map<String, Location> returnPoints;
    private FileManager fileManager;
    private ConfigManager configManager;
    private CommandHandler commandHandler;

    /**
     * Called when the script is enabled by Bukkit.
     */
    @Override
    public void onEnable() {
        waypointStorage = new HashMap<String, Waypoint>();
        returnPoints = new HashMap<String, Location>();
        configManager = new ConfigManager(this);
        configManager.handleConfigGeneration();
        fileManager = new FileManager(this);
        fileManager.loadWaypoints();
        commandHandler = new CommandHandler(this);
    }

    /**
     * Called when the script is disabled by Bukkit.
     */
    @Override
    public void onDisable() {
        fileManager.saveWpsFile();
        fileManager = null;
        configManager = null;
        commandHandler = null;
        waypointStorage = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }

        if (p != null) {
            if (args.length < 1) {
                commandHandler.handlePlayerHelp(p);
            }

            if (args[0].equalsIgnoreCase("help")) {
                if (args.length >= 2)
                {
                    commandHandler.handlePlayerHelp(p, args[1]);
                }
                if (args.length == 1)
                {
                    commandHandler.handlePlayerHelp(p);
                }
            }

            String wpsCommand = args[0];

            String[] additionalArgs = new String[args.length - 1];
            System.arraycopy(args, 1, additionalArgs, 0, args.length - 1);

            getCommandHandler().handlePlayerCommand(p, wpsCommand, additionalArgs);
        }
        if (p == null)
        {
            if (args.length < 1) {
                commandHandler.handleConsoleHelp();
            }

            if (args[0].equalsIgnoreCase("help")) {
                if (args.length >= 2)
                {
                    commandHandler.handleConsoleHelp(args[1]);
                }
                if (args.length == 1)
                {
                    commandHandler.handleConsoleHelp();
                }
            }

            String wpsCommand = args[0];

            String[] additionalArgs = new String[args.length - 1];
            System.arraycopy(args, 1, additionalArgs, 0, args.length - 1);

            getCommandHandler().handleConsoleCommand(wpsCommand, additionalArgs);
        }
        
        return true;
    }

    /**
     *
     * @return the instance of the file manager
     */
    public FileManager getFileManager() {
        return fileManager;
    }

    /**
     *
     * @return the instance of the command handler
     */
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    /**
     *
     * @return the instance of the config manager
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     *
     * @return the collection of Waypoints that exist
     */
    public Collection<Waypoint> getWaypoints() {
        return waypointStorage.values();
    }

    /**
     *
     * @return the collection of Waypoints that exist sorted
     */
    public Collection<Waypoint> getSortedWaypoints() {
        ArrayList t = new ArrayList<Waypoint>(waypointStorage.values());
        Collections.sort(t);
        return t;
    }

    /**
     *
     * @return the map that stores the Waypoints
     */
    public Map<String, Waypoint> getWaypointStorage() {
        return waypointStorage;
    }
    
    /**
     *
     * @return the map that stores the Waypoints
     */
    public Map<String, Location> getReturnPoints() {
        return returnPoints;
    }

    /**
     * Shows a severe error message for this plugin using the logger that Bukkit
     * provides.
     *
     * @param message The message to be printed as a severe error
     */
    public void executeSevereError(String message) {
        getLogger().log(Level.SEVERE, message);
    }

    /**
     * Checks to see if a waypoint with that name exists.
     * @param waypointName
     * @return if the Waypoint of that name is in the hashmap
     */
    public boolean doesWaypointExist(String waypointName) {
        return waypointStorage.containsKey(waypointName);
    }

    public Waypoint getWaypoint(String waypointName) {
        return waypointStorage.get(waypointName);
    }
}
