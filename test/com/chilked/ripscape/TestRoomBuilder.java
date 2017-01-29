package com.chilked.ripscape;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

public class TestRoomBuilder {
	static final String TEST_ROOMS = "resources/test/rooms";
	
	//load the room ASCII art, and compare the filled version with
	//the filled version in the test directory
	
	@Test
	void testFloorWallFill() {
		Map<String,String> preToPostProcessFilename = new HashMap<String,String>();
		
		//TODO: get the filenames from the filesystem
		
		for(String preProcessFilename : preToPostProcessFilename.keySet()) {
			String[] preText  = Globe.Room.RoomBuilder.loadRoomText(preProcessFilename);
			String[] postText = Globe.Room.RoomBuilder.loadRoomText(preToPostProcessFilename.get(preProcessFilename));
			
			assertEquals(preText.length,postText.length);
			
			for(int i=0; i<preText.length; i++) {
				assertEquals(preText[i],postText[i]);
			}
		}
	}
}
