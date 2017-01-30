package com.chilked.ripscape;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.*;

import org.yaml.snakeyaml.Yaml;

public class Globe {
	static String ROOM_ASCII_PATH;

	private Room[] rooms;
	
	public enum Direction { up, down, right, left; }
	
	public static class GlobalAddress {
		String roomName;
		Point  coord;
		
		GlobalAddress(String roomName, Point coord) {
			this.roomName = roomName;
			this.coord    = coord;
		}
	}
	
	public static class GlobalAddress2D {
		String  roomName;
		Point2D coord;
		
		String  getRoomName() { return roomName; }
		Point2D getCoord() { return coord; }
		
		boolean withinDistance(float radius, GlobalAddress2D otherAddress) {
			if(!roomName.equals(otherAddress.getRoomName())) {
				return false;
			} else {
				if(coord.distance(otherAddress.getCoord()) < radius) {
					return true;
				} else {
					return false;
				}
			}
		}
		
		GlobalAddress2D(String roomName, Point2D coord) {
			this.roomName = roomName;
			this.coord    = coord;
		}
	}
	
	public static class Room {
		final String name;
	
		final String   roomFilename;
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
		
		static class Tile {
			Point lowerCorner;
			
			static class TileType {
				final static String TILE_YAML = "tile.yaml";
				private static final Map<String,TileType> allTileTypes = new HashMap<String,TileType>();
				
				final private String  name;
				final private boolean passable;
				final private char    symbol;
				final private String  imageFilename;
				final private Image   image;
				
				private static void typeLoading() throws FileNotFoundException {
					Yaml yaml = new Yaml();
					FileInputStream stream = new FileInputStream(new File(TILE_YAML));
					
					@SuppressWarnings("unchecked")
					List<Map<String,Object>> rawList = (List<Map<String,Object>>)yaml.load(stream);
					
					for(Map<String,Object> fields : rawList) {
						String  name          = (String)fields.get("name");
						boolean passable      = (boolean)fields.get("passable");
						char    symbol        = (char)fields.get("symbol");
						String  imageFilename = (String)fields.get("imageFilename");
						
						TileType tileType = new TileType(name,passable,symbol,imageFilename);
						
						allTileTypes.put(name,tileType);
					}
				}
				
				static TileType getTileTypeObject(String tileTypeName) { return allTileTypes.get(tileTypeName); }
				
				public String toString() { return name; }
				
				private Image loadImage(String imageFilename) { return null; } //TODO: this should actually do something
				
				static {
					try {
						typeLoading();
					} catch(FileNotFoundException e) {
						throw new IllegalStateException("Invalid tile type filename.");
					}
				}
				
				TileType(String name, boolean passable, char symbol, String imageFilename) {
					this.name          = name;
					this.passable      = passable;
					this.symbol        = symbol;
					this.imageFilename = imageFilename;
					this.image         = loadImage(imageFilename);
				}
			}
		}
	
		static public class RoomBuilder {
			private static List<Character> wallChars;
			private static List<Character> floorChars;
			private static List<Character> worldObjectChars;
			
			private final String roomFilename;
			
			private final char defWallChar;
			private final char defFloorChar;
			
			public String[] loadRoomText() {
				try {
					Scanner scanner = new Scanner(new File(roomFilename));
					List<String> lines   = new ArrayList<String>();
					while(scanner.hasNextLine()) {
						lines.add(scanner.nextLine());
					}

					String[] linesArr = lines.toArray(new String[0]);

					scanner.close();
					
					return preprocessRoomText(linesArr);
				} catch(FileNotFoundException e) {
					e.printStackTrace();
					throw new IllegalArgumentException("Could not load room art.");
				}
			}
			
			private boolean charIsWall(char c)        { return wallChars.contains(c); }
			private boolean charIsFloor(char c)       { return floorChars.contains(c); }
			private boolean charIsWorldObject(char c) { return worldObjectChars.contains(c); }
			private boolean charIsEmpty(char c)       { return c==' '; }
			
			private String[] deepCopy(String[] inputStrings) {
				String[] stringsCopy = new String[inputStrings.length];

				for(int i=0; i<inputStrings.length; i++) {
					stringsCopy[i] = inputStrings[i];
				}
				
				return stringsCopy;
			}

			private String[] preprocessRoomText(String[] inputLines) {
				String[] lines;
				
				lines = replaceEmptyWithFloors(inputLines);
				lines = replaceEmptyWithWalls(lines);
				lines = truncateToWalls(lines);
				checkInvalidChars(lines);
				
				return lines;
			}

			private void checkPoint(char[][] charSquare, Set<Point> checkedPoints, Point point) {
				checkedPoints.add(point);

				if(!charIsWall(charSquare[point.x][point.y])) {
					Point upperPoint = new Point(point.x,point.y+1);
					Point lowerPoint = new Point(point.x,point.y-1);
					Point rightPoint = new Point(point.x+1,point.y);
					Point leftPoint  = new Point(point.x-1,point.y);
				
					charSquare[point.x][point.y] = defFloorChar;
					
					if(!checkedPoints.contains(upperPoint) && upperPoint.getY()<charSquare.length) {
						checkPoint(charSquare,checkedPoints,upperPoint);
					} 
					if(!checkedPoints.contains(lowerPoint) && lowerPoint.getY()>=0) {
						checkPoint(charSquare,checkedPoints,lowerPoint);
					}
					if(!checkedPoints.contains(rightPoint) && rightPoint.getX()<charSquare[rightPoint.y].length) {
						checkPoint(charSquare,checkedPoints,rightPoint);
					}
					if(!checkedPoints.contains(leftPoint) && leftPoint.getX()>=0) {
						checkPoint(charSquare,checkedPoints,leftPoint);
					}
				}
			}

			private String[] replaceEmptyWithFloors(String[] inputLines) {
				String[] lines = deepCopy(inputLines);
				
				//convert String array into double char array
				char[][] charSquare = new char[lines.length][];
				for(int j=0;j<lines.length;j++) {
					char[] lineChars = lines[j].toCharArray(); //does not include any newlines
					charSquare[j] = lineChars;
				}
				
				//find the first pair of non-consecutive wall chars on one line, then run the pixel fill algo
				boolean foundPoint = false;
				outerloop:
				for(int j=0; j<charSquare.length; j++) {
					char[]  charLine   = charSquare[j];
					boolean firstFound = false;
					for(int i=0; i<charLine.length; i++) {
						if(charIsWall(charLine[i]) && !firstFound) {
							firstFound = true;
						} else if(charIsWall(charLine[i]) && firstFound) {
							Point point = new Point(i,j);
							foundPoint  = true;
							checkPoint(charSquare, new HashSet<Point>(), point);
							
							break outerloop;
						}
					}
				}
				
				if(!foundPoint) {
					throw new IllegalStateException("Could not find a pair of wall characters for pixel fill.");
				}
				
				return lines;
			}

			private String[] replaceEmptyWithWalls(String[] inputLines) {
				String[] lines = deepCopy(inputLines);

				//now that we've set all enclosed floor tiles to the appropriate characters, replace all empty
				//chars with wall chars
				for(int j=0;j<lines.length;j++) {
					char[] lineChars = lines[j].toCharArray();
				
					for(int i=0; i<lineChars.length; i++) {
						if(charIsEmpty(lines[j].toCharArray()[i])) {
							lineChars[i] = defWallChar;
						}
					}
				
					lines[j] = new String(lineChars);
				}
				
				return lines;
			}

			private String[] truncateToWalls(String[] inputLines) {
				String[] lines = deepCopy(inputLines);

				//truncate lines to the horizontal position of the furthest wall character in the file, recognize lines without walls
				boolean[] markForDeletion = new boolean[lines.length];
				int highestWallIndex=-1;
				for(int j=0;j<lines.length;j++) {
					char[] lineChars = lines[j].toCharArray();
				
					int highestWallIndexLine=-1;
					for(int i=0; i<lineChars.length; i++) {
						if(charIsWall(lineChars[i])) {
							highestWallIndexLine = i;
						}
					}
				
					if(highestWallIndexLine>highestWallIndex) {
						highestWallIndex = highestWallIndexLine;
					}
				
					if(highestWallIndex>=0) {
						lines[j] = (new String(lineChars)).substring(0,highestWallIndex+1); //subString goes up to but does not include the last index
						markForDeletion[j] = false;
					} else {
						markForDeletion[j] = true;
					}
				}

				//delete lines without walls
				int deletedLinesCount = 0;
				for(int j=0;j<lines.length;j++) {
					if(markForDeletion[j]) {
						deletedLinesCount++;
						for(int i=j;i<lines.length-1;i++) {
							lines[i] = lines[i+1];
						}
					}
				}

				List<String> linesArrayList = new ArrayList<String>();
				linesArrayList.addAll(Arrays.asList(lines)); //removeRange only available to ArrayList
				linesArrayList.subList(lines.length-deletedLinesCount,lines.length-1).clear();
				
				return linesArrayList.toArray(lines);
			}

			private void checkInvalidChars(String[] inputLines) {
				String[] lines = deepCopy(inputLines);

				//now check if there are any invalid characters (nonexistent WorldObjects, tileTypes, etc.)
				for(String line : lines) {
					for(char lineChar : line.toCharArray()) {
						if(!charIsWall(lineChar) && !charIsFloor(lineChar) && !charIsWorldObject(lineChar)) {
							throw new IllegalStateException("Invalid Unicode room map character.");
						}
					}
				}
			}
			
			RoomBuilder(String roomFilename, char defWallChar, char defFloorChar) {
				this.roomFilename = roomFilename;
				this.defWallChar  = defWallChar;
				this.defFloorChar = defFloorChar;
			}
		}
		
		private Portal[] loadPortals(String[] asciiMap) {
			return null; //TODO: make this actually do something
		}
		
		private WorldObject[] loadWorldObjects(String[] asciiMap) {
			return null; //TODO: make this actually do something
		}
		
		Room(String name, String roomFilename) {
			RoomBuilder roomBuilder = new RoomBuilder(roomFilename,'w','f'); //TODO: load appropriate wall and floor chars
			
			this.name          = name;
			this.roomFilename  = roomFilename;
			this.asciiMap      = roomBuilder.loadRoomText();
			this.portals       = loadPortals(asciiMap);
			this.worldObjects  = loadWorldObjects(asciiMap);
			//TODO: how are the other fields derived from these
		}
	}
	
	Globe() {
		//TODO: load up all of the rooms
	}
}