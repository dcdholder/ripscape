package com.chilked.ripscape;

import static org.junit.Assert.*;

import java.io.File;
import java.util.*;

import org.junit.Test;

public class TestRoomBuilder {
	static final String POSITIVE_TEST_ROOMS            = "resources/test/RoomBuilder/positive";
	static final String NEGATIVE_UNCLOSED_TEST_ROOMS   = "resources/test/RoomBuilder/negative-unclosed";
	static final String NEGATIVE_WRONG_POST_TEST_ROOMS = "resources/test/RoomBuilder/negative-wrong-post";
	
	//load the room ASCII art, and compare the filled version with
	//the filled version in the test directory
	
	List<String> getFilenames(String directory) {
		List<String> filenames = new ArrayList<String>();
		File[] files = new File(directory).listFiles();
		
		for(File file : files) {
			if (file.isFile()) {
				filenames.add(file.getName());
			}
		}
		
		return filenames;
	}
	
	Map<String,String> getPrePostFilenames(String directory) {
		Map<String,String> prePostFilenames = new HashMap<String,String>();
		List<String> filenames = new ArrayList<String>();
		File[] files = new File(directory).listFiles();
		
		for(File file : files) {
			if (file.isFile()) {
				filenames.add(file.getName());
			}
		}
		
		for(String filename : filenames) {
			String prefix = filename.split("_")[0];
			String suffix = filename.split("_")[1];
			if(suffix.equals("pre.txt")) {
				if(filenames.contains(prefix + "_post.txt")) {
					prePostFilenames.put(filename,prefix + "_post.txt");
				} else {
					throw new IllegalStateException("Post-processed file not found.");
				}
			} else if(!suffix.equals("post.txt")) {
				throw new IllegalStateException("Should be _pre or _post.txt.");
			}
		}
		
		return prePostFilenames;
	}
	
	@Test
	void testFloorWallFillPositive() {
		Map<String,String> preToPostProcessFilename = getPrePostFilenames(POSITIVE_TEST_ROOMS);
		
		//TODO: get the filenames from the filesystem
		
		for(String preProcessFilename : preToPostProcessFilename.keySet()) {
			Globe.Room.RoomBuilder roomBuilderPre  = new Globe.Room.RoomBuilder(POSITIVE_TEST_ROOMS + "/" + preProcessFilename,'w','f');
			Globe.Room.RoomBuilder roomBuilderPost = new Globe.Room.RoomBuilder(POSITIVE_TEST_ROOMS + "/" + preToPostProcessFilename.get(preProcessFilename),'w','f');
			
			String[] preText  = roomBuilderPre.loadRoomText();
			String[] postText = roomBuilderPost.loadRoomText();
			
			assertEquals(preText.length,postText.length);
			
			for(int i=0; i<preText.length; i++) {
				assertEquals(preText[i],postText[i]);
			}
		}
	}
	
	@Test
	void testFloorWallFillNegativeUnclosed() {
		List<String> filenames = getFilenames(NEGATIVE_UNCLOSED_TEST_ROOMS);
		
		for(String filename : filenames) {
			Globe.Room.RoomBuilder roomBuilder = new Globe.Room.RoomBuilder(NEGATIVE_UNCLOSED_TEST_ROOMS + "/" + filename,'w','f');
			
			roomBuilder.loadRoomText();
		}
	}
	
	@Test
	void testFloorWallFillNegativeWrongPost() {
		Map<String,String> preToPostProcessFilename = getPrePostFilenames(NEGATIVE_WRONG_POST_TEST_ROOMS);
		
		//TODO: get the filenames from the filesystem
		
		for(String preProcessFilename : preToPostProcessFilename.keySet()) {
			Globe.Room.RoomBuilder roomBuilderPre  = new Globe.Room.RoomBuilder(NEGATIVE_WRONG_POST_TEST_ROOMS + "/" + preProcessFilename,'w','f');
			Globe.Room.RoomBuilder roomBuilderPost = new Globe.Room.RoomBuilder(NEGATIVE_WRONG_POST_TEST_ROOMS + "/" + preToPostProcessFilename.get(preProcessFilename),'w','f');
			
			String[] preText  = roomBuilderPre.loadRoomText();
			String[] postText = roomBuilderPost.loadRoomText();
			
			assertEquals(preText.length,postText.length);
			
			for(int i=0; i<preText.length; i++) {
				assertNotEquals(preText[i],postText[i]);
			}
		}
	}
}
