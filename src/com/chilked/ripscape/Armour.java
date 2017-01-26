package com.chilked.ripscape;

import java.util.*;

public class Armour extends Item {
	final static String ARMOUR_JSON = "armour.json";
	private static final Map<String,ArmourType> allArmourTypes = new HashMap<String,ArmourType>();
	
	private final ArmourType armourType; //itemType cast
	
	private class ArmourType extends ItemType {
		private final ArmourSlot armourSlot;
		
		private final float baseDefense;
		
		ArmourType(String name, int baseValue, ArmourSlot armourSlot, float baseDefense) {
			super(name,baseValue,false); //we choose to make all armour non-stackable
			this.armourSlot  = armourSlot;
			this.baseDefense = baseDefense;
		}
	}
	
	ArmourSlot getArmourSlot() { return armourType.armourSlot; }
	
	static ArmourType getArmourTypeObject(String armourTypeName) { return allArmourTypes.get(armourTypeName); }
	
	private static void typeLoading(String armourJson, Map<String,ArmourType> armourTypesContainer) {
		
	}
	
	static { typeLoading(ARMOUR_JSON, allArmourTypes); }
	
	Armour(String name) {
		super(getArmourTypeObject(name));
		this.armourType = (ArmourType)itemType;
	}
}
