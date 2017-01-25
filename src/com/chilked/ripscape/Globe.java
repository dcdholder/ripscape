package com.chilked.ripscape;

import java.awt.*;

public class Globe {
	static String ROOM_ASCII_PATH;

	private Room[] rooms;
	
	static class Room {
		final String name;
	
		final String   asciiFilename;
		final String[] asciiMap;
	
		final Portal[]      portals;      //connections to other rooms
		final WorldObject[] worldObjects; //anvils, ore fields etc.
	
		static class Portal {
			Direction exitDirection;
		
			Point sourcePoint;
			Point destPoint;
	
			String destRoomName;
			
			Portal(Direction exitDirection, Point sourcePoint, String destRoomName, 
			       Point destPoint) {
			
				this.exitDirection = exitDirection;
				this.sourcePoint   = sourcePoint;
				this.destRoomName  = destRoomName;
				this.destPoint     = destPoint;
			}
		}
	
		private String[] loadAsciiMap(String asciiFilename) {
			return null; //TODO: make this actually do something
		}
		
		private Portal[] loadPortals(String[] asciiMap) {
			return null; //TODO: make this actually do something
		}
		
		private WorldObject[] loadWorldObjects(String[] asciiMap) {
			return null; //TOOD: make this acutally do something
		}
		
		Room(String name, String asciiFilename) {
			this.name          = name;
			this.asciiFilename = asciiFilename;
			this.asciiMap      = loadAsciiMap(asciiFilename);
			this.portals       = loadPortals(asciiMap);
			this.worldObjects  = loadWorldObjects(asciiMap);
			//TODO: how are the other fields derived from these
		}
	}
	
	Globe() {
		//TODO: load up all of the rooms
	}
}