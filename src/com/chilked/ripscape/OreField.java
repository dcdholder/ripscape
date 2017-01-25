package com.chilked.ripscape;

import java.util.*;
import java.awt.*;

public class OreField extends WorldObject {
	final static String ORE_FIELD_JSON = "ore_field.json";
	private static final Map<String,OreFieldType> allOreFieldTypes = new HashMap<String,OreFieldType>();
	
	private final OreFieldType oreFieldType;

	private class OreFieldType extends WorldObjectType {
		ItemType oreType;
		
		private final float baseMineTime;
		private final float respawnTime;
		private final int   minimumMiningLevel;
		
		OreFieldType(String name, String imageFilename, String oreTypeName, float baseMineTime, float respawnTime, 
		             int minimumMiningLevel) {

			super(name, imageFilename);
			this.oreType            = Item.getItemTypeObject(oreTypeName);
			this.baseMineTime       = baseMineTime;
			this.respawnTime        = respawnTime;
			this.minimumMiningLevel = minimumMiningLevel;
		}
	}
	
	OreField(Point lowerCorner, Point dimensions, OreFieldType oreFieldType) {
		super(lowerCorner,dimensions);
		this.oreFieldType = oreFieldType;
	}
}
