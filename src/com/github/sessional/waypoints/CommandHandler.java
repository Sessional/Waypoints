package com.github.sessional.waypoints;

import java.util.HashMap;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandHandler
{

    private WpsPlugin plugin;
    private HashMap<String, Location> returnPoints;

    /**
     * 
     * @param plugin the root WpsPlugin object
     */
    public CommandHandler(WpsPlugin plugin)
    {
        this.plugin = plugin;
        returnPoints = new HashMap<String, Location>();
    }

    /**
     * 
     * @return 
     */
    public WpsPlugin getPlugin()
    {
        return plugin;
    }

    /**
     * 
     * @param player
     * @param command
     * @param args
     * @return 
     */
    public boolean handleCommand(Player player, String command, String[] args)
    {
        if (command.equalsIgnoreCase("add") || command.equalsIgnoreCase("create"))
        {
            return doCreate(player, args);
        } else if (command.equalsIgnoreCase("delete") || command.equalsIgnoreCase("remove"))
        {
            return doDelete(player, args);
        } else if (command.equalsIgnoreCase("go"))
        {
            return doGo(player, args);
        } else if (command.equalsIgnoreCase("return"))
        {
            return doReturn(player, args);
        } else if (command.equalsIgnoreCase("list"))
        {
            return doList(player, args);
        } else if (command.equalsIgnoreCase("version"))
        {
            return showVersion(player, args);
        } else if (command.equalsIgnoreCase("save"))
        {
            return doSave(player, args);
        } else if (command.equalsIgnoreCase("load"))
        {
            return doLoad(player, args);
        }
        return false;
    }

    /**
     * 
     * @param player
     * @param args
     * @return 
     */
    public boolean showVersion(Player player, String[] args)
    {
        if (player == null)
        {
            getPlugin().getLogger().info("Waypoints version: " + getPlugin().getVersion());
        } else
        {
            player.sendMessage("Waypoints version: " + getPlugin().getVersion());
        }
        return true;
    }

    /**
     * 
     * @param player
     * @param args
     * @return 
     */
    public boolean doSave(Player player, String[] args)
    {
        if (player == null)
        {
            if (getPlugin().saveData())
            {
                getPlugin().getLogger().info("Waypoints have been saved.");
                return true;
            }
            return false;
        } else
        {
            if (getPlugin().saveData())
            {
                player.sendMessage("Waypoints have been saved.");
                return true;
            }
            return false;
        }
    }

    /**
     * 
     * @param player
     * @param args
     * @return 
     */
    public boolean doLoad(Player player, String[] args)
    {
        if (player == null)
        {
            if (getPlugin().loadData())
            {
                getPlugin().getLogger().info("Waypoints have been loaded.");
                return true;
            }
            return false;
        } else
        {
            if (getPlugin().loadData())
            {
                player.sendMessage("Waypoints have been loaded.");
                return true;
            }
            return false;
        }
    }

    /**
     * 
     * @param player
     * @param args
     * @return 
     */
    public boolean doGo(Player player, String[] args)
    {
        if (player == null)
        {
            getPlugin().getLogger().info("This command can not be executed from console.");
        }
        String wp = args[0];
        if (doesWaypointExist(wp))
        {
            if (returnPoints.containsKey(player.getName()))
            {
                returnPoints.remove(player.getName());
            }
            returnPoints.put(player.getName(), player.getLocation());

            Waypoint goTo = getWaypoint(wp);
            World world = getPlugin().getServer().getWorld(getWaypoint(wp).getWorld());
            
            int pX = (int)goTo.getX();
            int pZ = (int)goTo.getZ();
            Chunk chunk = world.getChunkAt(pX, pZ);
            if (!chunk.isLoaded())
                chunk.load();
            player.teleport(new Location(world, goTo.getX(), goTo.getY(), goTo.getZ()));
            player.sendMessage("Welcome to " + wp + "!");
            return true;
        } else
        {
            player.sendMessage("Waypoint '" + wp + "' does not exist!");
        }
        return false;
    }

    /**
     * 
     * @param waypointName
     * @return 
     */
    private int getWaypointIndex(String waypointName)
    {
        for (int i = 0; i < getPlugin().getWaypoints().size(); i++)
        {
            Waypoint wp = getPlugin().getWaypoints().get(i);
            if (wp.getName().equalsIgnoreCase(waypointName))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * 
     * @param waypointName
     * @return 
     */
    private Waypoint getWaypoint(String waypointName)
    {
        if (doesWaypointExist(waypointName))
        {
            return getPlugin().getWaypoints().get(getWaypointIndex(waypointName));
        }
        return null;
    }

    /**
     * 
     * @param waypointName
     * @return 
     */
    public boolean doesWaypointExist(String waypointName)
    {
        if (getWaypointIndex(waypointName) != -1)
        {
            return true;
        }
        return false;
    }

    /**
     * 
     * @param player
     * @param args
     * @return 
     */
    public boolean doCreate(Player player, String[] args)
    {
        String wp = args[0];
        String[] remainingArgs = null;

        if (args != null && args.length > 1)
        {
            remainingArgs = new String[args.length - 1];
            for (int i = 1; i < args.length; i++)
            {
                remainingArgs[i - 1] = args[i];
            }
        }

        if (doesWaypointExist(wp))
        {
            if (player == null)
            {
                getPlugin().getLogger().info("A waypoint name '" + wp + "' already exists.");
            } else
            {
                player.sendMessage("A waypoint with name '" + wp + "' already exists.");
            }
            return true;
        }
        if (!doesWaypointExist(wp))
        {
            if (remainingArgs == null)
            {
                if (player == null)
                {
                    getPlugin().getLogger().info("This command can not be called from console!");
                    return false;
                }
                double newY = player.getLocation().getY() + 1.0;
                Waypoint w = new Waypoint(player.getLocation().getX(),
                        newY, player.getLocation().getZ(),
                        wp, player.getWorld().getName());
                getPlugin().getWaypoints().add(getInsertIndex(wp), w);
                
                player.sendMessage("Waypoint '" + wp + "' created at ["
                        + player.getLocation().getX() + ","
                        + newY + ","
                        + player.getLocation().getZ() + "]");

                getPlugin().saveData();
                if (getPlugin().getConfig().getBoolean("dynmapSupport") == true)
                {
                    getPlugin().addToDynMap(w);
                }
                return true;
            } else
            {
                if (remainingArgs.length < 4)
                {
                    if (player == null)
                    {
                        getPlugin().getLogger().info("Please make sure to have the required arguments.");
                    } else
                    {
                        player.sendMessage("Please make sure to have the required arguments.");
                    }
                    return false;
                }
                
                Waypoint newWp = new Waypoint(Double.parseDouble(remainingArgs[1]), (Double.parseDouble(remainingArgs[2]) + 1), Double.parseDouble(remainingArgs[3]), wp, remainingArgs[0]);
                getPlugin().getWaypoints().add(getInsertIndex(wp), newWp);
                if (player == null)
                {
                    getPlugin().getLogger().info("Waypoint '" + wp + "'created at ["
                            +  remainingArgs[1] + "," + (remainingArgs[2] + 1)
                            + "," + remainingArgs[3] + "]");
                } else
                {
                    player.sendMessage("Waypoint '" + wp + "'created at ["
                            +  remainingArgs[1] + "," + (remainingArgs[2] + 1)
                            + "," + remainingArgs[3] + "]");
                }
                getPlugin().saveData();
                getPlugin().saveData();
                if (getPlugin().getConfig().getBoolean("dynmapSupport") == true)
                {
                    getPlugin().addToDynMap(newWp);
                }
                return true;
            }
        }


        return false;
    }

    /**
     * 
     * @param player
     * @param args
     * @return 
     */
    public boolean doDelete(Player player, String[] args)
    {
        String wp = args[0];
        if (doesWaypointExist(wp))
        {
            getPlugin().removeFromDynMap(getPlugin().getWaypoints().get(getWaypointIndex(wp)));
            getPlugin().getWaypoints().remove(getWaypointIndex(wp));
            if (player == null)
            {
                getPlugin().getLogger().info("Waypoint '" + wp + "' deleted.");
            } else
            {
                player.sendMessage("Waypoint '" + wp + "' deleted.");
            }
            getPlugin().saveData();
            return true;
        } else
        {
            if (player == null)
            {
                getPlugin().getLogger().info("Waypoint '" + wp + "' does not exist.");
            } else
            {
                player.sendMessage("Waypoint '" + wp + "' does not exist.");
            }
        }
        return false;
    }

    /**
     * 
     * @param player
     * @param args
     * @return 
     */
    public boolean doReturn(Player player, String[] args)
    {
        if (player == null)
        {
            getPlugin().getLogger().info("This command can not be executed from command line.");
        }
        if (returnPoints.containsKey(player.getName()))
        {
            player.teleport(returnPoints.get(player.getName()));
            returnPoints.remove(player.getName());
            return true;
        } else
        {
            player.sendMessage("You do not have a return point.");
            return true;
        }
    }

    /**
     * 
     * @param player
     * @param args
     * @return 
     */
    public boolean doList(Player player, String[] args)
    {
        String subCommand = null;
        if (args != null)
        {
            subCommand = args[0];
        }
        String[] remainingArgs = null;

        if (args != null && args.length > 1)
        {
            remainingArgs = new String[args.length - 1];
            for (int i = 1; i < args.length; i++)
            {
                remainingArgs[i - 1] = args[i];
            }
        }

        if (remainingArgs == null || args.length == 0)
        {
            return listAllWaypoints(player);
        } else
        {
            if (subCommand != null && subCommand.equalsIgnoreCase("world"))
            {
                return listWorldWaypoints(player, remainingArgs[0]);
            } else if (subCommand != null && subCommand.equalsIgnoreCase("page"))
            {
                return listPagedWaypoints(player, Integer.parseInt(remainingArgs[0]));
            }
        }

        return false;
    }

    /**
     * 
     * @param player
     * @return 
     */
    public boolean listAllWaypoints(Player player)
    {
        if (player == null)
        {
            for (Waypoint wp : getPlugin().getWaypoints())
            {
                getPlugin().getLogger().info(wp.getName() + "[" + wp.getWorld()
                        + "] [x,y,z] [" + (int) wp.getX() + "," + (int) wp.getY()
                        + "," + (int) wp.getZ() + "]");
            }
            return true;
        }
        for (Waypoint wp : getPlugin().getWaypoints())
        {
            player.sendMessage("§e" + wp.getName() + "§f [" + wp.getWorld()
                    + "] [x,y,z] [" + (int) wp.getX() + "," + (int) wp.getY()
                    + "," + (int) wp.getZ() + "]");
        }
        return true;
    }

    /**
     * 
     * @param player
     * @param pageNumber
     * @return 
     */
    public boolean listPagedWaypoints(Player player, int pageNumber)
    {
        int countPerPage = 9;
        int upperBound = pageNumber * countPerPage;
        int lowerBound = (pageNumber - 1) * 9;

        if (upperBound > getPlugin().getWaypoints().size())
        {
            if (player == null)
            {
                getPlugin().getLogger().info("Page " + pageNumber + ":");
                for (int i = lowerBound; i < getPlugin().getWaypoints().size(); i++)
                {
                    Waypoint wp = getPlugin().getWaypoints().get(i);
                    getPlugin().getLogger().info(wp.getName() + " [" + wp.getWorld()
                            + "] [x,y,z] [" + (int) wp.getX() + "," + (int) wp.getY()
                            + "," + (int) wp.getZ() + "]");
                }
                return true;
            }

            player.sendMessage("Page " + pageNumber + ":");
            for (int i = lowerBound; i < getPlugin().getWaypoints().size(); i++)
            {
                Waypoint wp = getPlugin().getWaypoints().get(i);
                player.sendMessage("§e" + wp.getName() + "§f [" + wp.getWorld()
                        + "] [x,y,z] [" + (int) wp.getX() + "," + (int) wp.getY()
                        + "," + (int) wp.getZ() + "]");
            }
        } else if (upperBound < getPlugin().getWaypoints().size())
        {
            if (player == null)
            {
                getPlugin().getLogger().info("Page " + pageNumber + ":");
                for (int i = lowerBound; i < upperBound; i++)
                {
                    Waypoint wp = getPlugin().getWaypoints().get(i);
                    getPlugin().getLogger().info(wp.getName() + " [" + wp.getWorld()
                            + "] [x,y,z] [" + (int) wp.getX() + "," + (int) wp.getY()
                            + "," + (int) wp.getZ() + "]");
                }
                return true;
            }

            player.sendMessage("Page " + pageNumber + ":");
            for (int i = lowerBound; i < upperBound; i++)
            {
                Waypoint wp = getPlugin().getWaypoints().get(i);
                player.sendMessage("§e" + wp.getName() + "§f [" + wp.getWorld()
                        + "] [x,y,z] [" + (int) wp.getX() + "," + (int) wp.getY()
                        + "," + (int) wp.getZ() + "]");
            }
        }
        return true;
    }

    /**
     * 
     * @param player
     * @param worldName
     * @return 
     */
    public boolean listWorldWaypoints(Player player, String worldName)
    {
        for (Waypoint wp : getPlugin().getWaypoints())
        {
            if (wp.getWorld().equalsIgnoreCase(worldName))
            {
                if (player == null)
                {
                    getPlugin().getLogger().info(wp.getName() + " [" + wp.getWorld()
                            + "] [x,y,z] [" + (int) wp.getX() + "," + (int) wp.getY()
                            + "," + (int) wp.getZ() + "]");
                } else
                {
                    player.sendMessage("§e" + wp.getName() + "§f [" + wp.getWorld()
                            + "] [x,y,z] [" + (int) wp.getX() + "," + (int) wp.getY()
                            + "," + (int) wp.getZ() + "]");
                }
            }
        }
        return true;
    }

    /**
     * 
     * @param wp
     * @return 
     */
    private int getInsertIndex(String wp)
    {
        if (getPlugin().getWaypoints().isEmpty())
        {
            return 0;
        }

        if (wp.compareToIgnoreCase(getPlugin().getWaypoints().get(getPlugin().getWaypoints().size() - 1).getName()) > 0)
        {
            return getPlugin().getWaypoints().size();
        }

        if (wp.compareToIgnoreCase(getPlugin().getWaypoints().get(0).getName()) < 0)
        {
            return 0;
        }

        int low = 0;
        int high = getPlugin().getWaypoints().size() - 1;
        int mid = (low + high) / 2;

        while (low < high)
        {
            if (wp.compareToIgnoreCase(getPlugin().getWaypoints().get(mid).getName()) < 0)
            {
                high = mid - 1;
            } else if (wp.compareToIgnoreCase(getPlugin().getWaypoints().get(mid).getName()) > 0)
            {
                low = mid + 1;
            }

            mid = (low + high) / 2;
        }

        return mid;
    }
}
