package com.chilked.ripscape;

import java.util.*;

public class Link {
	private static final Set<Link> ALL_LINKS = new HashSet<Link>();

	private String inputRoomName;
	private int    inputPortalId;
	
	private String outputRoomName;
	private int    outputPortalId;
	
	private boolean sameInput(Link link) {
		if(this.inputRoomName==link.inputRoomName && this.inputPortalId==link.inputPortalId) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean sameOutput(Link link) {
		if(this.outputRoomName==link.outputRoomName && this.outputPortalId==link.outputPortalId) {
			return true;
		} else {
			return false;
		}
	}
	
	private void vetDirectionality() { //check to see if ALL_LINKS already contains something with the same input stats
		for(Link link : ALL_LINKS) {
			if(this.sameInput(link)) {
				if(this.sameOutput(link)) {
					throw new IllegalArgumentException("Duplicate link.");
				} else {
					throw new IllegalArgumentException("Links with the same input, different outputs exist.");
				}
			}
		}
	}
	
	List<Link> getLinksBelongingToRoom(String roomName) {
		List<Link> roomLinks = new ArrayList<Link>();
		
		for(Link link : ALL_LINKS) {
			if(link.inputRoomName.equals(roomName)) {
				roomLinks.add(link);
			}
		}
		
		if(roomLinks.size()==0) {
			throw new IllegalArgumentException("No link with this room name exists.");
		}
		
		return roomLinks;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + inputPortalId;
		result = prime * result + ((inputRoomName == null) ? 0 : inputRoomName.hashCode());
		result = prime * result + outputPortalId;
		result = prime * result + ((outputRoomName == null) ? 0 : outputRoomName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Link other = (Link) obj;
		if (inputPortalId != other.inputPortalId)
			return false;
		if (inputRoomName == null) {
			if (other.inputRoomName != null)
				return false;
		} else if (!inputRoomName.equals(other.inputRoomName))
			return false;
		if (outputPortalId != other.outputPortalId)
			return false;
		if (outputRoomName == null) {
			if (other.outputRoomName != null)
				return false;
		} else if (!outputRoomName.equals(other.outputRoomName))
			return false;
		return true;
	}
	
	Link(String inputRoomName, int inputPortalId, String outputRoomName, int outputPortalId) {
		this.inputRoomName  = inputRoomName;
		this.inputPortalId  = inputPortalId;
		this.outputRoomName = outputRoomName;
		this.outputPortalId = outputPortalId;

		vetDirectionality();
		
		ALL_LINKS.add(this);
	}
}
