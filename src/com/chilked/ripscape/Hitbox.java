package com.chilked.ripscape;

import java.awt.Point;
import java.awt.geom.Point2D;

public class Hitbox {
	Point2D.Double upperLeft;
	Point2D.Double lowerLeft;
	Point2D.Double upperRight;
	Point2D.Double lowerRight;
	
	Point2D.Double getUpperLeft()  { return upperLeft; }
	Point2D.Double getLowerLeft()  { return lowerLeft; }
	Point2D.Double getUpperRight() { return upperRight; }
	Point2D.Double getLowerRight() { return lowerRight; }
	
	public boolean reaches(Double distance, Hitbox hitbox) {
		if(distance(hitbox)<distance) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean reaches(Double distance, Point2D.Double point) {
		if(distance(point)<distance) {
			return true;
		} else {
			return false;
		}
	}
	
	public double distance(Hitbox hitbox) {
		double upperLeftDistance  = distance(getUpperLeft());
		double upperRightDistance = distance(getUpperRight());
		double lowerLeftDistance  = distance(getLowerLeft());
		double lowerRightDistance = distance(getLowerRight());
		
		return Math.min(Math.min(upperLeftDistance, upperRightDistance), Math.min(lowerLeftDistance, lowerRightDistance));
	}
	
	public double distance(Point2D.Double point) {
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
			} else {
				return -1.0; //no precise distances yet if inside the hitbox
			}
		}
	}
	
	Hitbox(Point2D.Double lowerCorner, Point dimensions) { //dimensions MUST map to an integer
		this.lowerLeft  = lowerCorner;
		this.lowerRight = new Point2D.Double(lowerCorner.getX()+dimensions.getX(),lowerCorner.getY());
		this.upperLeft  = new Point2D.Double(lowerCorner.getX(),lowerCorner.getY()+dimensions.getY());
		this.upperRight = new Point2D.Double(lowerCorner.getX()+dimensions.getX(),lowerCorner.getY()+dimensions.getY());
	}
}
