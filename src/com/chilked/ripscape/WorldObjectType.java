package com.chilked.ripscape;

public class WorldObjectType {
	final private String name;

	final private String imageFilename;
	final private Image  image;
	
	public String toString() { return name; }
	
	void typeLoading(String JSONFilename, Map<WorldObjectType> worldObjectMap) {
		//TODO: implement this
	}
	
	WorldObjectType(String name, String imageFilename) {
		this.name          = name;
		this.imageFilename = imageFilename;
		//TODO: how are the other fields derived from these
	}
}
