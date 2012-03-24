package waypoints;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.Collections;
import java.util.LinkedList;

public class Waypoints extends JavaPlugin
{

    /**
     * Configuration fields
     */
    String versionNum = "0.7";
    Logger log = Logger.getLogger("Minecraft");
    PluginManager pm;
    public List<Waypoint> waypointList = new LinkedList<Waypoint>();
    public Map<Player, Location> lastLocation = new HashMap<Player, Location>();
    public static PermissionHandler permissionHandler;
    public Configurations configs;
    public PermissionBase commandHandler;

    public void onEnable()
    {
        log.info("Waypoints is loading.");
        pm = this.getServer().getPluginManager();
        loadWaypoints(null);
        if (!loadConfigurations())
        {
            configs = new Configurations();
            commandHandler = new PermissionsDefault(this);
            System.out.println("Creating new configurations.");
        } else
        {
            if (configs.permissionsMod)
            {
                setupPermissions();
            } else if (configs.permissionsBukkit)
            {
                commandHandler = new PermissionsBukkitHandler(this);
            } else
            {
                commandHandler = new PermissionsDefault(this);
            }
        }
        sortWaypoints();
        log.info("Waypoints has loaded: version: " + versionNum);
    }

    private void sortWaypoints()
    {
        Collections.sort(waypointList);
    }

    public void setupPermissions()
    {
        if (permissionHandler != null)
        {
            return;
        }

        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (permissionsPlugin == null)
        {
            configs.permissionsMod = false;
            log.info("Did not find permissions plugin, ignoring them.");
            return;
        }

        configs.permissionsMod = true;

        permissionHandler = ((Permissions) permissionsPlugin).getHandler();
        commandHandler = new PermissionsHandler(this, permissionHandler);
        log.info("Found and will use plugin " + ((Permissions) permissionsPlugin).getDescription().getFullName() + " for permissions.");
    }

    public void onDisable()
    {
        pm = null;

        saveWaypoints(null);
        log.info("Waypoints has unloaded.");
        log = null;
    }

    public void onSave()
    {
        saveWaypoints(null);
        saveConfigurations();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (args.length == 0)
        {
            ((Player)sender).sendMessage("Please input at least one argument.");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("waypoints") || cmd.getName().equalsIgnoreCase("wps"))
        {
            Player committingPlayer = (Player) sender;
            return commandHandler.handleCommand(committingPlayer, args);
        }
        return false;
    }
    
    public String getVersion()
    {
        return "Waypoints is version: " + versionNum;
    }

    public void setConfigs(Configurations configs)
    {
        this.configs = configs;
    }

    /*
     * TODO: Get system file sparators! (Does this need doing?)
     */
    public boolean loadWaypoints(Player committingPlayer)
    {
        String waypointsNamePath = "./plugins/Waypoints/names.xml";
        String worldsPath = "./plugins/Waypoints/worlds.xml";
        String xCoordsPath = "./plugins/Waypoints/xCoords.xml";
        String yCoordsPath = "./plugins/Waypoints/yCoords.xml";
        String zCoordsPath = "./plugins/Waypoints/zCoords.xml";
        String[] waypoints = null;
        String[] worlds = null;
        int[] xCoords = null;
        int[] yCoords = null;
        int[] zCoords = null;
        File waypointsFile = new File(waypointsNamePath);
        File worldsFile = new File(worldsPath);
        File xCoordsFile = new File(xCoordsPath);
        File yCoordsFile = new File(yCoordsPath);
        File zCoordsFile = new File(zCoordsPath);
        try
        {

            if (waypointsFile.exists() && waypointsFile.canRead())
            {
                XMLDecoder nameReader = new XMLDecoder(new BufferedInputStream(new FileInputStream(waypointsNamePath)));
                waypoints = (String[]) nameReader.readObject();
                nameReader.close();
            }
            if (worldsFile.exists() && worldsFile.canRead())
            {
                XMLDecoder worldReader = new XMLDecoder(new BufferedInputStream(new FileInputStream(worldsPath)));
                worlds = (String[]) worldReader.readObject();
                worldReader.close();
            }
            if (xCoordsFile.exists() && xCoordsFile.canRead())
            {
                XMLDecoder xReader = new XMLDecoder(new BufferedInputStream(new FileInputStream(xCoordsPath)));
                xCoords = (int[]) xReader.readObject();
                xReader.close();
            }
            if (yCoordsFile.exists() && yCoordsFile.canRead())
            {
                XMLDecoder yReader = new XMLDecoder(new BufferedInputStream(new FileInputStream(yCoordsPath)));
                yCoords = (int[]) yReader.readObject();
                yReader.close();
            }
            if (zCoordsFile.exists() && zCoordsFile.canRead())
            {
                XMLDecoder zReader = new XMLDecoder(new BufferedInputStream(new FileInputStream(zCoordsPath)));
                zCoords = (int[]) zReader.readObject();
                zReader.close();
            }
            if (waypoints != null && worlds != null && xCoords != null && yCoords != null && zCoords != null)
            {
                for (int i = 0; i < waypoints.length; i++)
                {
                    waypointList.add(new Waypoint(waypoints[i], new Location(getServer().getWorld(worlds[i]), xCoords[i], yCoords[i], zCoords[i])));
                }
            }
            if (committingPlayer != null)
            {
                committingPlayer.sendMessage("Waypoints have been loaded.");
            }
            return true;
        } catch (FileNotFoundException ex)
        {
        }
        return false;
    }

    public boolean saveWaypoints(Player committingPlayer)
    {
        String[] waypoints = new String[waypointList.size()];
        String[] worlds = new String[waypointList.size()];
        int[] xCoords = new int[waypointList.size()];
        int[] yCoords = new int[waypointList.size()];
        int[] zCoords = new int[waypointList.size()];
        for (int i = 0; i < waypointList.size(); i++)
        {
            waypoints[i] = waypointList.get(i).name;
            worlds[i] = waypointList.get(i).location.getWorld().getName();
            xCoords[i] = waypointList.get(i).location.getBlockX();
            yCoords[i] = waypointList.get(i).location.getBlockY();
            zCoords[i] = waypointList.get(i).location.getBlockZ();
        }
        String waypointsNamePath = "./plugins/Waypoints/names.xml";
        String worldsPath = "./plugins/Waypoints/worlds.xml";
        String xCoordsPath = "./plugins/Waypoints/xCoords.xml";
        String yCoordsPath = "./plugins/Waypoints/yCoords.xml";
        String zCoordsPath = "./plugins/Waypoints/zCoords.xml";
        String pluginPath = "./plugins/Waypoints";
        File waypointsFile = new File(waypointsNamePath);
        File worldsFile = new File(worldsPath);
        File xCoordsFile = new File(xCoordsPath);
        File yCoordsFile = new File(yCoordsPath);
        File zCoordsFile = new File(zCoordsPath);
        File pluginFile = new File(pluginPath);
        try
        {
            if (!pluginFile.exists())
            {
                try
                {
                    pluginFile.mkdir();
                } catch (Exception e)
                {
                    log.info("Failed to create directory for Waypoints");
                }
            }
            if (!waypointsFile.exists())
            {
                try
                {
                    waypointsFile.createNewFile();
                } catch (IOException ex)
                {
                    log.info("Failed to create /waypoints/names.xml for Waypoints");
                }
            }
            XMLEncoder nameWriter = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(waypointsNamePath)));
            nameWriter.writeObject(waypoints);
            nameWriter.flush();
            nameWriter.close();
            if (!worldsFile.exists())
            {
                try
                {
                    worldsFile.createNewFile();
                } catch (IOException ex)
                {
                    log.info("Failed to create /waypoints/worlds.xml for Waypoints");
                }
            }
            XMLEncoder worldWriter = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(worldsPath)));
            worldWriter.writeObject(worlds);
            worldWriter.flush();
            worldWriter.close();
            if (!xCoordsFile.exists())
            {
                try
                {
                    xCoordsFile.createNewFile();
                } catch (IOException ex)
                {
                    log.info("Failed to create /waypoints/xCoords.xml for Waypoints");
                }
            }
            XMLEncoder xWriter = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(xCoordsPath)));
            xWriter.writeObject(xCoords);
            xWriter.flush();
            xWriter.close();
            if (!yCoordsFile.exists())
            {
                try
                {
                    yCoordsFile.createNewFile();
                } catch (IOException ex)
                {
                    log.info("Failed to create /waypoints/yCoords.xml for Waypoints");
                }
            }
            XMLEncoder yWriter = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(yCoordsPath)));
            yWriter.writeObject(yCoords);
            yWriter.flush();
            yWriter.close();
            if (!zCoordsFile.exists())
            {
                try
                {
                    zCoordsFile.createNewFile();
                } catch (IOException ex)
                {
                    log.info("Failed to create /waypoints/zCoords.xml for Waypoints");
                }
            }
            XMLEncoder zWriter = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(zCoordsPath)));
            zWriter.writeObject(zCoords);
            zWriter.flush();
            zWriter.close();
            if (committingPlayer != null)
            {
                committingPlayer.sendMessage("Waypoints have been saved.");
            }
            return true;
        } catch (FileNotFoundException ex)
        {
        }
        return false;
    }

    public boolean loadConfigurations()
    {
        String configPath = "./plugins/Waypoints";
        File configFile = new File(configPath + "/configs.cfg");
        Properties prop = new Properties();
        if (!new File(configPath).isDirectory())
        {
            new File(configPath).mkdir();
        }
        configs = new Configurations();
        if (configFile.exists())
        {
            FileInputStream input = null;
            try
            {
                input = new FileInputStream(configFile);
                prop.load(input);
                configs.permissionsMod = Boolean.parseBoolean(prop.getProperty("permissionsMod"));
                configs.permissionsBukkit = Boolean.parseBoolean(prop.getProperty("permissionsBukkit"));
                input.close();
                return true;
            } catch (Exception ex)
            {
            }
        }
        return false;
    }

    public boolean saveConfigurations()
    {
        String configPath = "./plugins/Waypoints";
        File configFile = new File(configPath + "/configs.cfg");
        Properties prop = new Properties();

        if (!new File(configPath).isDirectory())
        {
            new File(configPath).mkdir();
        }
        if (!configFile.exists())
        {
            try
            {
                configFile.createNewFile();
            } catch (IOException ex)
            {
            }
        }
        FileOutputStream configOutput;
        try
        {
            configOutput = new FileOutputStream(configFile);
            prop.put("permissionsMod", "" + configs.permissionsMod);
            prop.put("permissionsBukkit", "" + configs.permissionsBukkit);
            prop.store(configOutput, "Permissions values can only be true and false");
            configOutput.flush();
            configOutput.close();
        } catch (Exception ex)
        {
        }
        return false;
    }
}
