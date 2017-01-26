package com.chilked.ripscape;

import java.util.*;

public class Item {
	protected final ItemType itemType;
	
	final static String MISC_ITEM_JSON = "misc_item.json";
	private static final Map<String,ItemType> allMiscItemTypes = new HashMap<String,ItemType>();
	
	int num;
	
	public ItemType getItemType() { return itemType; }
	
	static ItemType getItemTypeObject(String itemTypeName) { return null; } //TODO: fill this in
	
	Item(String itemTypeName) {
		this(getItemTypeObject(itemTypeName));
	}
	
	Item(ItemType itemType) { 
		this(itemType,1); 
	}
	
	Item(ItemType itemType, int num)  {
		this.itemType = itemType;
		this.num      = num; 
	}
}
