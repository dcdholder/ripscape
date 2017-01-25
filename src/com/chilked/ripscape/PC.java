package com.chilked.ripscape;

import java.util.*;

public class PC {
	Skillset  skillset  = new Skillset();
	Inventory inventory = new Inventory();
	Equipment equipment = new Equipment();

	class Skillset {
		Map<Skill,Integer> skills = new HashMap<Skill, Integer>();
		
		Skillset() {
			skills.put(Skill.mining,1);
			skills.put(Skill.smithing,1);
		}
	}

	class Inventory {
		static final int capacity = 28;
		Item[] items = new Item[capacity];
		
		void add(Item newItem) {
			for(int i=0; i<capacity; i++) {
				if(items[i]==null) {
					items[i] = newItem;
					return;
				}
			}
			
			throw new IllegalArgumentException("Cannot add item; no more inventory space.");
		}
		
		void remove(int index) { items[index] = null; }
	}
	
	class Equipment {
		Map<ArmourSlot,Armour> armourSlots = new HashMap<ArmourSlot,Armour>();
		Weapon weaponSlot;
		
		//methods return previously-equipped weapons and armour
		//be prepared for nulls
		Armour equipArmour(Armour armour) {
			Armour prevArmour = armourSlots.get(armour.getArmourSlot());
			armourSlots.put(armour.getArmourSlot(),armour);
			
			return prevArmour;
		}
		Armour removeArmour(ArmourSlot armourSlot) {
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
	}
	
	public void equipItem(int inventoryIndex) {
		if(inventory.items[inventoryIndex] instanceof Armour) {
			equipArmour(inventoryIndex);
		} else if(inventory.items[inventoryIndex] instanceof Weapon) {
			equipWeapon(inventoryIndex);
		} else {
			throw new IllegalArgumentException("Cannot equip non-equippable item.");
		}
	}
	
	private void equipArmour(int inventoryIndex) {
		if(inventory.items[inventoryIndex] instanceof Armour) {
			Armour armour    = (Armour)inventory.items[inventoryIndex];
			Armour oldArmour = equipment.equipArmour(armour);
		
			if(oldArmour!=null) {
				inventory.add(oldArmour);
			}
		} else {
			throw new IllegalArgumentException("Cannot equip non-armour as armour.");
		}
	}
	public void removeArmour(ArmourSlot armourSlot) {
		Armour oldArmour = equipment.removeArmour(armourSlot);
		
		if(oldArmour!=null) {
			inventory.add(oldArmour);
		}
	}
	
	private void equipWeapon(int inventoryIndex) {
		if(inventory.items[inventoryIndex] instanceof Weapon) {
			Weapon weapon    = (Weapon)inventory.items[inventoryIndex];
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
