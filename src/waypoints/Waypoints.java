package waypoints;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Andrew
 */
public class Waypoints extends JavaPlugin
{

    Logger log = Logger.getLogger("Minecraft");
    PluginManager pm;
    List<Waypoint> waypointList = new ArrayList<Waypoint>();
    Map<Player, Location> lastLocation = new HashMap<Player, Location>();

    public void onEnable()
    {
        pm = this.getServer().getPluginManager();
        loadWaypoints();
        log.info("Waypoints has loaded.");
    }

    public void onDisable()
    {
        pm = null;

        saveWaypoints();
        log.info("World's Waypoints has unloaded.");
        log = null;
    }

    public void onSave()
    {
        saveWaypoints();
    }

    public boolean doesWaypointExist(String name)
    {
        for (Waypoint point : waypointList)
        {
            if (point.name.equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return false;
    }

    public Waypoint getWaypoint(String name)
    {
        for (int i = 0; i < waypointList.size(); i++)
        {
            if (waypointList.get(i).name.equalsIgnoreCase(name))
            {
                return waypointList.get(i);
            }
        }
        return null;
    }

    public int getWaypointIndex(String name)
    {
        for (int i = 0; i < waypointList.size(); i++)
        {
            if (waypointList.get(i).name.equalsIgnoreCase(name))
            {
                return i;
            }
        }

        return -1;
    }

    public boolean createWaypoint(Player committingPlayer, String[] args)
    {
        String name = args[1];
        Location loc;
        if (args.length > 2)
        {
            int xCoord = Integer.parseInt(args[2]);
            int yCoord = Integer.parseInt(args[3]);
            int zCoord = Integer.parseInt(args[4]);
            loc = new Location(committingPlayer.getWorld(), xCoord, yCoord, zCoord);
        } else
        {
            loc = committingPlayer.getLocation();
        }
        if (!doesWaypointExist(name))
        {
            waypointList.add(new Waypoint(name, loc));
            committingPlayer.sendMessage("Waypoint " + name + " created at " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + ".");
            return true;
        } else
        {
            committingPlayer.sendMessage("Waypoint with that name already exists.");
        }
        return false;
    }

    public boolean deleteWaypoint(String[] args)
    {
        String name = args[1];
        if (doesWaypointExist(name))
        {
            int index = getWaypointIndex(name);
            if (index != -1)
            {
                waypointList.remove(index);
                return true;
            }
        }
        return false;
    }

    public boolean goWaypoint(Player committingPlayer, String[] args)
    {
        String name = args[1];
        if (doesWaypointExist(name))
        {
            if (lastLocation.containsKey(committingPlayer))
            {
                lastLocation.remove(committingPlayer);
            }
            lastLocation.put(committingPlayer, committingPlayer.getLocation());
            committingPlayer.teleport(getWaypoint(name).location);
            return true;
        }
        return false;
    }
    
    public boolean listWaypoints(Player committingPlayer, String[] args)
    {
        ArrayList<Waypoint> dummyList = new ArrayList<Waypoint>();
        for (int i = 0; i < waypointList.size(); i++)
        {
            dummyList.add(waypointList.get(i));
        }

        ArrayList<Waypoint> sortedList = new ArrayList<Waypoint>();
        sort(sortedList);

        for (int i = 0; i < sortedList.size(); i++)
        {
            committingPlayer.sendMessage("" + sortedList.get(i).name + " [x,y,z] ["
                    + (int) sortedList.get(i).location.getX() + "," + (int) sortedList.get(i).location.getY() + "," + (int) sortedList.get(i).location.getZ() + "]");

        }
        return true;
    }

    public void sort(ArrayList<Waypoint> finalList)
    {
        ArrayList<Waypoint> dummyList = new ArrayList<Waypoint>();
        for (int i = 0; i < waypointList.size(); i++)
        {
            dummyList.add(waypointList.get(i));
        }

        int smallestIndex = Integer.MAX_VALUE;

        for (int i = 0; i < dummyList.size(); i++)
        {
            if (smallestIndex == Integer.MAX_VALUE || dummyList.get(i).name.compareTo(dummyList.get(smallestIndex).name) < 0)
            {
                smallestIndex = i;
            }
            if (i == (dummyList.size() - 1))
            {
                finalList.add(dummyList.get(smallestIndex));
                dummyList.remove(smallestIndex);
                i = -1;
                smallestIndex = Integer.MAX_VALUE;
            }
        }
    }

    public boolean printHelp(Player committingPlayer, String[] args)
    {
        committingPlayer.sendMessage("/waypoints create(add) <name> [x] [y] [z]");
        committingPlayer.sendMessage("/waypoints go <name>");
        committingPlayer.sendMessage("/waypoints delete <name>");
        committingPlayer.sendMessage("/waypoints list");
        committingPlayer.sendMessage("/waypoints save");
        committingPlayer.sendMessage("/waypoints load");
        committingPlayer.sendMessage("/waypoints return");
        return true;
    }

    public boolean returnToPoint(Player committingPlayer, String[] args)
    {
        if (lastLocation.containsKey(committingPlayer))
        {
            Location loc = lastLocation.get(committingPlayer);
            committingPlayer.teleport(loc);
            lastLocation.remove(committingPlayer);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {

        try
        {
            if (cmd.getName().equalsIgnoreCase("waypoints"))
            {
                Player committingPlayer = (Player) sender;
                if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("add"))
                {
                    return createWaypoint(committingPlayer, args);
                } else if (args[0].equalsIgnoreCase("go"))
                {
                    return goWaypoint(committingPlayer, args);
                } else if (args[0].equalsIgnoreCase("delete"))
                {
                    return deleteWaypoint(args);
                } else if (args[0].equalsIgnoreCase("save"))
                {
                    return saveWaypoints();
                } else if (args[0].equalsIgnoreCase("load"))
                {
                    return loadWaypoints();
                } else if (args[0].equalsIgnoreCase("list"))
                {
                    return listWaypoints(committingPlayer, args);
                } else if (args[0].equalsIgnoreCase("help"))
                {
                    return printHelp(committingPlayer, args);
                } else if (args[0].equalsIgnoreCase("return"))
                {
                    return returnToPoint(committingPlayer, args);
                }
            }
        } catch (Exception e)
        {
        }
        return false;
    }

    /*
     * TODO: Get system file sparators! (Does this need doing?)
     */
    public boolean loadWaypoints()
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
            return true;
        } catch (FileNotFoundException ex)
        {
            log.info("Failed to read items for waypoints. Do files exist?");
        }

        return false;
    }

    public boolean saveWaypoints()
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
                try {
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

            if (!worldsFile.exists())
            {
                try
                {
                    worldsFile.createNewFile();
                } catch (IOException ex)
                {
                    log.info("Failed to create /waypoints/zCoords.xml for Waypoints");
                }
            }
            XMLEncoder zWriter = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(zCoordsPath)));
            zWriter.writeObject(zCoords);
            zWriter.flush();
            zWriter.close();

            return true;
        } catch (FileNotFoundException ex)
        {
            log.info("Failed to write files for waypoints.");
        }

        return false;
    }
}
