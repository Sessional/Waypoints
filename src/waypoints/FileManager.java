/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package waypoints;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.List;

/**
 *
 * @author AKRUSE
 */
public class FileManager {
    
    private WaypointPlugin plugin;
    
    private File datFile;
    
    private File wpsFile;
    
    /**
     * A class to encapsulate the methods used to read the files
     * @param plugin the parent Waypoint plugin that houses this object
     */
    public FileManager(WaypointPlugin plugin)
    {
        this.plugin = plugin;
        
        datFile = new File("./plugins/Waypoints/waypoints.dat");
        wpsFile = new File(plugin.getDataFolder().getPath()
                + File.separatorChar + "waypoints.wps");
    }
    
    /**
     * Handles loading the Waypoints based upon which file exists.
     */
    public void loadWaypoints()
    {
        if (wpsFile.exists())
        {
            loadWpsFile();
        }
        else
        {
            if (datFile.exists())
            {
                loadDatFile();
                saveWpsFile();
            }
        }
    }
    
    /**
     * Load the old binary form of the file.
     */
    public void loadDatFile()
    {
        try {
            FileInputStream fsIn = new FileInputStream(datFile);
            BufferedInputStream bIn = new BufferedInputStream(fsIn);
            ObjectInputStream oIn = new ObjectInputStream(bIn);
            List<com.github.sessional.waypoints.Waypoint> waypointData =
                    (List<com.github.sessional.waypoints.Waypoint>) oIn.readObject();
            oIn.close();
            for (com.github.sessional.waypoints.Waypoint w : waypointData) {
                plugin.getWaypointStorage().put(w.getName(), new Waypoint(plugin, w.getName(),
                        w.getWorld(), (float) w.getX(), (float) w.getY(), (float) w.getZ()));
            }
        } catch (Exception ex) {
            plugin.executeSevereError("Failure to load old waypoint data\nError code: 2");
        }
    }
    
    

    /**
     * Save the text version of the file - easier to modify if anything goes wrong
     */
    public void saveWpsFile() {
        try {
            if (!wpsFile.exists()) {
                wpsFile.createNewFile();
            }
            FileWriter fw = new FileWriter(wpsFile);
            BufferedWriter bw = new BufferedWriter(fw);

            for (Waypoint w : plugin.getWaypointStorage().values()) {
                bw.write(w.toSaveData() + "\n");
            }
            bw.close();
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Load the text based save file
     */
    public void loadWpsFile() {
        try {
            FileReader fr = new FileReader(wpsFile);
            BufferedReader br = new BufferedReader(fr);

            while (br.ready()) {
                String line = br.readLine();
                if (line.equals("\n"))
                    continue;
                Waypoint wp = new Waypoint(plugin);
                wp.fromSaveData(line);
                plugin.getWaypointStorage().put(wp.getName(), wp);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
