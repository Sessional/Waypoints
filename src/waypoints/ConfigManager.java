/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package waypoints;

/**
 *
 * @author AKRUSE
 */
class ConfigManager {

    private WaypointPlugin plugin;
    public ConfigManager(WaypointPlugin plugin) {
        this.plugin = plugin;
    }
    
    public boolean permissionsEnabled()
    {
        return plugin.getConfig().getBoolean("EnablePermissions");
    }
    
    public boolean dynmapEnabled()
    {
        return plugin.getConfig().getBoolean("EnableDynmap");
    }
    
}
