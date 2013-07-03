Supports multiverse multiple world teleports

Commands:
Create
Parameters in <> are required.
Parameters in [] are optional.
A parameter split with a | is an alias, either one can be used (but only one at a time)
/wps <add|create> <name> [world] [x] [y] [z]
/waypoints <add|create> <name> [world] [x] [y] [z]
Permissions:
Any of the following permissions will allow the use of this command
	waypoints.add
	waypoints.create
Examples:
	/wps add home
		Creates a waypoint called home at current location
        /wps create home homeWorld 0 0 0
		Creates a waypoint called home at x:0 y:0 z:0

Delete
Parameters in <> are required.
Parameters in [] are optional.
A parameter split with a | is an alias, either one can be used (but only one at a time)
/wps <delete|remove> <name>
/waypoints <delete|remove> <name>
Permissions:
Any of the following permissions will allow the use of this command
	waypoints.delete
	waypoints.remove
Examples:
	/wps delete home
		Deletes the waypoint called home
	/wps remove home
		Deletes the waypoint called home

Go
Parameters in <> are required.
Parameters in [] are optional.
A parameter split with a | is an alias, either one can be used (but only one at a time)

/wps go <name>
/waypoints go <name>
Permissions:
	waypoints.go
Examples:
	/wps go home
		Teleports the player to the waypoint called home

List
Parameters in <> are required.
Parameters in [] are optional.
A parameter split with a | is an alias, either one can be used (but only one at a time)
/wps list [world <world name> |page <#>]
/waypoints list [world <world name> |page <#>]
Permissions:
	waypoints.list
Examples:
	/wps list
		Lists all waypoints that exist
	/wps list world homeworld
		Lists all waypoints that exist on the world homeworld
	/wps list page 2
		Lists waypoints in a page format starting on page two

Return
Parameters in <> are required.
Parameters in [] are optional.
A parameter split with a | is an alias, either one can be used (but only one at a time)

/wps return
/waypoints return
Permissions:
	waypoints.return
Examples:
	/wps return
		Teleports the player to location before the last go command