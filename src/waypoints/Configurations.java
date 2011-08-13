package waypoints;

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
