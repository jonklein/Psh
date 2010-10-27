package org.spiderland.Psh.PushBrush;

/**
 * A class for easily storing and passing brush attributes
 * @author Tom
 *
 */
public class BrushAttributes {
	public float x, y, radius, red, green, blue;
	public float t;
	
	public BrushAttributes(){
		x = y = radius = red = green = blue = t = -1;
	}

	public BrushAttributes(float newx, float newy, float newradius, float newr,
			float newg, float newb) {

		x = newx;
		y = newy;
		radius = newradius;
		red = newr;
		green = newg;
		blue = newb;

		t = 0;
	}

	public BrushAttributes(float newx, float newy, float newradius, float newr,
			float newg, float newb, float newt) {

		x = newx;
		y = newy;
		radius = newradius;
		red = newr;
		green = newg;
		blue = newb;
		t = newt;
	}
}
