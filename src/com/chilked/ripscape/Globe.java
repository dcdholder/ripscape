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
	
	public enum Direction { 
		up, down, right, left,
		upRight, upLeft, downRight, downLeft;
	
		public boolean isGridwise() {
			if(this==up || this==down) {
				return true;
			} else {
				return false;
			}
		}
		
		public boolean isDiagonal() {
			return !isGridwise();
		}
		
		public List<Direction> divideUpDiagonal() {
			List<Direction> directions = new ArrayList<Direction>();
			
			switch(this) {
				case upRight:
					directions.add(up);
					directions.add(right);
					break;
				case upLeft:
					directions.add(up);
					directions.add(left);
					break;
				case downRight:
					directions.add(down);
					directions.add(right);
					break;
				case downLeft:
					directions.add(down);
					directions.add(left);
					break;
				default:
					throw new IllegalArgumentException("This is not a diagonal direction.");
			}
			
			return directions;
		}
		
		public Point getPointFromCardinalDirection(Point pointA) {
			switch(this) {
				case up:
					return new Point(pointA.x,pointA.y+1);
				case down:
					return new Point(pointA.x,pointA.y-1);
				case left:
					return new Point(pointA.x+1,pointA.y);
				case right:
					return new Point(pointA.x-1,pointA.y);
				default:
					throw new IllegalArgumentException("This is not a cardinal direction.");
			}
		}
		
		static public Direction getDirectionFromAdjacentPoints(Point pointA, Point pointB) {
			Direction returnDirection = null;
			
			int xDiff = pointB.x-pointA.x;
			int yDiff = pointB.y-pointA.y;
			
			if(xDiff==0&&yDiff==0) {
				throw new IllegalArgumentException("Both points are the same.");
			}
			
			if(xDiff>1 || xDiff<-1 || yDiff>1 || yDiff<-1) {
				throw new IllegalArgumentException("Points are not adjacent.");
			}
			
			switch(xDiff) {
				case 0:
					switch(yDiff) {
						case 1:  return up;
						case -1: return down;
					}
				case 1:
					switch(yDiff) {
						case 0:  return left;
						case 1:  return upLeft;
						case -1: return downLeft;
					}
				case -1:
					switch(yDiff) {
						case 0:  return right;
						case 1:  return upRight;
						case -1: return downRight;
					}
			}
		
			return returnDirection;
		}
		
		static public List<Direction> getDirectionsFromPath(ArrayList<Point> points) {
			List<Direction> directions = new ArrayList<Direction>();
			
			for(int i=0;i<points.size()-1;i++) {
				directions.add(getDirectionFromAdjacentPoints(points.get(i), points.get(i+1)));
			}
			
			return directions;
		}
	}
	
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
	
		final TileMap tileMap;
		
		final String   roomFilename;
		final String[] asciiMap;
	
		final Portal[]      portals;      //connections to other rooms
		final WorldObject[] worldObjects; //anvils, ore fields etc.
		
		static class TileMap {
			Tile[][] map;
			
			static class Tile {
				TileType   tileType;
				List<Item> items; //dropped items
				
				static class TileType {
					final static String TILE_YAML = "tile.yaml";
					private static final Map<String,TileType> allTileTypes = new HashMap<String,TileType>();
					
					final private String  name;
					final private boolean passable;
					final private char    symbol;
					final private String  imageFilename;
					final private Image   image;
					
					boolean isPassable() { return passable; }
					
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
				
				TileType getTileType() { return tileType; }
				
				Tile(TileType tileType, List<Item> items) {
					this.tileType = tileType;
					this.items    = items;
				}
			}
				
			class TileNode {
				TileNode  parentNode;
				Point thisPoint;
				Point destPoint;

				double costFromSource;
				double furtherDistanceEstimate;
				
				double totalCost() {
					return costFromSource + furtherDistanceEstimate;
				}

				double beelineDistance(Point pointA, Point pointB) {
					if(pointA.equals(pointB)) {
						return 0.0;
					}
				
					if(pointA.x==pointB.x) {
						return Math.abs(pointB.x-pointA.x);
					} else if(pointA.y==pointB.y) {
						return Math.abs(pointB.y-pointA.y);
					} else {
						double diagonalDistance;
						double horizontalDistance;
				
						if(Math.abs(pointB.x-pointA.x)<Math.abs(pointB.y-pointA.y)) {
							diagonalDistance   = Math.abs(pointB.x-pointA.x)*Math.sqrt(2.0);
							horizontalDistance = Math.abs(pointB.y-pointA.y) - Math.abs(pointB.x-pointA.x);
						} else {
							diagonalDistance   = Math.abs(pointB.y-pointA.y)*Math.sqrt(2.0);
							horizontalDistance = Math.abs(pointB.x-pointA.x) - Math.abs(pointB.y-pointA.y);
						}
					
						return diagonalDistance + horizontalDistance;
					}
				}

				//note that diagonal movement is only an option when three squares are clear
				ArrayList<Point> getPathToSquare(Point initialPoint, Point finalPoint) {
					TileNode initialNode = new TileNode(null,initialPoint,finalPoint,0.0);
				
					return initialNode.getPathToSquareInternal(new ArrayList<TileNode>(),new ArrayList<TileNode>());
				}

				//run this from a node object
				ArrayList<Point> getPathToSquareInternal(List<TileNode> closedNodes, List<TileNode> openNodes) {
					List<Point> visitableAdjacentSquares = getVisitableAdjacentSquares(thisPoint);

					//add to the list of closed points, remove from list of open points
					closedNodes.add(this);
				
					if(openNodes.size()!=0) {
						openNodes.remove(this);
					}
				
					//if there are any visitable adjacent squares not in the open list, add them
					for(Point adjacentPoint : visitableAdjacentSquares) {
						boolean matchFound = false;
						for(TileNode openNode : openNodes) {
							if(openNode.thisPoint.equals(adjacentPoint)) {
								matchFound = true;
								break;
							}
						}
						if(!matchFound) {
							openNodes.add(new TileNode(this,adjacentPoint,this.destPoint,costFromSource+beelineDistance(thisPoint,adjacentPoint)));
						}
					}
				
					//if we were not able to add open nodes in this call, and the openNodes list is empty...
					if(openNodes.size()==0 && !thisPoint.equals(destPoint)) {
						throw new IllegalStateException("Could not find an open path to the destination");
					}
						
					//now search the open points list for the most promising lead, and take it
					TileNode cheapestNode = null;
					if(openNodes.size()!=0) {
						double   smallestCost   = 10000.0;
						boolean  firstIteration = true;
						for(TileNode openNode : openNodes) {
							if(openNode.totalCost()<smallestCost || firstIteration) {
								cheapestNode = openNode;
								smallestCost = openNode.totalCost();
							}
							firstIteration = false;
						}
					}
					
					
					
					if(thisPoint.equals(destPoint) && (cheapestNode.totalCost()>=this.totalCost() || cheapestNode==null)) {
						ArrayList<Point> path = new ArrayList<Point>();
						//reverse through parent nodes until you reach the node with a null parent
						TileNode checkNode = this;
						while(checkNode.parentNode!=null) {
							path.add(checkNode.thisPoint);
							checkNode = checkNode.parentNode;
						}
						path.add(checkNode.thisPoint);
					
						return path;
					} else {
						//one recursion for every checked node - possiblity for stack overflow?
						return cheapestNode.getPathToSquareInternal(closedNodes,openNodes);
					}
				}
				
				/*void moveInDirection(Direction direction) {
					moving = true;
				} */

				/*
				void moveToSquare(Point finalSquare) {
					List<Point>     path       = getPathToSquare(this.location,finalPoint);
					List<Direction> directions = Direction.getDirectionsFromPath(path);
				
					for(int i=0; i<directions.size(); i++) {
						moveInDirection(directions.get(i));
					}
				}
				*/

				boolean singleSquareMovementPossible(Point pointA, Point pointB) {
					List<Point> passThroughPoints = getPassThroughPoints(pointA, pointB);
				
					boolean passable = true;
					for(Point passThroughPoint : passThroughPoints) {
						if(!passable(passThroughPoint)) {
							passable = false;
						}
					}
				
					return passable;
				}

				List<Point> getVisitableAdjacentSquares(Point pointA) {
					List<Point> adjacentSquares  = getAdjacentSquares(pointA);
					List<Point> visitableSquares = new ArrayList<Point>();
				
					for(Point adjacentSquare : adjacentSquares) {
						if(singleSquareMovementPossible(pointA,adjacentSquare)) {
							visitableSquares.add(adjacentSquare);
						}
					}
				
					return visitableSquares;
				}

				List<Point> getAdjacentSquares(Point pointA) {
					List<Point> adjacentSquares = new ArrayList<Point>();
				
					adjacentSquares.add(new Point(pointA.x+1,pointA.y));
					adjacentSquares.add(new Point(pointA.x-1,pointA.y));
					adjacentSquares.add(new Point(pointA.x,pointA.y+1));
					adjacentSquares.add(new Point(pointA.x,pointA.y-1));
				
					return adjacentSquares;
				}

				List<Point> getPassThroughPoints(Point pointA, Point pointB) {
					List<Point> passThroughPoints = new ArrayList<Point>();
				
					Direction direction = Direction.getDirectionFromAdjacentPoints(pointA,pointB);
				
					if(direction.isGridwise()) {
						passThroughPoints.add(pointB);
					} else if(direction.isDiagonal()) {
						List<Direction> directions = direction.divideUpDiagonal();
						
						for(Direction cardinalDirection : directions) {
							passThroughPoints.add(cardinalDirection.getPointFromCardinalDirection(pointA));
						}
					}
					
					return passThroughPoints;
				}

				double pathLength(ArrayList<Point> path) {
					List<Direction> directions = Direction.getDirectionsFromPath(path);
					
					double total = 0.0;
					for(Direction direction : directions) {
						if(direction.isGridwise()) {
							total+=1.0;
						} else if(direction.isDiagonal()) {
							total+=Math.sqrt(2.0);
						}
					}
					
					return total;
				}
				
				List<Point> getPathToPerimeter(Hitbox hitbox, Point initialPoint) {
					boolean     foundSmallerPath   = false;
					List<Point> smallestPath       = null;
					double      smallestPathLength = 10000.0; //this is the cutoff - paths must be shorter than this to be found

					for(Point point : hitbox.getPerimeter()) {
						ArrayList<Point> path       = getPathToSquare(initialPoint,point);
						double           pathLength = pathLength(path);
					
						if(pathLength<smallestPathLength) {
							foundSmallerPath   = true;
							smallestPath       = path;
							smallestPathLength = pathLength;
						}
					}
				
					if(!foundSmallerPath || smallestPath==null) {
						throw new IllegalStateException("No path found.");
					}
					
					return smallestPath;
				}

				TileNode(TileNode parentNode, Point thisPoint, Point destPoint, double costFromSource) {
					this.thisPoint               = thisPoint;
					this.destPoint               = destPoint;
					this.costFromSource          = costFromSource;
					this.furtherDistanceEstimate = beelineDistance(thisPoint,destPoint);
				}
			}
			
			boolean passable(Point point) {
				return map[point.y][point.y].getTileType().isPassable();
			}
			
			TileMap(Tile[][] map) {
				this.map = map;
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
		
		private TileMap loadTileMap(String[] asciiMap) {
			return null; //TODO: make this actually do something
		}
		
		private Portal[] loadPortals(String[] asciiMap) {
			return null; //TODO: make this actually do something
		}
		
		private WorldObject[] loadWorldObjects(String[] asciiMap) {
			return null; //TODO: make this actually do something
		}
		
		Room(String name, String roomFilename) {
			RoomBuilder roomBuilder = new RoomBuilder(roomFilename,'w','f'); //TODO: load appropriate wall and floor chars
			
			//TODO: separate Room and RoomBuilder code better
			this.name          = name;
			this.roomFilename  = roomFilename;
			this.asciiMap      = roomBuilder.loadRoomText();
			this.tileMap       = loadTileMap(asciiMap);
			this.portals       = loadPortals(asciiMap);
			this.worldObjects  = loadWorldObjects(asciiMap);
			//TODO: how are the other fields derived from these
		}
	}
	
	private static void typeLoading() throws FileNotFoundException {
		Yaml yaml = new Yaml();
		FileInputStream stream = new FileInputStream(new File(WORLD_OBJECT_YAML_DIRECTORY+ORE_FIELD_YAML));
		
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> rawList = (List<Map<String,Object>>)yaml.load(stream);
		
		for(Map<String,Object> fields : rawList) {
			String inputRoomName     = (String)fields.get("name");
			String inputRoomFilename = (String)fields.get("roomFilename");
			String defFloor          = (String)fields.get("defFloor");
			String defWall           = (String)fields.get("defWall");
			
			//construct room, add room to rooms list
		}
	}
	
	Globe() {
		//TODO: load up all of the rooms
	}
}