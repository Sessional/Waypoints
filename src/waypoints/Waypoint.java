package waypoints;

import org.bukkit.Location;

public class Waypoint implements Comparable {
    Waypoint(String name, Location location)
    {
        this.location = location;
        this.name = name;
    }
    public Location location;
    public String name;

    @Override
    public String toString()
    {
        return name;
    }

    public int compareTo(Object o)
    {
        Waypoint otherWaypoint = (Waypoint)o;
        return this.name.compareToIgnoreCase(otherWaypoint.name);
    }
}
