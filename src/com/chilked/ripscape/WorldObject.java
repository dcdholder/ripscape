package com.chilked.ripscape;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class WorldObject {
	WorldObjectType worldObjectType;
	
	Point lowerCorner;	
	Hitbox hitbox;
	
	public static class WorldObjectType {
		static final String WORLD_OBJECT_YAML_DIRECTORY = "resources/types/";
		
		final private String name;

		Point dimensions;
		
		final private String imageFilename;
		final private Image  image;
		
		public String toString() { return name; }
		
		private Image loadImage(String imageFilename) {
			return null; //TODO: actually load an image
		}
		
		WorldObjectType(String name, String imageFilename, Point dimensions) {
			this.name          = name;
			this.imageFilename = imageFilename;
			this.image         = loadImage(imageFilename);
			this.dimensions    = dimensions;
			//TODO: how is the image field derived from these
		}
	}
	
	WorldObject(WorldObjectType worldObjectType, Point lowerCorner) {
		this.lowerCorner = lowerCorner;
		this.hitbox      = new Hitbox(lowerCorner,worldObjectType.dimensions);
	}
}
