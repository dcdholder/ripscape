package com.chilked.ripscape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import org.yaml.snakeyaml.Yaml;

public class Item {
	protected final ItemType itemType;
	
	final static String MISC_ITEM_YAML = "misc_item.yaml";
	private static final Map<String,ItemType> allMiscItemTypes = new HashMap<String,ItemType>();
	
	int num;
	
	public static class ItemType {
		final private String  name;
		final private int     baseValue;
		final private boolean stackable;
		
		private static void typeLoading() throws FileNotFoundException {
			Yaml yaml = new Yaml();
			FileInputStream stream = new FileInputStream(new File(MISC_ITEM_YAML));
			
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> rawList = (List<Map<String,Object>>)yaml.load(stream);
			
			for(Map<String,Object> fields : rawList) {
				String  name       = (String)fields.get("name");
				int     baseValue  = (int)fields.get("baseValue");
				boolean stackable  = (boolean)fields.get("stackable");
				
				ItemType itemType = new ItemType(name,baseValue,stackable);
				
				allMiscItemTypes.put(name,itemType);
			}
		}
		
		static ItemType getItemTypeObject(String itemTypeName) { return allMiscItemTypes.get(itemTypeName); }
		
		public String toString() { return name; }
		
		static {
			try {
				typeLoading();
			} catch(FileNotFoundException e) {
				throw new IllegalStateException("Invalid item type filename.");
			}
		}
		
		ItemType(String name, int baseValue, boolean stackable) {
			this.name      = name;
			this.baseValue = baseValue;
			this.stackable = stackable;
		}
	}
	
	ItemType getItemType() { return itemType; }
	
	Item(String itemTypeName) {
		this(ItemType.getItemTypeObject(itemTypeName));
	}
	
	Item(ItemType itemType) { 
		this(itemType,1); 
	}
	
	Item(ItemType itemType, int num)  {
		this.itemType = itemType;
		this.num      = num; 
	}
}
