package com.chilked.ripscape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import org.yaml.snakeyaml.Yaml;

public class Ammunition extends Item {	
	private final AmmunitionType ammunitionType;
	
	public static class AmmunitionType extends Item.ItemType {
		final static String AMMUNITION_YAML = "ammunition.yaml"; //TODO: set this to something
		private static final Map<String,AmmunitionType> allAmmunitionTypes = new HashMap<String,AmmunitionType>();
		
		private final SubType subType;
		
		private final double baseDamage;
		
		public enum SubType { arrow, bolt, knife; }
		SubType getSubType() { return subType; }
		
		private static void typeLoading() throws FileNotFoundException {
			Yaml yaml = new Yaml();
			FileInputStream stream = new FileInputStream(new File(ITEM_YAML_DIRECTORY+AMMUNITION_YAML));
			
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> rawList = (List<Map<String,Object>>)yaml.load(stream);
			
			for(Map<String,Object> fields : rawList) {
				String  name       = (String)fields.get("name");
				int     baseValue  = (int)fields.get("baseValue");
				SubType subType    = SubType.valueOf((String)fields.get("subType"));
				double  baseDamage = (double)fields.get("baseDamage");
				
				AmmunitionType ammunitionType = new AmmunitionType(name,baseValue,subType,baseDamage);
				
				allAmmunitionTypes.put(name,ammunitionType);
			}
		}
		
		static AmmunitionType getAmmunitionTypeObject(String ammunitionTypeName) { return allAmmunitionTypes.get(ammunitionTypeName); }
		
		static {
			try {
				typeLoading();
			} catch(FileNotFoundException e) {
				throw new IllegalStateException("Invalid ammunition type filename.");
			}
		}
		
		AmmunitionType(String name, int baseValue, SubType subType, double baseDamage) {
			super(name,baseValue,true);
			this.subType    = subType;
			this.baseDamage = baseDamage;
		}
	}
	
	Ammunition(String name) {
		super(AmmunitionType.getAmmunitionTypeObject(name));
		this.ammunitionType = (AmmunitionType)itemType;
	}
}