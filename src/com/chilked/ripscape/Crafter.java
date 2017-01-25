package com.chilked.ripscape;

public class Crafter extends WorldObject { //anvils, forges etc.
	private final CrafterType crafterType;
	
	private class CrafterType extends WorldObjectType {
		final static String CRAFTER_JSON = "crafter.json";
		private static final Map<CrafterType> allCrafterTypes = new HashMap<CrafterType>();
		
		private final Skill targetSkill; 
		
		private final CraftingTransactionType[] craftingTransactionType;
		private final boolean requiresTool;
		private final String  toolName;
		
		private class CraftingTransactionType {
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
		
		CrafterType(Skill targetSkill, CraftingTransactionType[] craftingTransactionType,
		            boolean requiresTool, String toolName) {
			
			this.targetSkill             = targetSkill;
			this.craftingTransactionType = transactionType;
			this.requiresTool            = requiresTool;
			this.experience              = experience;
		}
	}
}
