package com.github.sessional.waypoints;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

public class WpsPlugin extends JavaPlugin
{

    /**
     * 
     */
    private Logger log;
    /**
     * 
     */
    private List<Waypoint> waypointData;
    /**
     * 
     */
    private CommandHandler commandHandler;
    /**
     * 
     */
    private File saveFile;
    /**
     * 
     */
    private List<File> oldFiles;
    /**
     * 
     */
    private File configFile;
    /**
     * 
     */
    private String version = "1.2.2";
    /**
     * 
     */
    private DynmapAPI dApi;
    private MarkerAPI mark;
    private MarkerSet set;

    /**
     * 
     */
    @Override
    public void onEnable()
    {
        log = getLogger();

        saveFile = new File("./plugins/Waypoints/waypoints.dat");
        if (saveFile.exists())
        {
            loadData();
        } else
        {
            waypointData = new ArrayList<Waypoint>();
            oldFiles = new ArrayList<File>();
            oldFiles.add(new File("./plugins/Waypoints/names.xml"));
            oldFiles.add(new File("./plugins/Waypoints/worlds.xml"));
            oldFiles.add(new File("./plugins/Waypoints/xCoords.xml"));
            oldFiles.add(new File("./plugins/Waypoints/yCoords.xml"));
            oldFiles.add(new File("./plugins/Waypoints/zCoords.xml"));

            if (oldFilesExist())
            {
                loadOldWaypoints();
                this.saveData();
            }
        }

        configFile = new File("./plugins/Waypoints/config.yml");
        if (!configFile.exists())
        {
            this.saveDefaultConfig();
        }

        if (this.getConfig().getBoolean("bukkitPermissions") == true)
        {
            commandHandler = new BukkitCommandHandler(this);
        } else
        {
            commandHandler = new CommandHandler(this);
        }

        if (this.getConfig().getBoolean("dynmapSupport") == true)
        {
            dApi = (DynmapAPI) getServer().getPluginManager().getPlugin("dynmap");

            mark = dApi.getMarkerAPI();
            set = mark.createMarkerSet("waypointMarkers", "waypoints", null, false);
            set.setLabelShow(false);
            enableDynMapSupport();
        }

        log.info("Waypoints is now enabled.");
    }

    private void enableDynMapSupport()
    {
        for (Waypoint w : getWaypoints())
        {
            addToDynMap(w);
        }
    }

    public void addToDynMap(Waypoint wp)
    {
        set.createMarker("wp" + wp.getName(), wp.getName(), wp.getWorld(), wp.getX(), wp.getY(), wp.getZ(), mark.getMarkerIcon("redflag"), true);
    }

    public void removeFromDynMap(Waypoint wp)
    {
        for (Marker m : set.getMarkers())
        {
            if (m.getLabel().equalsIgnoreCase(wp.getName()))
            {
                m.deleteMarker();
            }
        }
    }
    
    /**
     * 
     * @return 
     */
    private boolean oldFilesExist()
    {
        for (File f : oldFiles)
        {
            if (!f.exists())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @return 
     */
    public List<Waypoint> getWaypoints()
    {
        return waypointData;
    }

    /**
     * 
     */
    @Override
    public void onDisable()
    {
        saveData();
        for (Waypoint w : getWaypoints())
        {
            if (getConfig().getBoolean("dynMapSupport") == true)
            {
                removeFromDynMap(w);
            }
        }
        commandHandler = null;
        waypointData = null;
        configFile = null;
        log = null;
        oldFiles = null;
    }

    /**
     * 
     * @param sender
     * @param cmd
     * @param commandLabel
     * @param args
     * @return 
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = null;
        if (sender instanceof Player)
        {
            player = (Player) sender;
        }

        if (args.length <= 0)
        {
            if (player == null)
            {
                getLogger().info("Waypoints requires more arguments.");
            } else
            {
                player.sendMessage("Waypoints requires more arguments.");
            }
            return true;
        }

        String subCommand = args[0];
        String[] remainingArgs = null;
        if (args.length > 1)
        {
            remainingArgs = new String[args.length - 1];
            for (int i = 1; i < args.length; i++)
            {
                remainingArgs[i - 1] = args[i];
            }
        }
        return commandHandler.handleCommand(player, subCommand, remainingArgs);
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    /**
     * Attempts to save the waypoint list into a single save file
     * @return true if it works, false if not
     */
    public boolean saveData()
    {
        try
        {
            if (!saveFile.exists())
            {
                saveFile.createNewFile();
            }
            FileOutputStream fsOut = new FileOutputStream(saveFile);
            BufferedOutputStream bOut = new BufferedOutputStream(fsOut);
            ObjectOutputStream oOut = new ObjectOutputStream(bOut);
            oOut.writeObject(waypointData);

            oOut.close();
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Loads the new single save file for Waypoints data
     * @return true if it works, false if not.
     */
    public boolean loadData()
    {
        try
        {
            FileInputStream fsIn = new FileInputStream(saveFile);
            BufferedInputStream bIn = new BufferedInputStream(fsIn);
            ObjectInputStream oIn = new ObjectInputStream(bIn);
            waypointData = (List<Waypoint>) oIn.readObject();
            oIn.close();
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Will get the version number alone as a string
     * @return the version number as a string
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Loads old waypoint data from the 5 file system so that the program can
     * update it to the single save system
     * @return true if it works, false if not
     */
    public boolean loadOldWaypoints()
    {
        String namesPath = "./plugins/Waypoints/names.xml";
        String worldsPath = "./plugins/Waypoints/worlds.xml";
        String xCoordsPath = "./plugins/Waypoints/xCoords.xml";
        String yCoordsPath = "./plugins/Waypoints/yCoords.xml";
        String zCoordsPath = "./plugins/Waypoints/zCoords.xml";
        String[] waypoints = null;
        String[] worlds = null;
        int[] xCoords = null;
        int[] yCoords = null;
        int[] zCoords = null;
        File waypointsFile = oldFiles.get(0);
        File worldsFile = oldFiles.get(1);
        File xCoordsFile = oldFiles.get(2);
        File yCoordsFile = oldFiles.get(3);
        File zCoordsFile = oldFiles.get(4);
        try
        {

            if (waypointsFile.exists() && waypointsFile.canRead())
            {
                XMLDecoder nameReader = new XMLDecoder(new BufferedInputStream(new FileInputStream(waypointsFile)));
                waypoints = (String[]) nameReader.readObject();
                nameReader.close();
            }
            if (worldsFile.exists() && worldsFile.canRead())
            {
                XMLDecoder worldReader = new XMLDecoder(new BufferedInputStream(new FileInputStream(worldsFile)));
                worlds = (String[]) worldReader.readObject();
                worldReader.close();
            }
            if (xCoordsFile.exists() && xCoordsFile.canRead())
            {
                XMLDecoder xReader = new XMLDecoder(new BufferedInputStream(new FileInputStream(xCoordsFile)));
                xCoords = (int[]) xReader.readObject();
                xReader.close();
            }
            if (yCoordsFile.exists() && yCoordsFile.canRead())
            {
                XMLDecoder yReader = new XMLDecoder(new BufferedInputStream(new FileInputStream(yCoordsFile)));
                yCoords = (int[]) yReader.readObject();
                yReader.close();
            }
            if (zCoordsFile.exists() && zCoordsFile.canRead())
            {
                XMLDecoder zReader = new XMLDecoder(new BufferedInputStream(new FileInputStream(zCoordsFile)));
                zCoords = (int[]) zReader.readObject();
                zReader.close();
            }
            if (waypoints != null && worlds != null && xCoords != null && yCoords != null && zCoords != null)
            {
                for (int i = 0; i < waypoints.length; i++)
                {
                    this.getWaypoints().add(new Waypoint((double) xCoords[i], (double) yCoords[i], (double) zCoords[i], waypoints[i], worlds[i]));
                }
            }
            return true;
        } catch (FileNotFoundException ex)
        {
        }
        return false;
    }
}
