package com.chilked.ripscape;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

class Inventory {
	private final int capacity;
	Item[] items;
	
	void add(Item newItem) {
		for(int i=0; i<capacity; i++) {
			if(items[i]==null) {
				items[i] = newItem;
				return;
			}
		}
		
		throw new IllegalArgumentException("Cannot add item; no more inventory space.");
	}
	
	Item view(int index) {
		if(index>=capacity) {
			throw new NoSuchElementException("Illegal inventory index.");
		} else {
			return items[index];  
		}
	}
	
	Item retrieve(int index) {
		Item desiredItem = view(index);
		remove(index);
		
		return desiredItem;
	}
	
	void remove(int index) { 
		items[index] = null; 
	}
	
	List<Integer> retrieveIndices(Item checkItem) {
		List<Integer> indices = new ArrayList<Integer>();
		
		for(int i=0; i<capacity; i++) {
			if(view(i).getItemType().equals(checkItem.getItemType())) {
				indices.add(i);
			}
		}
		
		return indices;
	}
	
	boolean isFull() {
		for(int i=0; i<capacity; i++) {
			if(items[i]==null) {
				return false;
			}
		}
		return true;
	}
	
	Inventory(int capacity) {
		this.capacity = capacity;
		this.items    = new Item[capacity];
	}
}
