package com.chilked.ripscape;

public class ItemType {
	final private String  name;
	final private int     baseValue;
	final private boolean stackable;
	
	public String toString() { return name; }
	
	ItemType(String name, int baseValue, boolean stackable) {
		this.name      = name;
		this.baseValue = baseValue;
		this.stackable = stackable;
	}
}
