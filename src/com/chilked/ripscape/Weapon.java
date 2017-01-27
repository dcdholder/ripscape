package com.chilked.ripscape;

import java.io.*;
import java.util.*;

import org.yaml.snakeyaml.Yaml;

import com.chilked.ripscape.Ammunition.AmmunitionType;

public class Weapon extends Item {
	private final WeaponType weaponType;
	
	private static class WeaponType extends ItemType {
		final static String WEAPON_YAML = "weapon.yaml";
		private static final Map<String,WeaponType> allWeaponTypes = new HashMap<String,WeaponType>();
		
		private final SubType    subType;
		private final DamageType damageType;
		private final Handedness handedness;

		private final boolean                expendsAmmunition;
		private final AmmunitionType.SubType ammunitionType;

		private final float reach;

		private final float baseDamage;
		private final float attackSpeed;
		
		public enum SubType    { dagger, pick; }
		public enum DamageType { slash, stab, crush; }
		public enum Handedness { one, two; }
		
		private static void typeLoading() throws FileNotFoundException {
			Yaml yaml = new Yaml();
			FileInputStream stream = new FileInputStream(new File(WEAPON_YAML));
			
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> rawList = (List<Map<String,Object>>)yaml.load(stream);
			
			for(Map<String,Object> fields : rawList) {
				String                 name              = (String)fields.get("name");
				int                    baseValue         = (int)fields.get("baseValue");
				SubType                subType           = SubType.valueOf((String)fields.get("subType"));
				DamageType             damageType        = DamageType.valueOf((String)fields.get("damageType"));
				Handedness             handedness        = Handedness.valueOf((String)fields.get("handedness"));
				boolean                expendsAmmunition = (boolean)fields.get("expendsAmmunition");
				AmmunitionType.SubType ammunitionType    = null;
				float                  reach             = (float)fields.get("reach");
				float                  baseDamage        = (float)fields.get("baseDamage");
				float                  attackSpeed       = (float)fields.get("attackSpeed");
				
				if(expendsAmmunition) { //will throw an exception if that ammo type does not exist
					ammunitionType = AmmunitionType.getAmmunitionTypeObject(((String)fields.get("ammunitionType"))).getSubType();
				}
				
				WeaponType weaponType = new WeaponType(name,baseValue,subType,damageType,handedness,expendsAmmunition,
				                                       ammunitionType,reach,baseDamage,attackSpeed);
				
				allWeaponTypes.put(name,weaponType);
			}
		}
		
		static WeaponType getWeaponTypeObject(String weaponTypeName) { return allWeaponTypes.get(weaponTypeName); }
		
		static {
			try {
				typeLoading();
			} catch(FileNotFoundException e) {
				throw new IllegalStateException("Invalid weapon type filename.");
			}
		}
		
		WeaponType(String name, int baseValue, SubType subType, DamageType damageType, 
		           Handedness handedness, boolean expendsAmmunition, AmmunitionType.SubType ammunitionType, 
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
	
	Weapon(String name) {
		super(WeaponType.getWeaponTypeObject(name));
		this.weaponType = (WeaponType)itemType;
	}
}
