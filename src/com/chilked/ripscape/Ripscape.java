package com.chilked.ripscape;

public class Ripscape {
	static public void main() {
		loadPrerequisiteClasses();
	}
	
	static private void loadPrerequisiteClasses() {
		//required because WeaponType uses an AmmunitionType
		@SuppressWarnings("unused")
		Ammunition ammo = new Ammunition("Bronze Arrow");
	}
}
