package com.chilked.ripscape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import org.yaml.snakeyaml.Yaml;

public class Armour extends Item {
	private final ArmourType armourType; //itemType cast
	
	public static class ArmourType extends Item.ItemType {
		final static String ARMOUR_YAML = "armour.yaml";
		private static final Map<String,ArmourType> allArmourTypes = new HashMap<String,ArmourType>();
		
		private final ArmourSlot armourSlot;
		
		private final double baseDefense;
		
		public enum ArmourSlot { head, neck, torso, legs, feet, hands, finger; }
		
		private static void typeLoading() throws FileNotFoundException {
			Yaml yaml = new Yaml();
			FileInputStream stream = new FileInputStream(new File(ITEM_YAML_DIRECTORY+ARMOUR_YAML));
			
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> rawList = (List<Map<String,Object>>)yaml.load(stream);
			
			for(Map<String,Object> fields : rawList) {
				String     name        = (String)fields.get("name");
				int        baseValue   = (int)fields.get("baseValue");
				ArmourSlot armourSlot  = ArmourSlot.valueOf((String)fields.get("armourSlot"));
				double     baseDefense = (double)fields.get("baseDefense");
				
				ArmourType armourType = new ArmourType(name,baseValue,armourSlot,baseDefense);
				
				allArmourTypes.put(name,armourType);
			}
		}
		
		static ArmourType getArmourTypeObject(String armourTypeName) { return allArmourTypes.get(armourTypeName); }
		
		static {
			try {
				typeLoading();
			} catch(FileNotFoundException e) {
				throw new IllegalStateException("Invalid armour type filename.");
			}
		}
		
		ArmourType(String name, int baseValue, ArmourSlot armourSlot, double baseDefense) {
			super(name,baseValue,false); //we choose to make all armour non-stackable
			this.armourSlot  = armourSlot;
			this.baseDefense = baseDefense;
		}
	}
	
	ArmourType.ArmourSlot getArmourSlot() { return armourType.armourSlot; }
	
	Armour(String name) {
		super(ArmourType.getArmourTypeObject(name));
		this.armourType = (ArmourType)itemType;
	}
}
