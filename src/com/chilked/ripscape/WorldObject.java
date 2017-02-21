package com.chilked.ripscape;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.awt.geom.Point2D;

public abstract class WorldObject {
	WorldObjectType worldObjectType;
	
	Point lowerCorner;	
	Hitbox hitbox;
	
	public static class WorldObjectType {
		static final String WORLD_OBJECT_YAML_DIRECTORY = "resources/types/";
		
		final private String name;
		char repChar;
		
		Point dimensions;
		
		final private String imageFilename;
		final private Image  image;
		
		public int getWidth()  {return dimensions.x;}
		public int getHeight() {return dimensions.y;}
		public char getChar()  {return repChar;}
		
		public String toString() { return name; }
		
		private Image loadImage(String imageFilename) {
			return null; //TODO: actually load an image
		}
		
		WorldObjectType(String name, char repChar, String imageFilename, Point dimensions) {
			this.name          = name;
			this.repChar       = repChar;
			this.imageFilename = imageFilename;
			this.image         = loadImage(imageFilename);
			this.dimensions    = dimensions;
			//TODO: how is the image field derived from these
		}
	}

	//consider each different object type as a different layer, requiring a different character array

	List<WorldObject> getNontouchingObjectsFromAscii(WorldObjectType objectType, char[][] letterArray) {
		List<WorldObject> worldObjects = new ArrayList<WorldObject>();

		//take care of the objects which aren't touching each other
		for(int j=0;j<letterArray.length;j++) {
			for(int i=0;i<letterArray[j].length;i++) {
				//because we go from left to right and down to up, we can just check rightwards and upwards
				if(letterArray[j][i]==objectType.getChar()) {
					//check if the character width matches the individual width or height
					boolean widthMatchesObject           = false;		
					boolean widthReachesSmallerDimension = false;
					boolean widthReachesLargerDimension  = false;
				
					for(int a=0;i+a<letterArray.length;a++) {
						if(letterArray[j][i+a]==objectType.getChar()) {
							if(a+1==objectType.getWidth() || a+1==objectType.getHeight()) {
								widthReachesSmallerDimension = true;
							}
					
							if(a+1==objectType.getWidth() && a+1==objectType.getHeight()) {
								widthReachesLargerDimension = true;
							}
						} else {
							if(a==objectType.getWidth() || a==objectType.getHeight()) {
								widthMatchesObject = true;
							}
						}
					}
				
					//check if the character height matches the individual width or height
					boolean heightMatchesObject           = false;			
					boolean heightReachesSmallerDimension = false;
					boolean heightReachesLargerDimension  = false;
				
					if(widthMatchesObject) {
						for(int b=0;j+b<letterArray.length;b++) {
							if(letterArray[j+b][i]==objectType.getChar()) {
								if(b+1==objectType.getWidth() || b+1==objectType.getHeight()) {
									heightReachesSmallerDimension = true;
								}
					
								if(b+1==objectType.getWidth() && b+1==objectType.getHeight()) {
									heightReachesLargerDimension = true;
								}
							} else {
								if(b==objectType.getWidth() || b==objectType.getHeight()) {
									heightMatchesObject = true;
								}
							}
						}
					}
				
					if(widthMatchesObject && heightMatchesObject) {
						int mapHeight;
						int mapWidth;
					
						if(widthReachesSmallerDimension && heightReachesLargerDimension) {
							mapHeight = Math.max(objectType.getWidth(),objectType.getHeight());
							mapWidth  = Math.min(objectType.getWidth(),objectType.getHeight());
						} else if(widthReachesLargerDimension && heightReachesSmallerDimension) {
							mapHeight = Math.max(objectType.getWidth(),objectType.getHeight());
							mapWidth  = Math.min(objectType.getWidth(),objectType.getHeight());
						}
					
						//TODO: actually generate the object
					
						for(int b=j;b<j+mapHeight;b++) {
							for(int a=i;a<i+mapWidth;a++) {
								letterArray[b][a] = ' '; //TODO: declare the 'empty char' somewhere else
							}
						}
					} 
				}
			}
		}
	}

	//build a new letterArray for each type of world object
	List<WorldObject> getAllWorldObjectsOfTypeFromAscii(WorldObjectType worldObjectType, char[][] letterArray) {
		//cut the letterArray down to just the letter for one world object type
		List<WorldObject> worldObjects = new ArrayList<WorldObject>();
		char[][] letters = new char[letterArray.length][letterArray[0].length];
		
		for(int j=0;j<letterArray.length;j++) {
			for(int i=0;i<letterArray[j].length;i++) {
				if(worldObjectType.getChar()==letterArray[j][i]) {
					letters[j][i] = letterArray[j][i];
				}
			}
		}
		
		worldObjects.addAll(getSquareObjectsFromAscii(worldObjectType,letters));
		worldObjects.addAll(getNontouchingObjectsFromAscii(worldObjectType,letters));
		worldObjects.addAll(getNonsquareTouchingObjectsFromAscii(worldObjectType,letters));
		
		return worldObjects;
	}

	List<WorldObject> getAllWorldObjectsFromAscii(char[][] letterArray) {
		List<WorldObject> allWorldObjects = new ArrayList<WorldObject>();

		for(WorldObjectType worldObjectType : WorldObjectType.getAllTypes()) {
			getAllWorlObjectsOfTypeFromAscii(worldObjectType,letterArray);
		}
		
		return allWorldObjects;
	}

	List<WorldObject> getSquareObjectsFromAscii(WorldObjectType objectType, char[][] letterArray) {
		List<WorldObject> worldObjects = new ArrayList<WorldObject>();

		//take care of any connected objects which are only 1x1 - no complex logic needed here
		if(objectType.getWidth()==objectType.getHeight()) {
			for(int j=0;j<letterArray.length;j++) {
				for(int i=0;i<letterArray[j].length;i++) {
					if(letterArray[j][i]==objectChar) {
						worldObjects.add(new WorldObject()); //TODO: fill out the constructor
						if(j+objectType.getWidth()<letterArray.length && i+objectType.getWidth()<letterArray[j].length) {
							for(int b=j;b<j+objectType.getWidth();b++) {
								for(int a=i;a<i+objectType.getWidth();a++) {
									letterArray[b][a] = emptyChar;
								}
							}
						} else {
							throw new IllegalStateException("Object boundaries would be out of bounds.");
						}
					}
				}
			}
		}
	}

	List<WorldObject> getNonsquareTouchingObjectsFromAscii(WorldObjectType objectType, char[][] letterArray) {
		//TODO: this placeholder will be kicking around until I get detecting touching objects working
		//at this time, just throw an exception if any of the remaining objects are touching (oddly-shaped objects only)
		
		List<WorldObject> worldObjects = new ArrayList<WorldObject>();
		
		//TODO: this will be a quick execution-ender in the final version -- if no remaining objects are detected, you're good to go my man
		for(int j=0;j<letterArray.length;j++) {
			for(int i=0;i<letterArray[j].length;i++) {
				if(letterArray[i][j]==objectType.getChar()) {
					throw new IllegalStateException("This method is a work in progress. There should not be any remaining object chars.");
				}
			}
		}
		/*
		List<Integer> widthwisePossibilities = new ArrayList<Integer>();
		List<Integer> heightwisePossibilities = new ArrayList<Integer>();
		
		for(int j=0;j<letterArray.length;j++) {
			//for a single line, collect the possible widthwise and heightwise possibilities
			for(int i=0;i<=characterWidth/objectWidth;i++) {
				int integerDivision = (characterWidth - i*objectWidth) / objectHeight;
				int remainder       = (characterWidth - i*objectWidth) % objectHeight;
		
				if(remainder==0) {
					widthwisePossibilities.add(i);                //widthwise
					heightwisePossibilities.add(integerDivision); //heightwise
				}
			}
		}
		*/
		
		return worldObjects;
	}
	
	WorldObject(WorldObjectType worldObjectType, Point lowerCorner) {
		this.lowerCorner = lowerCorner;
		this.hitbox      = new Hitbox(lowerCorner,worldObjectType.dimensions);
	}
}
