/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package waypoints;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Andrew
 */
public abstract class PermissionBase {
    public Waypoints waypoints;
    public PermissionBase(Waypoints waypoints)
    {
        this.waypoints = waypoints;
    }
    public abstract boolean handleCommand(Player committingPlayer, String[] args);

    public boolean doesWaypointExist(String name)
    {
        for (Waypoint point : waypoints.waypointList)
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
        for (int i = 0; i < waypoints.waypointList.size(); i++)
        {
            if (waypoints.waypointList.get(i).name.equalsIgnoreCase(name))
            {
                return waypoints.waypointList.get(i);
            }
        }
        return null;
    }

    public int getWaypointIndex(String name)
    {
        for (int i = 0; i < waypoints.waypointList.size(); i++)
        {
            if (waypoints.waypointList.get(i).name.equalsIgnoreCase(name))
            {
                return i;
            }
        }

        return -1;
    }

    public boolean deleteWaypoint(String[] args)
    {
        String name = args[1];
        if (doesWaypointExist(name))
        {
            int index = getWaypointIndex(name);
            if (index != -1)
            {
                waypoints.waypointList.remove(index);
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
            if (waypoints.lastLocation.containsKey(committingPlayer))
            {
                waypoints.lastLocation.remove(committingPlayer);
            }
            waypoints.lastLocation.put(committingPlayer, committingPlayer.getLocation());
            committingPlayer.teleport(getWaypoint(name).location);
            return true;
        }
        return false;
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
            waypoints.waypointList.add(new Waypoint(name, loc));
            committingPlayer.sendMessage("Waypoint " + name + " created at " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + ".");
            return true;
        } else
        {
            committingPlayer.sendMessage("Waypoint with that name already exists.");
        }
        return false;
    }

    public boolean returnToPoint(Player committingPlayer, String[] args)
    {
        if (waypoints.lastLocation.containsKey(committingPlayer))
        {
            Location loc = waypoints.lastLocation.get(committingPlayer);
            committingPlayer.teleport(loc);
            waypoints.lastLocation.remove(committingPlayer);
            return true;
        }
        return false;
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

    public void sort(ArrayList<Waypoint> startList, ArrayList<Waypoint> finalList)
    {
        ArrayList<Waypoint> dummyList = new ArrayList<Waypoint>();
        for (int i = 0; i < startList.size(); i++)
        {
            dummyList.add(startList.get(i));
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

    public boolean listWaypoints(Player committingPlayer, String[] args)
    {
        ArrayList<Waypoint> dummyList = new ArrayList<Waypoint>();
        for (int i = 0; i < waypoints.waypointList.size(); i++)
        {
            dummyList.add(waypoints.waypointList.get(i));
        }

        ArrayList<Waypoint> sortedList = new ArrayList<Waypoint>();
        sort(dummyList, sortedList);

        for (int i = 0; i < sortedList.size(); i++)
        {
            committingPlayer.sendMessage("§e" + sortedList.get(i).name + "§f [x,y,z] ["
                    + (int) sortedList.get(i).location.getX() + "," + (int) sortedList.get(i).location.getY() + "," + (int) sortedList.get(i).location.getZ() + "]");

        }
        return true;
    }
}
