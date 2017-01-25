package com.chilked.ripscape;

import java.util.*;

public abstract class Item {
	final static String MISC_ITEM_JSON = "misc_item.json";
	private static final Map<String,ItemType> allOreFieldTypes = new HashMap<String,ItemType>();
	
	int num;
	
	static ItemType getItemTypeObject(String itemTypeName) { return null; } //TODO: fill this in
	
	Item()        { this(1); }
	Item(int num) { this.num = num; }
}
