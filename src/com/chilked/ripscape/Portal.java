package com.chilked.ripscape;

import java.awt.*;

//links are added to portals after their initialization
public class Portal {
	int   portalId;
	Point position; //must stand in the portal square to be transported
	
	Link link = null;
	
	public void setLink(Link link) {
		if(link==null) {
			this.link = link;
		} else {
			throw new IllegalArgumentException("Portal link can only be set once.");
		}
	}
	
	Portal(int portalId, Point position) {
		this.portalId = portalId;
		this.position = position;
	}
}
