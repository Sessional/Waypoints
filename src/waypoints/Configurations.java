/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package waypoints;

/**
 *
 * @author Andrew
 */
public class Configurations
{
    public boolean permissionsMod;
    public boolean permissionsBukkit;

    public Configurations()
    {
    }

    public void setPermissionsMod(boolean bool)
    {
        permissionsMod = bool;
    }

    public void setPermissionsBukkit(boolean bool)
    {
        permissionsBukkit = bool;
    }

    public boolean getPermissionsMod()
    {
        return permissionsMod;
    }

    public boolean getPermissionsBukkit()
    {
        return permissionsBukkit;
    }
}
