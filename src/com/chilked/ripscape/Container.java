package com.chilked.ripscape;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class Container extends WorldObject {
	ContainerType containerType;
	
	Inventory inventory;
	
	public static class ContainerType extends WorldObjectType {
		final static String CONTAINER_YAML = "containers.yaml";
		private static final Map<String,ContainerType> allContainerTypes = new HashMap<String,ContainerType>();
		
		boolean stackable;
		int     capacity;
		
		private static void typeLoading() throws FileNotFoundException {
			Yaml yaml = new Yaml();
			FileInputStream stream = new FileInputStream(new File(WORLD_OBJECT_YAML_DIRECTORY+CONTAINER_YAML));
			
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> rawList = (List<Map<String,Object>>)yaml.load(stream);
			
			for(Map<String,Object> fields : rawList) {
				String  name          = (String)fields.get("name");
				String  imageFilename = (String)fields.get("imageFilename");
				Point   dimensions    = new Point((int)fields.get("width"),(int)fields.get("height"));
				boolean stackable     = (boolean)fields.get("stackable");
				int     capacity      = (int)fields.get("capacity");
				
				ContainerType containerType = new ContainerType(name,imageFilename,dimensions,stackable,capacity);
				
				allContainerTypes.put(name,containerType);
			}
		}
		
		static ContainerType getContainerTypeObject(String containerTypeName) { return allContainerTypes.get(containerTypeName); }
		
		static {
			try {
				typeLoading();
			} catch(FileNotFoundException e) {
				throw new IllegalStateException("Invalid container type filename.");
			}
		}
		
		ContainerType(String name, String imageFilename, Point dimensions, boolean stackable, int capacity) {
			super(name,imageFilename,dimensions);
			this.stackable = stackable;
			this.capacity  = capacity;
		}
	}
	
	Container(String containerName, Point lowerCorner) {
		this(ContainerType.getContainerTypeObject(containerName),lowerCorner);
	}
	
	Container(ContainerType containerType, Point lowerCorner) {
		super(containerType,lowerCorner);
		this.inventory = new Inventory(containerType.capacity);
	}
}
