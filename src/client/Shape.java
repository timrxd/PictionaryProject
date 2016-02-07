package client;

import java.awt.*;
import java.io.Serializable;
/**
 * An object class to represent a shape drawn onto the panel
 * 
 * @author tdowd
 * @version 1.0, 10/19/15
 *
 */
public class Shape implements Serializable{
	
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 5254405392232039185L;
	
	/**
	 *  The type of shape: Rect, Oval, Line, or Free
	 */
	private String type;
	
	/**
	 *  Whether or not the shape is filled 
	 */
	private boolean fill;
	
	/**
	 *  The color of the shape
	 */
	private Color color;
	
	/**
	 *  The coordinates of the shape
	 */
	int x1, y1, x2, y2;
	
	/**
	 * Constructor for the Shape object
	 * 
	 * @param t the type of the shape
	 * @param f the fill of the shape
	 * @param a the x coordinate of the first click
	 * @param b the y coordinate of the first click
	 * @param c the x coordinate of the second click
	 * @param d the y coordinate of the second click
	 * @param col the color of the shape
	 */
	public Shape(String t, boolean f, int a, int b, int c, int d, Color col) {
		
		// Assign data members their values
		type = t;
		fill = f;
		x1 = a;
		y1 = b;
		x2 = c;
		y2 = d;
		color = col;		
	}
	
	/**
	 * Creates a shape based on string information
	 * @param string containing information on shape
	 */
	public Shape(String s) {
		String[] array = s.split(" ");
		
		type = array[0];
		fill = (Integer.parseInt(array[1]) == 1);
		x1 = (Integer.parseInt(array[2]));
		y1 = (Integer.parseInt(array[3]));
		x2 = (Integer.parseInt(array[4]));
		y2 = (Integer.parseInt(array[5]));

	 	int  red   = ((Integer.parseInt(array[6])) & 0x00ff0000) >> 16;
	 	int  green = (Integer.parseInt(array[6]) & 0x0000ff00) >> 8;
	 	int  blue  =  (Integer.parseInt(array[6])) & 0x000000ff;
	 	
	 	color = new Color(red, green, blue);
			
	}
	
	
	/**
	 * Returns string information of a shape
	 */
	public String toString() {
		String s = "";
		
		s += type + " ";
		s += (fill ? 1:0) + " ";
		s += x1 + " " + y1 + " " + x2 + " " + y2  + " ";
		s += color.getRGB();
		
		return s;
		
	}
	
	/**
	 * Have the shape draw itself based on it's properties, <br>
	 * onto the context of the Graphics parameter
	 * 
	 * @param g the Graphics context
	 */
	public void drawShape(Graphics g) {
		
		// Set the color
		g.setColor(color);
		
		// Draw based on shape type
		// For rect/oval, smaller x & y's need to go first, then width/height
		// Lines are just pairs of coordinates
		// Freehand is a line from the current mouse position to the last mouse position
		if (type.equals("Rect")) {
			if (fill)
				g.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1-x2), Math.abs(y1 - y2));
			else
				g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1-x2), Math.abs(y1 - y2));		
		}
		else if (type.equals("Oval")) {
			if (fill)
				g.fillOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1-x2), Math.abs(y1 - y2));
			else
				g.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1-x2), Math.abs(y1 - y2));			
		}
		else if (type.equals("Line")) {
			g.drawLine(x1,y1,x2,y2);
			
		}
		else if (type.equals("Free")) {
			g.drawLine(x1, y1, x2, y2);
		}		
	}	
	
	/**
	 * Return if the shape is filled 
	 * @return String
	 */
	public boolean getFill() {
		return fill;
	}
	
	/**
	 * Return the type of shape it is 
	 * @return String
	 */
	public String getType() {
		return type;
	}
}
