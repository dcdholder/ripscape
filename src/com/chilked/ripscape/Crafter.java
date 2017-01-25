package com.chilked.ripscape;

import java.util.*;
import java.awt.*;

public class Crafter extends WorldObject { //anvils, forges etc.
	final static String CRAFTER_JSON = "crafter.json";
	private static final Map<String,CrafterType> allCrafterTypes = new HashMap<String,CrafterType>();
	
	private final CrafterType crafterType;
	
	private static class CrafterType extends WorldObjectType {
		private final Skill targetSkill; 
		
		private final CraftingTransactionType[] craftingTransactionType;
		private final boolean requiresTool;
		private final String  toolName;
		
		private static class CraftingTransactionType {
			private final Item[] inputItems;
			private final Item[] outputItems;
			
			private final int minimumLevel;
			private final int experience;
			
			CraftingTransactionType(Item[] inputItems, Item[] outputItems, int minimumLevel, 
			                        int experience) {
				
				this.inputItems   = inputItems;
				this.outputItems  = outputItems;
				this.minimumLevel = minimumLevel;
				this.experience   = experience;
			}
		}
		
		CrafterType(String name, String imageFilename, Skill targetSkill, CraftingTransactionType[] craftingTransactionType,
		            boolean requiresTool, String toolName) {
			
			super(name,imageFilename);
			this.targetSkill             = targetSkill;
			this.craftingTransactionType = craftingTransactionType;
			this.requiresTool            = requiresTool;
			this.toolName                = toolName;
		}
	}
	
	Crafter(Point lowerCorner, Point dimensions, CrafterType crafterType) {
		super(lowerCorner,dimensions);
		this.crafterType = crafterType;
	}
}
