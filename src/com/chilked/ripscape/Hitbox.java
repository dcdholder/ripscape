package com.chilked.ripscape;

import java.awt.Point;

import java.util.*;

public class Hitbox {
	Point dimensions;
	
	Point upperLeft;
	Point lowerLeft; //lowerLeft is the reference coordinate for any single-cell object
	Point upperRight;
	Point lowerRight;
	
	Point getUpperLeft()  { return upperLeft; }
	Point getLowerLeft()  { return lowerLeft; }
	Point getUpperRight() { return upperRight; }
	Point getLowerRight() { return lowerRight; }
	
	public boolean partiallyEnclosed(Hitbox hitbox) {
		if(enclosed(hitbox.upperLeft) || enclosed(hitbox.upperRight) || enclosed(hitbox.lowerLeft) || enclosed(hitbox.lowerRight)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean totallyEnclosed(Hitbox hitbox) {
		if(enclosed(hitbox.upperLeft) && enclosed(hitbox.upperRight) && enclosed(hitbox.lowerLeft) && enclosed(hitbox.lowerRight)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean enclosed(Point point) {
		if(lowerLeft.getX()<point.getX()&&lowerRight.getX()>point.getX()) {
			if(lowerLeft.getY()<point.getY()&&upperLeft.getY()<point.getY()) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean withinDistance(Double distance, Hitbox hitbox) {
		if(distance(hitbox)<distance) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean withinDistance(Double distance, Point point) {
		if(distance(point)<distance) {
			return true;
		} else {
			return false;
		}
	}
	
	private double internalDistance(Point point) {
		double distLeft   = point.getX()-lowerLeft.getX();
		double distRight  = lowerRight.getX()-point.getX();
		double distBottom = point.getY()-lowerRight.getY();
		double distTop    = upperRight.getY()-point.getY();
		
		return Math.min(Math.min(distLeft,distRight),Math.min(distTop,distBottom));
	}
	
	public double distance(Hitbox hitbox) {
		double upperLeftDistance  = distance(getUpperLeft());
		double upperRightDistance = distance(getUpperRight());
		double lowerLeftDistance  = distance(getLowerLeft());
		double lowerRightDistance = distance(getLowerRight());
		
		if(!partiallyEnclosed(hitbox)) {
			return Math.min(Math.min(upperLeftDistance, upperRightDistance), Math.min(lowerLeftDistance, lowerRightDistance));
		} else {
			return 0.0;
		}
	}
	
	public double distance(Point point) {
		if(point.x<=lowerLeft.x) {
			if(point.y<=lowerLeft.y) {
				return lowerLeft.distance(point);
			} else if(point.y>=upperLeft.y) {
				return upperLeft.distance(point);
			} else {
				return upperLeft.x - point.x;
			}
		} else if(point.x>=upperRight.x) {
			if(point.y<=lowerLeft.y) {
				return lowerRight.distance(point);
			} else if(point.y>=upperLeft.y) {
				return upperRight.distance(point);
			} else {
				return point.x - upperRight.x;
			}
		} else {
			if(point.y>upperLeft.y) {
				return point.y - upperLeft.y;
			} else if(point.y<lowerLeft.y) {
				return lowerLeft.y - point.y;
			} else { //you're inside the hitbox!
				return internalDistance(point);
			}
		}
	}
	
	public Set<Point> getPerimeter() {
		Set<Point> perimeterPoints = new HashSet<Point>();
		
		for(int i=lowerLeft.x-1;i<=lowerLeft.x+dimensions.x;i++) {
			perimeterPoints.add(new Point(i,lowerLeft.y-1));
			perimeterPoints.add(new Point(i,lowerLeft.y+dimensions.y));
		}
		
		for(int j=lowerLeft.y;j<=lowerLeft.y+dimensions.x-1;j++) {
			perimeterPoints.add(new Point(lowerLeft.x-1,j));
			perimeterPoints.add(new Point(lowerLeft.x+dimensions.x,j));
		}
		
		//only points with positive coordinates have meaning here
		Set<Point> perimeterPointsCopy = new HashSet<Point>();
		perimeterPointsCopy.addAll(perimeterPoints);
		for(Point surroundingPoint : perimeterPoints) {
			if(surroundingPoint.x<0 || surroundingPoint.y<0) {
				perimeterPointsCopy.remove(surroundingPoint);
			}
		}
		
		return perimeterPointsCopy;
	}
	
	Hitbox(Point lowerCorner, Point dimensions) { //dimensions MUST map to an integer
		this.lowerLeft  = lowerCorner;
		this.lowerRight = new Point(lowerCorner.x+dimensions.x,lowerCorner.y);
		this.upperLeft  = new Point(lowerCorner.x,lowerCorner.y+dimensions.y);
		this.upperRight = new Point(lowerCorner.x+dimensions.x,lowerCorner.y+dimensions.y);
		this.dimensions = dimensions;
	}
}
