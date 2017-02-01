package com.chilked.ripscape;

import java.awt.Point;

import com.chilked.ripscape.Item.ItemType;

public class Ripscape {
	static public void main(String[] args) {
		loadPrerequisiteClasses();
	}
	
	static private void loadPrerequisiteClasses() {
		//required because WeaponType uses an AmmunitionType
		@SuppressWarnings("unused")
		Ammunition ammo      = new Ammunition("Bronze Arrow");
		Weapon     weapon    = new Weapon("Bronze Dagger");
		Armour     armour    = new Armour("Bronze Medium Helm");
		Container  container = new Container("Crate", new Point(0,0));
		Crafter    crafter   = new Crafter("Anvil", new Point(0,0));
		OreField   oreField  = new OreField("Tin Ore Field", new Point(0,0));
	}
}
