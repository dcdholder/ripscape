package com.chilked.ripscape;

import java.util.*;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import com.chilked.ripscape.Container.ContainerType;
import com.chilked.ripscape.Item.ItemType;
import com.chilked.ripscape.PC.Skillset;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;

public class OreField extends WorldObject {
	private final OreFieldType oreFieldType;
	
	long lastMineTime = 0;
	
	public static class OreFieldType extends WorldObjectType {
		final static String ORE_FIELD_YAML = "ore_fields.yaml";
		private static final Map<String,OreFieldType> allOreFieldTypes = new HashMap<String,OreFieldType>();
		
		Item.ItemType oreType;
		
		private final double baseMineTime;
		private final double respawnTime;
		private final int   minimumMiningLevel;
		private final int   experience;
		
		private static void typeLoading() throws FileNotFoundException {
			Yaml yaml = new Yaml();
			FileInputStream stream = new FileInputStream(new File(WORLD_OBJECT_YAML_DIRECTORY+ORE_FIELD_YAML));
			
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> rawList = (List<Map<String,Object>>)yaml.load(stream);
			
			for(Map<String,Object> fields : rawList) {
				String   name               = (String)fields.get("name");
				String   imageFilename      = (String)fields.get("imageFilename");
				ItemType oreType            = ItemType.getItemTypeObject((String)fields.get("oreType"));
				double    baseMineTime      = (double)fields.get("baseMineTime");
				double    respawnTime       = (double)fields.get("respawnTime");
				int      minimumMiningLevel = (int)fields.get("minimumMiningLevel");
				int      experience         = (int)fields.get("experience");
				
				OreFieldType oreFieldType = new OreFieldType(name,imageFilename,oreType,baseMineTime,respawnTime,minimumMiningLevel,experience);
				
				allOreFieldTypes.put(name,oreFieldType);
			}
		}
		
		static OreFieldType getOreFieldTypeObject(String oreFieldTypeName) { return allOreFieldTypes.get(oreFieldTypeName); }
		
		static {
			try {
				typeLoading();
			} catch(FileNotFoundException e) {
				throw new IllegalStateException("Invalid ore field type filename.");
			}
		}
		
		OreFieldType(String name, String imageFilename, ItemType oreType, double baseMineTime, double respawnTime, 
		             int minimumMiningLevel, int experience) {

			super(name, imageFilename, new Point(1,1)); //TODO: ore fields always have 1x1 dimensions - make this static
			this.oreType            = oreType;
			this.baseMineTime       = baseMineTime;
			this.respawnTime        = respawnTime;
			this.minimumMiningLevel = minimumMiningLevel;
			this.experience         = experience;
		}
	}
	
	void mine(PC pc) {
		//check if respawn timer is over
		if(lastMineTime > System.currentTimeMillis() - oreFieldType.respawnTime) {
			throw new IllegalStateException("Cannot mine before ore respawn.");
		}
		
		//check if initial conditions are met
		if(pc.equipment.viewWeapon().getWeaponType().getSubType() != Weapon.WeaponType.SubType.pick) {
			throw new IllegalArgumentException("No pick equipped.");
		}
		if(pc.skillset.getLevel(Skillset.Skill.mining)<oreFieldType.minimumMiningLevel) {
			throw new IllegalArgumentException("Requires mining level " + oreFieldType.minimumMiningLevel);
		}
		if(pc.inventory.isFull()) {
			throw new IllegalArgumentException("Inventory is full.");
		}
		if(!pc.hitbox.withinDistance((double)pc.equipment.viewWeapon().getWeaponType().getReach(),hitbox)) {
			throw new IllegalArgumentException("Too far away.");
		}
		
		//re-check if conditions are met until mining timer expires
		
		//give ore, experience, set lastMineTime to current time
		pc.inventory.add(new Item(oreFieldType.oreType));
		pc.skillset.addExperience(Skillset.Skill.mining,oreFieldType.experience);
		lastMineTime = System.currentTimeMillis();
	}
	
	boolean respawned() {
		return false; //TODO: make this do something
	}
	
	OreField(String oreFieldName, Point lowerCorner) {
		this(OreFieldType.getOreFieldTypeObject(oreFieldName),lowerCorner);
	}
	
	OreField(OreFieldType oreFieldType, Point lowerCorner) {
		super(oreFieldType,lowerCorner);
		this.oreFieldType = oreFieldType;
	}
}
