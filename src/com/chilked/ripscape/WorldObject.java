package com.chilked.ripscape;

public abstract class WorldObject {
	Point lowerCorner;
	Point dimensions;
	
	WorldObject(String name, Point lowerCorner, Point dimensions) {
		this.lowerCorner = lowerCorner;
		this.dimensions  = dimensions;
	}
}
