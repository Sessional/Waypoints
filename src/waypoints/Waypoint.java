/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package waypoints;

import org.bukkit.Location;

/**
 *
 * @author Andrew
 */
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
