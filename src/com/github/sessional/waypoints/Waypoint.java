package com.github.sessional.waypoints;

import java.io.Serializable;
import org.bukkit.Location;

public class Waypoint implements Serializable, Comparable
{

    private double x;
    private double y;
    private double z;
    private String name;
    private String world;

    /**
     * 
     * @param location
     * @param name
     * @param worldName 
     */
    public Waypoint(Location location, String name, String worldName)
    {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.name = name;
        this.world = worldName;
    }

    /**
     * 
     * @param x
     * @param y
     * @param z
     * @param name
     * @param worldName 
     */
    public Waypoint(double x, double y, double z, String name, String worldName)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
        this.world = worldName;
    }

    /**
     * Sets the name of this way point
     * @param name the desired name of this way point
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets the world of this way point
     * @param worldName the world this way point occupies
     */
    public void setWorld(String worldName)
    {
        this.world = worldName;
    }

    /**
     * Sets the location of this way point
     * @param location the desired location for this way point
     */
    public void setLocation(Location location)
    {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    /**
     * 
     * @return 
     */
    public double getX()
    {
        return x;
    }

    /**
     * 
     * @return 
     */
    public double getY()
    {
        return y;
    }

    /**
     * 
     * @return 
     */
    public double getZ()
    {
        return z;
    }

    /**
     * Returns the name of a way point
     * @return the name of this way point
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the world of a way point
     * @return returns the world name of this way point
     */
    public String getWorld()
    {
        return world;
    }
    
    /**
     * 
     * @param o
     * @return 
     */
    @Override
    public int compareTo(Object o)
    {
        Waypoint otherWaypoint = (Waypoint)o;
        return getName().compareToIgnoreCase(otherWaypoint.getName());
    }
}
