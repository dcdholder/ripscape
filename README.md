#Ripscape

##General Idea

Goal is to build a playable demo of a top-down game with mouse-oriented controls reminiscent of Runescape.

###Demo

The initial demo will be of a single small room with mineable ore fields, an anvil, and a forge. Player will be able to mine and smelt ores, retrieve items from containers, smith a small range of weapons and armour, and equip said weapons and armour.

##Progress

Very early development. Cursory testing has been done. 

##Technical Details

###Type Objects to Describe Item/World Object types

This project makes heavy use of the Type Object pattern, since it allows for individual items (weapons, armour, interactable world objects etc.) to be specced out outside of the source. Each possible Item/World Object type is retrieved from one configuration file or another, and then added to a pool of types, then referenced by the concrete manifestations of that type (a weapon, piece of armour etc.) This also allows us to reference object type properties in a quasi-static way, since only one Type Object for each type is instantiated.

###Room Generation

Rooms are generated from ASCII art. Different floor and wall tiles are represented using different characters, as are different world objects. Numbers are used to indicate "portals" into other rooms, which are mapped to each other in a separate configuration file. This configuration file also contains room names and default styles (floor/wall tile fill, etc.) The room ASCII art is pre-processed to simplify the room designer's life.

###Movement

An implementation of A* is used to pathfind on mouse click. Movement is mapped to a grid. The system permits horizontal and right-angle diagonal movement between adjacent squares.
