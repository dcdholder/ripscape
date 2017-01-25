package com.chilked.ripscape;

import java.awt.*;

public abstract class WorldObject {
	Point lowerCorner;
	Point dimensions;
	
	WorldObject(Point lowerCorner, Point dimensions) {
		this.lowerCorner = lowerCorner;
		this.dimensions  = dimensions;
	}
}
