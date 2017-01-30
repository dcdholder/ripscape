package com.chilked.ripscape;

import java.awt.Point;

public class Container extends WorldObject {
	ContainerType containerType;
	
	Inventory inventory;
	
	public static class ContainerType extends WorldObjectType {
		boolean stackable;
		int     capacity;
		
		ContainerType(String name, String imageFilename, Point dimensions, boolean stackable, int capacity) {
			super(name,imageFilename,dimensions);
			this.stackable = stackable;
			this.capacity  = capacity;
		}
	}
	
	Container(ContainerType containerType, Point lowerCorner) {
		super(containerType,lowerCorner);
		this.inventory = new Inventory(containerType.capacity);
	}
}
