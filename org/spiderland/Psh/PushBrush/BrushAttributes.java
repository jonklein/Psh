package org.spiderland.Psh.PushBrush;

/**
 * A class for easily storing and passing brush attributes
 * @author Tom
 *
 */
public class BrushAttributes {
	public int x, y, radius, red, green, blue;
	public int t;
	
	public BrushAttributes(){
		x = y = radius = red = green = blue = t = -1;
	}

	public BrushAttributes(int newx, int newy, int newradius, int newr, int newg,
			int newb) {
		
		x = newx;
		y = newy;
		radius = newradius;
		red = newr;
		green = newg;
		blue = newb;
		
		t = 0;
	}
	
	public BrushAttributes(int newx, int newy, int newradius, int newr, int newg,
			int newb, int newt) {
		
		x = newx;
		y = newy;
		radius = newradius;
		red = newr;
		green = newg;
		blue = newb;
		t = newt;
	}
}
