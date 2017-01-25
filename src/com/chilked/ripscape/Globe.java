package com.chilked.ripscape;

public class Globe {
	static String ROOM_ASCII_PATH;

	private Room[] rooms;
	
	class Room {
		final String name;
	
		final String   asciiFilename;
		final String[] asciiMap;
	
		final Portal[]      portals;      //connections to other rooms
		final WorldObject[] worldObjects; //anvils, ore fields etc.
	
		class Portal {
			Direction exitDirection;
		
			Point sourcePoint;
			Point destPoint;
	
			String destRoomName;
			
			Portal(Direction exitDirection, Point sourcePoint, String destRoomName, 
			       Point destPoint) {
			
				this.exitDirection = exitDirection;
				this.sourcePoint   = sourcePoint;
				this.destRoomName  = destPoint;	
			}
		}
	
		Room(String name, String asciiFilename) {
			this.name          = name;
			this.asciiFilename = asciiFilename;
			//TODO: how are the other fields derived from these
		}
	}
	
	Globe() {
		//TODO: load up all of the rooms
	}
}