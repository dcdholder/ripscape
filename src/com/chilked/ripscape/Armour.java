package com.chilked.ripscape;

public class Armour extends Item {
	private final ArmourType armourType;
	
	private class ArmourType extends ItemType {
		final static String ARMOUR_JSON = "armour.json";
		private static final Map<ArmourType> allArmourTypes = new HashMap<ArmourType>();
		
		private final ArmourSlot armourSlot;
		
		private final float baseDefense;
		
		ArmourType getArmourTypeObject() { allArmourTypes.get(); }
		
		static { typeLoading(ARMOUR_JSON, allArmourTypes); }
		
		ArmourType(String name, int baseValue, ArmourSlot armourSlot, float baseDefense) {
			super(name,baseValue,false); //we choose to make all armour non-stackable
			this.armourSlot  = armourSlot;
			this.baseDefense = baseDefense;
		}
	}
	
	Armour(String name) {
		armourType = ArmourType.getArmourTypeObject(name);
	}
}
