// Spencer Vilicic
// CS480 Computer Graphics
// Programming Assignment 1
// 9/21/2020
//****************************************************************************
// SketchBase.  
//****************************************************************************
// Comments : 
//   Subroutines to manage and draw points, lines and triangles
//
// History :
//   Aug 2014 Created by Jianming Zhang (jimmie33@gmail.com) based on code by
//   Stan Sclaroff (from CS480 '06 poly.c)

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SketchBase {
	public SketchBase() {
		// deliberately left blank
	}

	// draw a point
	public static void drawPoint(BufferedImage buff, Point2D p) {
		buff.setRGB(p.x, buff.getHeight() - p.y - 1, p.c.getBRGUint8());
	}

	//////////////////////////////////////////////////
	// Implement the following two functions
	//////////////////////////////////////////////////

	// draw a line segment
	public static void drawLine(BufferedImage buff, Point2D p1, Point2D p2) {
		int x = p1.x; // starting point x
		int y = p1.y; // starting point y

		Point2D point = new Point2D(x,y,p1.c); // instantiate a new point

		boolean swapped = false; // flag for whether delta x & y have been swapped

		int dx = Math.abs(p2.x - p1.x); // magnitude of change in X
		int dy = Math.abs(p2.y - p1.y); // magnitude of change in Y

		int signx = (int) Math.signum(p2.x - p1.x); // stores delta x as -1, 0, 1
		int signy = (int) Math.signum(p2.y - p1.y); // stores delta y as -1, 0, 1

		// check if slope m is > +/-1. If yes, swap variables so
		// iterating over the algorithm can be handled in one loop
		if (dy > dx) {
			int temp = dy;
			dy = dx;
			dx = temp;

			swapped = true;
		}

		// initialize candidate decision variable p
		int p = (2*dy) - dx;

		// ***********************************
		// Bresenham's algorithm, all cases
		// ***********************************
		for (int i = 1; i <= dx; i++) {
			point.x = x;
			point.y = y;
			point.c = interpolateColor(p1,p2,point);
			
			drawPoint(buff, point);
			
			// this if statement handles the cases where the decision
			// variable is not negative, i.e. a case where "y" should
			// be incremented (this is why we have a decision variable)
			if (p >= 0) {
				if (swapped)
					x += signx; // increment x by -1, 0, or 1
				else
					y += signy; // increment y by -1, 0, or 1

				p = p - (2*dx); // one half of the equation for p(next), which only applies in p>=0
			}

			// no matter what, we want to increment "x"
			if (swapped)
				y += signy; // increment x by -1, 0, or 1
			else
				x += signx; // increment y by -1, 0, or 1

			p = p + (2*dy); // second half of the equation for p(next), which applies in all cases
		}

	}
	
	//Calculates distance between two point objects
	private static float calculateDistance(Point2D p1, Point2D p2) {
		return (float) Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
	}

	// draw a triangle
	public static void drawTriangle(BufferedImage buff, Point2D p1, Point2D p2, Point2D p3, boolean do_smooth) {
		// sets all vertex colors to the color of the first point if smooth fill is off
	
		if (!do_smooth) {
			p2.c = p1.c;
			p3.c = p1.c;
		}

		// Sort points ascending by Y
		Point2D v1 = new Point2D();
		Point2D v2 = new Point2D();
		Point2D v3 = new Point2D();

		Point2D[] points = { p1, p2, p3 };
		Point2D[] sortedPoints = sortPointsByY(points); // bubble sort points by Y values

		v1 = sortedPoints[0]; //lowest Y
		v2 = sortedPoints[1];
		v3 = sortedPoints[2]; //highest Y

		if (v2.y == v3.y) {
			// flat-top triangle
			fillFlatTopTriangle(buff, v1, v2, v3, do_smooth);	
		} else if (v1.y == v2.y) {
			// flat-bottom triangle
			fillFlatBottomTriangle(buff, v1, v2, v3, do_smooth);
		} else {
			
			Point2D v4 = new Point2D();
			v4.y = v2.y;
			v4.x = (int) (v1.x + ((float)(v2.y-v1.y)/(float)(v3.y-v1.y)) * (v3.x-v1.x)); // intercept formula

			//*************************************************
			float distance1to4 = calculateDistance(v1, v4);
			float distance3to4 = calculateDistance(v3, v4);
			
			float p3weight = (float) distance3to4 / (distance1to4+distance3to4);
			float p4weight = (float) distance1to4 / (distance1to4+distance3to4);
			
			v4.c.r = (v1.c.r * p3weight) + (v3.c.r * p4weight);
			v4.c.g = (v1.c.g * p3weight) + (v3.c.g * p4weight);
			v4.c.b = (v1.c.b * p3weight) + (v3.c.b * p4weight);
//			System.out.println("Color value v4 blue: " + v4.c.b);
//			System.out.println("P1 blue: " + p1.c.b);
//			System.out.println("p2 blue: " + p2.c.b);
			//*************************************************
//			if (do_smooth)
//				v4.c = interpolateColor(v1,v3,v4);
//			else
//				v4.c = v1.c;
			
			fillFlatBottomTriangle(buff, v1, v2, v4, do_smooth);
			fillFlatTopTriangle(buff, v2, v4, v3, do_smooth);			
		}
	}

	private static void fillFlatBottomTriangle(BufferedImage buff, Point2D v1, Point2D v2, Point2D v3, boolean do_smooth) {
		//use inverted slopes to iterate over y instead of x
		float invslope1 = (float)(v2.x-v1.x) / (float)(v2.y-v1.y);
		float invslope2 = (float)(v3.x-v1.x) / (float)(v3.y-v1.y);

		float currentx1 = v1.x;
		float currentx2 = v1.x;
		
		Point2D p1 = new Point2D();
		Point2D p2 = new Point2D();

		for (int currentY = v1.y; currentY <= v2.y; currentY++) {
			p1.x = (int) currentx1;
			p1.y = currentY;
			
			//*************************************************
			float d1 = calculateDistance(v1, p1);
			float d2 = calculateDistance(v2, p1);
			
			float p1weight = (float) d2 / (d1+d2);
			float p2weight = (float) d1 / (d1+d2);
			
			p1.c.r = (v1.c.r * p1weight) + (v2.c.r * p2weight);
			p1.c.g = (v1.c.g * p1weight) + (v2.c.g * p2weight);
			p1.c.b = (v1.c.b * p1weight) + (v2.c.b * p2weight);
			//*************************************************
//			if (do_smooth)
//				p1.c = interpolateColor(v1,v2,p1);
//			else
//				p1.c = v1.c;
			
			p2.x = (int) currentx2;
			p2.y = currentY;

			//*************************************************
			float d3 = calculateDistance(v1, p2);
			float d4 = calculateDistance(v3, p2);
			
			float p3weight = (float) d4 / (d3+d4);
			float p4weight = (float) d3 / (d3+d4);
			
			p2.c.r = (v1.c.r * p3weight) + (v3.c.r * p4weight);
			p2.c.g = (v1.c.g * p3weight) + (v3.c.g * p4weight);
			p2.c.b = (v1.c.b * p3weight) + (v3.c.b * p4weight);
			//*************************************************
//			if (do_smooth)
//				p2.c = interpolateColor(v1,v3,p1);
//			else
//				p2.c = v1.c;
				
		    drawLine(buff, p1, p2);
		    
		    currentx1 += invslope1;
		    currentx2 += invslope2;
		}

	}

	private static void fillFlatTopTriangle(BufferedImage buff, Point2D v1, Point2D v2, Point2D v3, boolean do_smooth) {
		//use inverted slopes to iterate over y instead of x
		float invslope1 = (float)(v3.x - v1.x) / (float)(v3.y - v1.y);
		float invslope2 = (float)(v3.x - v2.x) / (float)(v3.y - v2.y);

		float currentx1 = v3.x;
		float currentx2 = v3.x;
		
		Point2D p1 = new Point2D();
		Point2D p2 = new Point2D();
		
		for (int currentY = v3.y; currentY > v1.y; currentY--) {
			p1.x = (int) currentx1;
			p1.y = currentY;
			
			//*************************************************
			float d1 = calculateDistance(v1, p1);
			float d2 = calculateDistance(v3, p1);
			
			float p1weight = (float) d2 / (d1+d2);
			float p2weight = (float) d1 / (d1+d2);
			
			p1.c.r = (v1.c.r * p1weight) + (v3.c.r * p2weight);
			p1.c.g = (v1.c.g * p1weight) + (v3.c.g * p2weight);
			p1.c.b = (v1.c.b * p1weight) + (v3.c.b * p2weight);
			//*************************************************
//			if (do_smooth)
//				p1.c = interpolateColor(v1, v3, p1);
//			else
//				p1.c = v1.c;
			
			
			p2.x = (int) currentx2;
			p2.y = currentY;
			//*************************************************
			float d3 = calculateDistance(v2, p2);
			float d4 = calculateDistance(v3, p2);
			
			float p3weight = (float) d4 / (d3+d4);
			float p4weight = (float) d3 / (d3+d4);
			
			p2.c.r = (v2.c.r * p3weight) + (v3.c.r * p4weight);
			p2.c.g = (v2.c.g * p3weight) + (v3.c.g * p4weight);
			p2.c.b = (v2.c.b * p3weight) + (v3.c.b * p4weight);
			//*************************************************
//			if (do_smooth)
//				p2.c = interpolateColor(v2, v3, p2);
//			else
//				p2.c = v1.c;
			
			drawLine(buff, p1, p2);
			
		    currentx1 -= invslope1;
		    currentx2 -= invslope2;
		}
	}

	//Bubble sort function to sort points in ascending Y order
	private static Point2D[] sortPointsByY(Point2D[] points) {
		boolean sorted = false;
		Point2D temp = new Point2D();

		while (!sorted) {
			sorted = true;
			for (int i = 0; i < points.length - 1; i++) {
				if (points[i].y > points[i + 1].y) {
					temp = points[i];
					points[i] = points[i + 1];
					points[i + 1] = temp;
					sorted = false;
				}
			}
		}
		
		return points;
	}

	private static ColorType interpolateColor(Point2D p1, Point2D p2, Point2D mid) {
		Point2D point = new Point2D(mid);
		
		float d1 = calculateDistance(p1, mid);
		float d2 = calculateDistance(p2, mid);
		
		float p1weight = (float) d2 / (d1+d2);
		float p2weight = (float) d1 / (d1+d2);
		
		point.c.r = (p1.c.r * p1weight) + (p2.c.r * p2weight);
		point.c.g = (p1.c.g * p1weight) + (p2.c.g * p2weight);
		point.c.b = (p1.c.b * p1weight) + (p2.c.b * p2weight);
		
		return point.c;
		
	}
	
	
	/////////////////////////////////////////////////
	// for texture mapping (Extra Credit for CS680)
	/////////////////////////////////////////////////
	public static void triangleTextureMap(BufferedImage buff, BufferedImage texture, Point2D p1, Point2D p2,
			Point2D p3) {
		// replace the following line with your implementation
		drawPoint(buff, p3);
	}
}
