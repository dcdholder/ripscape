package com.chilked.ripscape;

import org.junit.Test;

/*
 * Test equipping and unequipping inventory items. 
 */

public class TestEquipment {
	Weapon weaponA = new Weapon("Bronze Dagger");
	Weapon weaponB = new Weapon("Iron Dagger");
	
	Armour armourA = new Armour("Bronze Medium Helm");
	Armour armourB = new Armour("Iron Medium Helm");
	
	Item nonEquippable = new Item("Tin Ore");
	
	private PC initializeInventory() {
		PC pc = new PC();
		
		pc.inventory.add(weaponA);       //index 0
		pc.inventory.add(weaponB);       //index 1
		pc.inventory.add(armourA);       //index 2
		pc.inventory.add(armourB);       //index 3
		pc.inventory.add(nonEquippable); //index 4
		
		return pc;
	}
	
	private PC initializeWithFullInventory() { //fill inventory with daggers
		PC pc = new PC();
		
		for(int i=0; i<pc.inventory.CAPACITY; i++) {
			pc.inventory.add(weaponA);
		}
		
		return pc;
	}
	
	@Test
	void testEquipItemWeaponSlotEmpty() {
		PC pc = initializeInventory();
		
		pc.equipItem(0);
		
		//check that item has been removed from inventory, is now equipped
	}
	
	@Test
	void testEquipItemWeaponSlotFull() {
		PC pc = initializeInventory();
		
		pc.equipItem(0);
		pc.equipItem(1);
		
		//check that old item is in inventory, new item is equipped
	}
	
	@Test
	void testEquipItemArmourSlotEmpty() {
		PC pc = initializeInventory();
		
		pc.equipItem(2);
		
		//same as testEquipItemWeaponSlotEmpty
	}
	
	@Test
	void testEquipItemArmourSlotFull() {
		PC pc = initializeInventory();
		
		pc.equipItem(2);
		pc.equipItem(3);
		
		//same as testEquipItemWeaponSlotFull
	}
	
	@Test
	void testEquipItemNotEquippable() {
		PC pc = initializeInventory();
		
		pc.equipItem(4); //confirm that this throws an exception
	}
	
	@Test
	void testEquipItemEmptyIndex() {
		PC pc = initializeInventory();
		
		pc.equipItem(5); //confirm that this also throws an exception
	}
	
	@Test
	void testRemoveArmourSlotEmpty() {
		PC pc = initializeInventory();
		
		pc.removeArmour(ArmourSlot.head); //confirm that this throws an exception
	}
	
	@Test
	void testRemoveArmourSlotFull() {
		PC pc = initializeInventory();
		
		pc.equipItem(3);
		pc.removeArmour(ArmourSlot.head);
	}
	
	@Test
	void testRemoveArmourInventoryFull() {
		PC pc = initializeWithFullInventory();
		
		pc.equipItem(3);
		pc.inventory.add(new Weapon("Bronze Dagger"));
		pc.removeArmour(ArmourSlot.head); //confirm that this throws an exception
	}
	
	@Test
	void TestRemoveWeaponSlotEmpty() {
		PC pc = initializeInventory();
		
		pc.removeWeapon(); //confirm that this throws an exception
	}
	
	@Test
	void TestRemoveWeaponSlotFull() {
		PC pc = initializeInventory();
		
		pc.equipItem(1);
		pc.removeWeapon();
	}
	
	@Test
	void TestRemoveWeaponInventoryFull() {
		PC pc = initializeWithFullInventory();
		
		pc.equipItem(1);
		pc.inventory.add(new Armour("Bronze Medium Helm"));
		pc.removeWeapon(); //confirm that this throws an exception
	}
}
