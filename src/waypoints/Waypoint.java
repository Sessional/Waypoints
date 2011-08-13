package waypoints;

import org.bukkit.Location;

public class Waypoint {
    Waypoint(String name, Location location)
    {
        this.location = location;
        this.name = name;
    }
    public Location location;
    public String name;

    public String toString()
    {
        return name;
    }
}
