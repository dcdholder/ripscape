package com.chilked.ripscape;

import java.util.*;

public class Weapon extends Item {
	final static String WEAPON_JSON = "weapon.json";
	private static final Map<String,WeaponType> allWeaponTypes = new HashMap<String,WeaponType>();
	
	private final WeaponType weaponType;

	public enum SubType    { dagger, pick; }
	public enum DamageType { slash, stab, crush; }
	public enum Handedness { one, two; }
	
	private class WeaponType extends ItemType {
		private final SubType    subType;
		private final DamageType damageType;
		private final Handedness handedness;

		private final boolean                   expendsAmmunition;
		private final Ammunition.AmmunitionType ammunitionType;

		private final float reach;

		private final float baseDamage;
		private final float attackSpeed;
		
		WeaponType(String name, SubType subType, int baseValue, DamageType damageType, 
		           Handedness handedness, boolean expendsAmmunition, Ammunition.AmmunitionType ammunitionType, 
		           float reach, float baseDamage, float attackSpeed) {

			super(name,baseValue,false); //we choose to make all weapons non-stackable
			this.subType           = subType;
			this.damageType        = damageType;
			this.handedness        = handedness;
			this.expendsAmmunition = expendsAmmunition;
			this.ammunitionType    = ammunitionType;
			this.reach             = reach;
			this.baseDamage        = baseDamage;
			this.attackSpeed       = attackSpeed;
		}
	}
	
	WeaponType getWeaponTypeObject(String weaponTypeName) { return allWeaponTypes.get(weaponTypeName); }
	
	private static void typeLoading(String armourJson, Map<String,WeaponType> weaponTypesContainer) {
		
	}
	
	static { typeLoading(WEAPON_JSON, allWeaponTypes); }
	
	Weapon(String name) {
		weaponType = getWeaponTypeObject(name);
	}
}
