package com.chilked.ripscape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import org.yaml.snakeyaml.Yaml;

public class Ammunition extends Item {
	final static String AMMO_YAML = "ammo.yaml"; //TODO: set this to something
	private static final Map<String,AmmunitionType> allAmmunitionTypes = new HashMap<String,AmmunitionType>();
	
	private final AmmunitionType ammunitionType;
	
	public static class AmmunitionType extends Item.ItemType { 
		private final SubType subType;
		
		private final float baseDamage;
		
		public enum SubType { arrow, bolt, knife; }
		SubType getSubType() { return subType; }
		
		private static void typeLoading() throws FileNotFoundException {
			Yaml yaml = new Yaml();
			FileInputStream stream = new FileInputStream(new File(AMMO_YAML));
			
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> rawList = (List<Map<String,Object>>)yaml.load(stream);
			
			for(Map<String,Object> fields : rawList) {
				String  name       = (String)fields.get("name");
				int     baseValue  = (int)fields.get("baseValue");
				SubType subType    = SubType.valueOf((String)fields.get("subType"));
				float   baseDamage = (float)fields.get("baseDamage");
				
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
		
		AmmunitionType(String name, int baseValue, SubType subType, float baseDamage) {
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