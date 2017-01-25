package com.chilked.ripscape;

public class OreField extends WorldObject {
	private final OreFieldType oreFieldType;

	private class OreFieldType extends WorldObjectType {
		final static String ORE_FIELD_JSON = "ore_field.json";
		private static final Map<OreFieldType> allOreFieldTypes = new HashMap<OreFieldType>();
		
		OreType oreType;
		
		private final float baseMineTime;
		private final float respawnTime;
		private final int   minimumMiningLevel;
		
		OreFieldType(String name, String oreTypeName, float baseMineTime, float respawnTime, 
		             int minimumMiningLevel) {

			super(name);
			this.oreType            = oreType.getOreTypeObject(oreName);
			this.baseMineTime       = baseMineTime;
			this.respawnTime        = respawnTime;
			this.minimumMiningLevel = minimumMiningLevel;
		}
	}	
}
