package com.chilked.ripscape;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
	
	private static class Room {
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
	
		static private class RoomBuilder {
			private static final char[] wallChars;
			private static final char[] floorChars;
			private static final char[] worldObjectChars;
			
			private static final char defWallChar;
			private static final char defFloorChar;
			
			static private String[] loadRoomText(String roomFilename) {
				Scanner      scanner = new Scanner(new File(roomFilename));
				List<String> lines   = new ArrayList<String>();
				while(scanner.hasNextLine()) {
					lines.add(scanner.nextLine());
				}

				String[] linesArr = lines.toArray(new String[0]);

				return preprocessRoomText(linesArr);
			}
			
			static String[] deepCopy(String[] inputStrings) {
				String[] stringsCopy = new String[inputStrings.length];

				for(int i=0; i<inputStrings.length; i++) {
					stringsCopy[i] = inputStrings[i];
				}
				
				return stringsCopy;
			}

			static String[] preprocessRoomText(String[] inputLines) {
				String[] lines;
				
				lines = replaceEmptyWithFloors(inputLines);
				lines = replaceEmptyWithWalls(lines);
				lines = truncateToWalls(lines);
				checkInvalidChars(lines);
				
				return lines;
			}

			static String[] replaceEmptyWithFloors(String[] inputLines) {
				String[] lines = deepCopy(inputLines);
					
				//replace spaces with floor characters
				//this will work as long as walls are spaced at least one character apart
				for(int j=0;j<lines.length;j++) {
					char[] lineChars = lines[j].toCharArray(); //does not include any newlines

					boolean fillOn         = false;
					boolean prevCharIsWall = false;
					for(int i=0; i<lineChars.length; i++) {
						if(charIsWall(lineChars[i]) && !prevCharIsWall()) {
							fillOn         = !fillOn; //toggles on reaching first wall char after non-wall chars
							prevCharIsWall = true;
						} else if(!charIsWall(lineChars[i])) {
							prevCharIsWall = false;
							if(charIsEmpty(lineChars[i]) && fillOn) { //only triggers when space is enclosed by wall chars
								lineChars[i] = defFloorChar;
							}
						}
					}
				
					//if there are empty spaces postfixed to a line, 
					lines[j] = new String(lineChars);
				}
			}

			static String[] replaceEmptyWithWalls(String[] inputLines) {
				String[] lines = deepCopy(inputLines);

				//now that we've set all enclosed floor tiles to the appropriate characters, replace all empty
				//chars with wall chars
				for(int j=0;j<lines.length;j++) {
					char[] lineChars = lines[j].toCharArray();
				
					for(int i=0; i<lineChars.length; i++) {
						if(charIsEmpty(lines[j].toCharArray())) {
							lineChars[i] = defWallChar;
						}
					}
				
					lines[j] = new String(lineChars);
				}
				
				return lines;
			}

			static String[] truncateToWalls(String[] inputLines) {
				String[] lines = deepCopy(inputLines);

				//truncate lines to the horizontal position of the furthest wall character in the file, recognize lines without walls
				boolean[] markForDeletion = new boolean[lines.length];
				int highestWallIndex=-1;
				for(int j=0;j<lines.length;j++) {
					char[] lineChars = lines[j].toCharArray();
				
					int highestWallIndexLine=-1;
					for(int i=0; i<lineChars.length; i++) {
						if(charIsWall()) {
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
					if(markForDeletion[i]) {
						deletedLinesCount++;
						for(int i=j;i<lines.length-1;i++) {
							lines[i] = lines[i+1];
						}
					}
				}

				lines = Arrays.asList(removeRange(lines.length-deletedLinesCount,lines.length-1)).toArray();
				
				return lines;
			}

			static void checkInvalidChars(String[] inputLines) {
				String[] lines = deepCopy(inputLines);

				//now check if there are any invalid characters (nonexistent WorldObjects, tileTypes, etc.)
				for(String line : lines) {
					for(char lineChar : line.toCharArray()) {
						if(!charIsWall() && !charIsFloor() && !charIsWorldObject()) {
							throw new IllegalStateException("Invalid Unicode room map character.");
						}
					}
				}
			}
		}
		
		private Portal[] loadPortals(String[] asciiMap) {
			return null; //TODO: make this actually do something
		}
		
		private WorldObject[] loadWorldObjects(String[] asciiMap) {
			return null; //TODO: make this actually do something
		}
		
		Room(String name, String roomFilename) {
			this.name          = name;
			this.roomFilename  = roomFilename;
			this.asciiMap      = RoomBuilder.loadRoomText(roomFilename);
			this.portals       = loadPortals(asciiMap);
			this.worldObjects  = loadWorldObjects(asciiMap);
			//TODO: how are the other fields derived from these
		}
	}
	
	Globe() {
		//TODO: load up all of the rooms
	}
}