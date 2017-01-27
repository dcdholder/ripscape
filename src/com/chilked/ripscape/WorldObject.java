package com.chilked.ripscape;

import java.awt.*;

public abstract class WorldObject {
	Point lowerCorner;
	Point dimensions;
	
	public static class WorldObjectType {
		final private String name;

		final private String imageFilename;
		final private Image  image;
		
		public String toString() { return name; }
		
		private Image loadImage(String imageFilename) {
			return null; //TODO: actually load an image
		}
		
		WorldObjectType(String name, String imageFilename) {
			this.name          = name;
			this.imageFilename = imageFilename;
			this.image         = loadImage(imageFilename);
			//TODO: how is the image field derived from these
		}
	}
	
	WorldObject(Point lowerCorner, Point dimensions) {
		this.lowerCorner = lowerCorner;
		this.dimensions  = dimensions;
	}
}
