package com.chilked.ripscape;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.geom.Point2D;

import org.junit.Test;

public class TestHitbox {
	Point lowerCorner = new Point(1,1);
	Point dimensions  = new Point(1,1);
	
	Hitbox hitboxA = new Hitbox(lowerCorner,dimensions); //box in the center of a 3x3 field
	
	@Test
	void testHitboxHitboxDistance() {
		Point lowerCornerA = new Point(1,1);
		Point dimensionsA  = new Point(1,1);
		
		Hitbox hitboxA = new Hitbox(lowerCornerA,dimensionsA); //box in the center of a 3x3 field
		
		for(int j=0;j<=4;j++) {
			for(int i=0;i<=4;i++) {
				Point lowerCornerB = new Point(i,j);
				Hitbox hitboxB    = new Hitbox(lowerCornerB,new Point(1,1));
				double distance   = hitboxA.distance(hitboxB);
				
				//hitboxes in the corners should be at a distance of approx. sqrt(2)
				if(i==0&&j==0 || i==0&&j==4 || i==4&&j==0 || i==4&&j==4) {
					assertTrue(Math.abs(distance-Math.sqrt(2))<1.0E-6);
				} else if(i==0 || j==0 || i==4 || j==4) {
					//hitboxes on the edges, but not on the corners should be at a distance of approx. 1.0
					assertTrue(Math.abs(distance-1.0)<1.0E-6);
				} else {
					//hitboxes surrounding, or in the center should have distance of approx. 0.0
					assertTrue(Math.abs(distance+0.5)<1.0E-6);
				}
			}
		}		
	}
	
	//TODO: needs to be reworked for non-float Points
	/*
	@Test
	void testHitboxPointDistance() {
		Point lowerCorner = new Point(1,1);
		Point dimensions  = new Point(1,1);
		
		Hitbox hitboxA = new Hitbox(lowerCorner,dimensions); //box in the center of a 3x3 field
		
		for(int j=0;j<=2;j++) {
			for(int i=0;i<=2;i++) {
				Point testPoint = new Point(i*1.5,j*1.5);
				double distance = hitboxA.distance(testPoint);
				
				//points in the corners should be at a distance of approx. sqrt(2)
				if(i==0&&j==0 || i==0&&j==3 || i==3&&j==0 || i==3&&j==3) {
					assertTrue(Math.abs(distance-Math.sqrt(2))<1.0E-6);
				}
				
				//points on the edges, but not on the corners should be at a distance of 1
				if(i==1 || j==1 && !(i==1&&j==1)) {
					assertTrue(Math.abs(distance-1.0)<1.0E-6);
				}
				
				//point in the center should have distance -0.5
				if(i==1&&j==1) {
					assertTrue(Math.abs(distance+0.5)<1.0E-6);
				}
			}
		}
	}
	*/
}
