package com.chilked.ripscape;

public abstract class Item {
	int num;
	
	Item()        { this(1); }
	Item(int num) { this.num = num; }
}
