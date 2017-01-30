package com.chilked.ripscape;

import java.util.*;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import com.chilked.ripscape.Item.ItemType;
import com.chilked.ripscape.PC.Skillset.Skill;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Crafter extends WorldObject { //anvils, forges etc.
	private final CrafterType crafterType;
	
	private static class CrafterType extends WorldObject.WorldObjectType {
		final static String CRAFTER_YAML = "crafter.yaml";
		private static final Map<String,CrafterType> allCrafterTypes = new HashMap<String,CrafterType>();
		
		private final PC.Skillset.Skill targetSkill; 
		
		private final CraftingTransactionType[] craftingTransactionType;
		private final boolean  requiresTool;
		private final ItemType tool;
		
		private static class CraftingTransactionType {
			private final Item[] inputItems;
			private final Item[] outputItems;
			
			private final int minimumLevel;
			private final int experience;
			
			private static CraftingTransactionType[] typeLoading(List<Map<String,Object>> transactionList) {
				CraftingTransactionType[] transactionTypes = new CraftingTransactionType[transactionList.size()];
				
				int typeIndex = 0;
				for(Map<String,Object> listElement : transactionList) {
					List<Map<String,Object>> inputItemsList  = (List<Map<String,Object>>)listElement.get("inputItems");
					List<Map<String,Object>> outputItemsList = (List<Map<String,Object>>)listElement.get("outputItems");

					int minimumLevel = (int)listElement.get("minimumLevel");
					int experience   = (int)listElement.get("experience");
					
					int index = 0;
					Item[] inputItems = new Item[inputItemsList.size()];
					for(Map<String,Object> inputItem : inputItemsList) {
						inputItems[index] = new Item(ItemType.getItemTypeObject((String)inputItem.get("name")),(int)inputItem.get("num"));
						index++;
					}
					
					index = 0;
					Item[] outputItems = new Item[outputItemsList.size()];
					for(Map<String,Object> outputItem : outputItemsList) {
						inputItems[index] = new Item(ItemType.getItemTypeObject((String)outputItem.get("name")),(int)outputItem.get("num"));
					}
					
					transactionTypes[typeIndex] = new CraftingTransactionType(inputItems,outputItems,minimumLevel,experience);
					typeIndex++;
				}
				
				return transactionTypes;
			}
			
			CraftingTransactionType(Item[] inputItems, Item[] outputItems, int minimumLevel, 
			                        int experience) {
				
				this.inputItems   = inputItems;
				this.outputItems  = outputItems;
				this.minimumLevel = minimumLevel;
				this.experience   = experience;
			}
		}
		
		private static void typeLoading() throws FileNotFoundException {
			Yaml yaml = new Yaml();
			FileInputStream stream = new FileInputStream(new File(CRAFTER_YAML));
			
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> rawList = (List<Map<String,Object>>)yaml.load(stream);
			
			for(Map<String,Object> fields : rawList) {
				String   name            = (String)fields.get("name");
				String   imageFilename   = (String)fields.get("imageFilename");
				Point    dimensions      = new Point((int)fields.get("width"),(int)fields.get("height"));
				Skill    targetSkill     = Skill.valueOf((String)fields.get("oreType"));
				boolean  requiresTool    = (boolean)fields.get("requiresTool");
				ItemType tool            = ItemType.getItemTypeObject((String)fields.get("toolName"));

				List<Map<String,Object>> transactionList = (List<Map<String,Object>>)fields.get("transaction");
				
				CraftingTransactionType[] craftingTransactionType = CraftingTransactionType.typeLoading(transactionList);
				
				CrafterType crafterType = new CrafterType(name,imageFilename,dimensions,targetSkill,craftingTransactionType,requiresTool,tool);
				
				allCrafterTypes.put(name,crafterType);
			}
		}
		
		static CrafterType getItemTypeObject(String crafterTypeName) { return allCrafterTypes.get(crafterTypeName); }
		
		static {
			try {
				typeLoading();
			} catch(FileNotFoundException e) {
				throw new IllegalStateException("Invalid crafter type filename.");
			}
		}
		
		CrafterType(String name, String imageFilename, Point dimensions, PC.Skillset.Skill targetSkill, CraftingTransactionType[] craftingTransactionType,
		            boolean requiresTool, ItemType tool) {
			
			super(name,imageFilename,dimensions);
			this.targetSkill             = targetSkill;
			this.craftingTransactionType = craftingTransactionType;
			this.requiresTool            = requiresTool;
			this.tool                    = tool;
		}
	}
	
	Crafter(CrafterType crafterType, Point lowerCorner) {
		super(crafterType,lowerCorner);
		this.crafterType = (CrafterType)worldObjectType;
	}
}
