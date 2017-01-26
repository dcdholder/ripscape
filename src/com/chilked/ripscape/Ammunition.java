package com.chilked.ripscape;

import java.util.*;

public class Ammunition extends Item {
	final static String AMMO_JSON = "ammo.json"; //TODO: set this to something
	private static final Map<String,AmmunitionType> allAmmunitionTypes = new HashMap<String,AmmunitionType>();
	
	private final AmmunitionType ammunitionType;
	
	public enum SubType { arrow, bolt, knife; }
	
	public static class AmmunitionType extends ItemType { 
		private final SubType subType;
		
		private final float baseDamage;
		
		AmmunitionType(String name, int baseValue, SubType subType, float baseDamage) {
			super(name,baseValue,true);
			this.subType    = subType;
			this.baseDamage = baseDamage;
		}
	}
	
	static AmmunitionType getAmmunitionTypeObject(String ammunitionTypeName) { return allAmmunitionTypes.get(ammunitionTypeName); }
	
	private static void typeLoading(String armourJson, Map<String,AmmunitionType> armourTypesContainer) {
		
	}
	
	static { typeLoading(AMMO_JSON, allAmmunitionTypes); }
	
	Ammunition(String name) {
		super(getAmmunitionTypeObject(name));
		this.ammunitionType = (AmmunitionType)itemType;
	}
}