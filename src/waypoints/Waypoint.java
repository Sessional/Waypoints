/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package waypoints;

import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author AKRUSE
 */
public class Waypoint implements Comparable {

    private String name;
    private String worldName;
    private float xCoord;
    private float yCoord;
    private float zCoord;
    private Location loc;
    private WaypointPlugin plugin;

    /**
     * Represents a space in a world that can be teleported to.
     *
     * @param plugin the parent WaypointPlugin class
     * @param name the name the Waypoint should use
     * @param worldName the world the Waypoint resides in
     * @param xCoord the x coordinate the waypoint is at
     * @param yCoord the y coordinate the waypoint is at
     * @param zCoord the z coordinate the waypoint is at
     */
    public Waypoint(WaypointPlugin plugin, String name, String worldName, float xCoord, float yCoord, float zCoord) {
        this.plugin = plugin;
        this.name = name;
        this.worldName = worldName;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
    }
    
    /**
     * Represents a space in a world that can be teleported to.
     *
     * @param plugin The parent WaypointPlugin class
     */
    public Waypoint(WaypointPlugin plugin, String name, Location location)
    {
        this.plugin = plugin;
        this.name = name;
        this.worldName = location.getWorld().getName();
        this.xCoord = (float) location.getX();
        this.yCoord = (float) location.getY();
        this.zCoord = (float) location.getZ();
    }
    
    /**
     * Represents a space in a world that can be teleported to.
     *
     * @param plugin The parent WaypointPlugin class
     */
    public Waypoint(WaypointPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    /**
     * A method to handle the gathering of Waypoint information.
     * Upon first access it creates the Bukkit location for this Waypoint.
     *
     * @return The location that this Waypoint represents
     */
    public Location getLocation() {
        if (loc == null) {
            loc = new Location(getWorld(), getX(), getY(), getZ());
        }
        return loc;
    }
    
    /**
     *
     * @return The name of this Waypoint.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return The name of the world this Waypoint resides in.
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     *
     * @return The world this Waypoint resides in.
     */
    public World getWorld() {
        return plugin.getServer().getWorld(worldName);
    }

    /**
     *
     * @return The x coordinate this Waypoint is located at.
     */
    public float getX() {
        return xCoord;
    }

    /**
     *
     * @return The y coordinate this Waypoint is located at.
     */
    public float getY() {
        return yCoord;
    }

    /**
     *
     * @return The z coordinate this Waypoint is located at.
     */
    public float getZ() {
        return zCoord;
    }

    /**
     * Helper method to allow the updating of the Waypoint.
     * Sets the location to be regenerated upon next access.
     *
     * @param worldName the name of the world that this Waypoint exists on.
     * @param xCoord the x coordinate that this Waypoint should reside at.
     * @param yCoord the y coordinate that this Waypoint should reside at.
     * @param zCoord the z coordinate that this Waypoint should reside at.
     */
    public void setLocation(String worldName, float xCoord, float yCoord,
            float zCoord) {
        loc = null;
        this.worldName = worldName;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
    }

    /**
     * Short method to change a single parameter of the Waypoint.
     * Uses setLocation to force a regeneration of the location next access.
     *
     * @param xCoord The new x coordinate to locate this Waypoint at.
     */
    public void setX(float xCoord) {
        setLocation(worldName, xCoord, yCoord, zCoord);
    }

    /**
     * Short method to change a single parameter of the Waypoint.
     * Uses setLocation to force a regeneration of the location next access.
     *
     * @param yCoord The new y coordinate to locate this Waypoint at.
     */
    public void setY(float yCoord) {
        setLocation(worldName, xCoord, yCoord, zCoord);
    }

    /**
     * Short method to change a single parameter of the Waypoint.
     * Uses setLocation to force a regeneration of the location next access.
     *
     * @param zCoord The new z coordinate to locate this Waypoint at.
     */
    public void setZ(float zCoord) {
        setLocation(worldName, xCoord, yCoord, zCoord);
    }

    /**
     * Short method to change a single parameter of the Waypoint.
     * Uses setLocation to force a regeneration of the location next access.
     *
     * @param worldName The new world to locate this Waypoint on.
     */
    public void setWorld(String worldName) {
        setLocation(worldName, xCoord, yCoord, zCoord);
    }

    /**
     * Changes the name of the Waypoint.
     *
     * @param name The new name for the Waypoint to be referenced by.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Formats a string that can be written to a file to save this Waypoint.
     *
     * @return The formatted string that will save this Waypoint.
     */
    public String toSaveData() {
        StringBuilder saveForm = new StringBuilder();
        saveForm.append(getName());
        saveForm.append(":");
        saveForm.append(getWorldName());
        saveForm.append("@");
        saveForm.append(getX());
        saveForm.append(",");
        saveForm.append(getY());
        saveForm.append(",");
        saveForm.append(getZ());
        return saveForm.toString();
    }

    /**
     * Loads the Waypoint data from a string.
     *
     * @param data A string of Waypoint data formatted in name:world@x,y,z
     * format.
     */
    public void fromSaveData(String data) {
        String[] elements = data.split(":");
        String[] waypointData = elements[1].split("@");
        String[] coordinates = waypointData[1].split(",");
        setName(elements[0]);
        setWorld(waypointData[0]);
        try {
            setX(Float.parseFloat(coordinates[0]));
            setY(Float.parseFloat(coordinates[1]));
            setZ(Float.parseFloat(coordinates[2]));
        } catch (NumberFormatException e) {
            plugin.executeSevereError("Failure to load waypoint coordinates "
                    + "for Waypoint " + getName() + "\nError code: 1");
        }
    }

    /**
     * 
     * @param o the other waypoint to compare to
     * @return -1 if less than, 0 if equal, +1 if greater than
     */
    @Override
    public int compareTo(Object o)
    {
        Waypoint otherWaypoint = (Waypoint) o;
        return getName().compareToIgnoreCase(otherWaypoint.getName());
    }
}
