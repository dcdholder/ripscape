package com.chilked.ripscape;

public class PC {
	Inventory inventory = new Inventory();
	Equipment equipment = new Equipment();
	Inventory inventory = new Inventory();

	class Skillset {
		Map<Skill,Integer> skills = new HashMap<Skill, Integer>();
		
		Skillset {
			skills.put(Skill.Mining,1);
			skills.put(Skill.Smithing,1);
		}
	}

	class Inventory {
		static int capacity = 28;
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
		Map<ArmourSlot,Armour> armourSlots = new HashMap<ArmourSlot, Integer>();
		Weapon weaponSlot;
		
		//methods return previously-equipped weapons and armour
		//be prepared for nulls
		Armour equipArmour(Armour armour) {
			Armour prevArmour = armourSlots.get(armour.getArmourSlot());
			armourSlots.put(armourSlot,armour);
			
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
			throw new IllegalArgumentException("Cannot equip non-equippable item.")
		}
	}
	
	private void equipArmour(int inventoryIndex) {
		if(inventory.items[inventoryIndex] instanceof Armour) {
			Item tmpArmour   = equipment.equipArmour(inventory.items[inventoryIndex]);
			Armour oldArmour = (Armour)tmpArmour;
		
			if(oldArmour!=null) {
				inventory.add(oldArmour);
			}
		} else {
			throw new IllegalArgumentException("Cannot equip non-armour as armour.");
		}
	}
	public void removeArmour(ArmourSlot armourSlot) {
		Armour oldArmour = equipment.removeArmour(ArmourSlot armourSlot);
		
		if(oldArmour!=null) {
			inventory.add(oldArmour);
		}
	}
	
	private void equipWeapon(int inventoryIndex) {
		if(inventory.items[inventoryIndex] instanceof Weapon) {
			Item   tmpWeapon = equipment.equipWeapon(inventory.items[inventoryIndex]);
			Weapon oldWeapon = (Weapon)tmpWeapon
		
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
