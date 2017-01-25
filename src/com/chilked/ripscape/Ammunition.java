package com.chilked.ripscape;

public class Ammunition extends Item {
	private final AmmunitionType ammunitionType;
	
	private class AmmunitionType extends ItemType {
		final static String AMMO_JSON = "ammo.json"; //TODO: set this to something
		private static final Map<AmmunitionType> allAmmunitionTypes = new HashMap<AmmunitionType>();
	
		public enum Subtype { arrow, bolt, knife; } 
		
		private final Subtype subType;
		
		private final float baseDamage;
		
		AmmunitionType getAmmunitionTypeObject() { allAmmunitionTypes.get(); }
		
		static { typeLoading(AMMO_JSON, allAmmunitionTypes); }
		
		AmmunitionType(String name, int baseValue, SubType subType) {
			super(name,baseValue,true);
			this.subType = subType;
		}
	}
	
	Ammunition(String name) {
		ammunitionType = AmmunitionType.getAmmunitionTypeObject(name);
	}
}