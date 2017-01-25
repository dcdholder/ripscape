package com.chilked.ripscape;

public class ItemType {
	final private String name;
	final private int baseValue;
	final private boolean stackable;
	
	public String toString() { return name; }
	
	void typeLoading(String JSONFilename, Map<ItemType> itemMap) {
		//TODO: implement this
	}
	
	ItemType(String name, boolean stackable) {
		this.name      = name;
		this.baseValue = baseValue;
		this.stackable = stackable;
	}
}
