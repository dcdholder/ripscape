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
	
	public boolean enclosed(Point2D.Double point) {
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
	
	public boolean withinDistance(Double distance, Point2D.Double point) {
		if(distance(point)<distance) {
			return true;
		} else {
			return false;
		}
	}
	
	private double internalDistance(Point2D.Double point) {
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
			} else { //you're inside the hitbox!
				return internalDistance(point);
			}
		}
	}
	
	Hitbox(Point lowerCorner, Point dimensions) { //lowercorner can either be float-valued or integral
		this(new Point2D.Double((double)lowerCorner.getX(),(double)lowerCorner.getY()), dimensions);
	}
	
	Hitbox(Point2D.Double lowerCorner, Point dimensions) { //dimensions MUST map to an integer
		this.lowerLeft  = lowerCorner;
		this.lowerRight = new Point2D.Double(lowerCorner.getX()+dimensions.getX(),lowerCorner.getY());
		this.upperLeft  = new Point2D.Double(lowerCorner.getX(),lowerCorner.getY()+dimensions.getY());
		this.upperRight = new Point2D.Double(lowerCorner.getX()+dimensions.getX(),lowerCorner.getY()+dimensions.getY());
	}
}
