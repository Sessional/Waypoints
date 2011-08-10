Supports multiverse multiple world teleports - DOES NOT SUPPORT PERMISSIONS YET!

NOTE: Parameters in [] are optional.

/waypoints add <name> [x] [y] [z]
/waypoints create <name> [x] [y] [z]

Usage: /waypoints add spawn
Notes: if no XYZ coords are given, it will create the waypoint on the block of your character

/waypoints list

Usage: /waypoints list
Notes: will list all(?) waypoints sorted alphabetically from A-Z

/waypoints save
/waypoints load

Usage: /waypoints save
Notes: will attempt to create or the load the files that are required for Waypoints - at the moment it does not save automatically, but it will load upon startup if files exist

/waypoints delete <name>
Usage: /waypoints delete spawn
Notes: removes the waypoint from the list of waypoints in game, must be saved afterwards for effects to take place upon server restarts

/waypoints go <name>
Usage: /waypoints go spawn
Notes: instantly moves character to this waypoint

/waypoints return
Usage: /waypoints return
Notes: will bring you back to the last position you were at before using /waypoints go, after return this position is removed and no longer functional until another go command is issued