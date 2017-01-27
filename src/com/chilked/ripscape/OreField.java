package com.chilked.ripscape;

import java.util.*;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import com.chilked.ripscape.Item.ItemType;

import java.awt.*;
import java.io.*;

public class OreField extends WorldObject {
	private final OreFieldType oreFieldType;

	public static class OreFieldType extends WorldObjectType {
		final static String ORE_FIELD_YAML = "ore_field.yaml";
		private static final Map<String,OreFieldType> allOreFieldTypes = new HashMap<String,OreFieldType>();
		
		Item.ItemType oreType;
		
		private final float baseMineTime;
		private final float respawnTime;
		private final int   minimumMiningLevel;
		private final int   experience;
		
		private static void typeLoading() throws FileNotFoundException {
			Yaml yaml = new Yaml();
			FileInputStream stream = new FileInputStream(new File(ORE_FIELD_YAML));
			
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> rawList = (List<Map<String,Object>>)yaml.load(stream);
			
			for(Map<String,Object> fields : rawList) {
				String   name               = (String)fields.get("name");
				String   imageFilename      = (String)fields.get("imageFilename");
				ItemType oreType            = ItemType.getItemTypeObject((String)fields.get("oreType"));
				float    baseMineTime       = (float)fields.get("baseMineTime");
				float    respawnTime        = (float)fields.get("respawnTime");
				int      minimumMiningLevel = (int)fields.get("minimumMiningLevel");
				int      experience         = (int)fields.get("experience");
				
				OreFieldType oreFieldType = new OreFieldType(name,imageFilename,oreType,baseMineTime,respawnTime,minimumMiningLevel,experience);
				
				allOreFieldTypes.put(name,oreFieldType);
			}
		}
		
		static OreFieldType getItemTypeObject(String oreFieldTypeName) { return allOreFieldTypes.get(oreFieldTypeName); }
		
		static {
			try {
				typeLoading();
			} catch(FileNotFoundException e) {
				throw new IllegalStateException("Invalid ore field type filename.");
			}
		}
		
		OreFieldType(String name, String imageFilename, ItemType oreType, float baseMineTime, float respawnTime, 
		             int minimumMiningLevel, int experience) {

			super(name, imageFilename);
			this.oreType            = oreType;
			this.baseMineTime       = baseMineTime;
			this.respawnTime        = respawnTime;
			this.minimumMiningLevel = minimumMiningLevel;
			this.experience         = experience;
		}
	}
	
	OreField(Point lowerCorner, Point dimensions, OreFieldType oreFieldType) {
		super(lowerCorner,dimensions);
		this.oreFieldType = oreFieldType;
	}
}
