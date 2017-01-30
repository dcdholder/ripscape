package com.chilked.ripscape;

import java.util.*;

import com.chilked.ripscape.Armour.ArmourType;
import com.chilked.ripscape.Globe.GlobalAddress;

public class PC {
	private static int PC_CAPACITY = 28;
	
	Skillset  skillset  = new Skillset();
	Inventory inventory = new Inventory(PC_CAPACITY);
	Equipment equipment = new Equipment();
	GlobalAddress globalAddress;
	Hitbox hitbox;
	
	static class Skillset {
		private static int MAX_LEVEL = 99;
		private static Map<Integer,Integer> minExperienceLevel = new HashMap<Integer,Integer>();
		
		private Map<Skill,Integer> level      = new HashMap<Skill,Integer>();
		private Map<Skill,Integer> experience = new HashMap<Skill,Integer>();
		
		public enum Skill { mining, smithing; }
		
		int getLevel(Skill skill)      { return experience.get(skill); }
		int getExperience(Skill skill) { return level.get(skill); }

		void addExperience(Skill skill, int addExperience) {
			int newExperience = experience.get(skill) + addExperience;
			experience.put(skill,newExperience);
			
			int currentLevel = level.get(skill);
			if(newExperience > minExperienceLevel.get(currentLevel+1)) {
				for(int i=1;i<MAX_LEVEL;i++) {
					if(newExperience<minExperienceLevel.get(currentLevel+i)) {
						level.put(skill,i-1);
						break;
					}
				}
			}
		}
			
		Skillset() {
			level.put(Skill.mining,1);
			level.put(Skill.smithing,1);
		}
	}
	
	class Equipment {
		Map<ArmourType.ArmourSlot,Armour> armourSlots = new HashMap<ArmourType.ArmourSlot,Armour>();
		Weapon weaponSlot;
		
		//methods return previously-equipped weapons and armour
		//be prepared for nulls
		Armour equipArmour(Armour armour) {
			Armour prevArmour = armourSlots.get(armour.getArmourSlot());
			armourSlots.put(armour.getArmourSlot(),armour);
			
			return prevArmour;
		}
		Armour removeArmour(ArmourType.ArmourSlot armourSlot) {
			Armour prevArmour = armourSlots.get(armourSlot);
			armourSlots.remove(armourSlot);
			
			return prevArmour;
		}

		Weapon equipWeapon(Weapon weapon) {
			Weapon prevWeapon = weaponSlot;
			weaponSlot        = weapon;
			
			return prevWeapon;
		}
		Weapon removeWeapon() {
			Weapon prevWeapon = weaponSlot;
			weaponSlot = null;
			
			return prevWeapon;
		}
		Armour viewArmour(ArmourType.ArmourSlot armourSlot) {
			return armourSlots.get(armourSlot);
		}
		Weapon viewWeapon() {
			return weaponSlot;
		}
	}
	
	public void equipItem(int inventoryIndex) {
		if(inventory.view(inventoryIndex) instanceof Armour) {
			equipArmour(inventoryIndex);
		} else if(inventory.items[inventoryIndex] instanceof Weapon) {
			equipWeapon(inventoryIndex);
		} else {
			throw new IllegalArgumentException("Cannot equip non-equippable item.");
		}
	}
	
	private void equipArmour(int inventoryIndex) {
		if(inventory.view(inventoryIndex) instanceof Armour) {
			Armour armour    = (Armour)inventory.retrieve(inventoryIndex);
			Armour oldArmour = equipment.equipArmour(armour);
		
			if(oldArmour!=null) {
				inventory.add(oldArmour);
			}
		} else {
			throw new IllegalArgumentException("Cannot equip non-armour as armour.");
		}
	}
	public void removeArmour(ArmourType.ArmourSlot armourSlot) {
		Armour oldArmour = equipment.removeArmour(armourSlot);
		
		if(oldArmour!=null) {
			inventory.add(oldArmour);
		}
	}
	
	private void equipWeapon(int inventoryIndex) {
		if(inventory.view(inventoryIndex) instanceof Weapon) {
			Weapon weapon    = (Weapon)inventory.retrieve(inventoryIndex);
			Weapon oldWeapon = equipment.equipWeapon(weapon);
		
			if(oldWeapon!=null) {
				inventory.add(oldWeapon);
			}
		} else {
			throw new IllegalArgumentException("Cannot equip a non-weapon as a weapon.");
		}
	}
	public void removeWeapon() {
		Weapon oldWeapon = equipment.removeWeapon();
		
		if(oldWeapon!=null) {
			inventory.add(oldWeapon);
		}
	}
}
