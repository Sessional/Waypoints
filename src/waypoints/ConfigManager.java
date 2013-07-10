package waypoints;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.InvalidConfigurationException;

class ConfigManager {

    private WaypointPlugin plugin;
    
    private File configFile;
    
    public ConfigManager(WaypointPlugin plugin) {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder().getPath()
                + File.separatorChar + "config.yml");
    }
    
    public boolean permissionsEnabled()
    {
        return plugin.getConfig().getBoolean("EnablePermissions");
    }
    
    public boolean dynmapEnabled()
    {
        return plugin.getConfig().getBoolean("EnableDynmap");
    }

    void handleConfigGeneration() {
        if (configFile.exists())
        {
            try {
                plugin.getConfig().load(configFile);
                if (plugin.getConfig().isSet("bukkitPermissions"))
                {
                    boolean permissionsEnabled = plugin.getConfig().getBoolean("bukkitPermissions");
                    boolean dynmapEnabled = plugin.getConfig().getBoolean("dynmapSupport");
                    configFile.delete();
                    plugin.getConfig().set("EnablePermissions", permissionsEnabled);
                    plugin.getConfig().set("EnableDynmap", dynmapEnabled);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(configFile));
                    bw.write("EnablePermissions: " + permissionsEnabled + "\n");
                    bw.write("EnableDynmap:" + dynmapEnabled + "\n");
                    bw.close();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            plugin.saveDefaultConfig();
        }
    }
    
}
