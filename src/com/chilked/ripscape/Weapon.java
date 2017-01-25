package com.chilked.ripscape;

public class Weapon extends Item {
	private final WeaponType weaponType;

	private class WeaponType extends ItemType {
		final static String WEAPON_JSON = "weapon.json";
		private static final Map<WeaponType> allWeaponTypes = new HashMap<WeaponType>();

		public enum SubType    { dagger, pick; }
		public enum DamageType { slash, stab, crush; }
		public enum Handedness { one, two; }
		
		private final DamageType damageType;
		private final Handedness handedness;

		private final boolean expendsAmmunition;
		private final String  ammunitionType;

		private final float reach;

		private final float baseDamage;
		private final float attackSpeed;
		
		WeaponType getWeaponTypeObject() { allWeaponTypes.get(); }
		
		static { typeLoading(WEAPON_JSON, allWeaponTypes); }
		
		WeaponType(String name, SubType subType, int baseValue, DamageType damageType, 
		           Handedness handedness, boolean expendsAmmunition, String ammunitionName, 
		           float reach, float baseDamage, float attackSpeed) {

			super(name,baseValue,false); //we choose to make all weapons non-stackable
			this.subType           = subType;
			this.damageType        = damageType;
			this.handedness        = handedness;
			this.expendsAmmunition = expendsAmmunition;
			this.ammunitionName    = ammunitionName;
			this.reach             = reach;
			this.baseDamage        = bsaeDamage;
		}
	}
	
	Weapon(String name) {
		weaponType = WeaponType.getWeaponTypeObject(name);
	}
}
