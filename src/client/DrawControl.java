package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A JPanel that handles any drawing functions.<br>
 * An array list of Shape objects is used to store the image.<br>
 * Options for Shape are shape/fill/coordinates/color.<br>
 * Uses mouse input, click 1 to activate, click 2 to place shape
 * 
 * @author tdowd
 * @version 0.9, 10/15/15
 *
 */
public class DrawControl extends JPanel{
	
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -4630121447209049571L;

	/**
	 *  Holds shapes in order the were drawn
	 */
	private ArrayList<Shape> model;
	
	/**
	 *  Coordinates of click 1
	 */
	private int x, y;
	
	/**
	 *  Temporary shape that is drawn while dragging 
	 */
	private Shape temp;
	
	/**
	 *  Invisible shape for when temp is not in use
	 */
	private Shape none = new Shape("rect", false, 0,0,0,0,Color.black);
	
	/**
	 *  Container for DrawControl panel, allows access to shape/fill/color options
	 */
	private PictionaryClient window;
	
	/**
	 * Binary difference between freehand strokes to allow for undo
	 */
	private boolean stroke;
	
	/**
	 * Constructor for DrawControl<br>
	 * Takes the SimpleDraw window as argument<br>
	 * Allows it to access shape/fill/color options<br>
	 * 
	 * @param w the parent SimpleDraw window
	 */
	public DrawControl(PictionaryClient w) {
		
		// Start with blank background
		this.setBackground(Color.white);
		
		// Hold onto container for later use
		window = w;
		
		// Make an Images folder for save/load
		(new File(System.getProperty("user.dir")+"/Images")).mkdirs();
		
		// Initialize data values
		model = new ArrayList<Shape>();
		x = 0;
		y = 0;
		temp = none;
		
		// Listener to track user mouse clicks
		addMouseListener(new MouseAdapter()
	    {
			/**
			 * Action to take on mouse pressed down
			 * 
			 * @param m the MouseEvent
			 */
	        public void mousePressed(MouseEvent m)
	        {
	        	if (window.isArtist()) {
	            	x = m.getX();
	            	y = m.getY();   
		            repaint();	        	
	        	}
	        }	      
	        
	        /**
			 * Action to take on mouse released
			 * 
			 * @param m the MouseEvent
			 */
	        public void mouseReleased(MouseEvent m)
	        {
	        	if (window.isArtist()) {       		
	        		addShape(x, y, m.getX(), m.getY());
	        		if(window.getShapeType().equals("Line")) {
	        			int xMod = 0, yMod = 0;
	    	 			if (Math.abs(x-m.getX()) > Math.abs(y-m.getY()))
	    	 				yMod = 1;
	    	 			else
	    	 				xMod = 1;
	    	 			
	    	 			addShape(x+xMod, y+yMod, m.getX()+xMod, m.getY()+yMod);
	    	 			addShape(x-xMod, y-yMod, m.getX()-xMod, m.getY()-yMod);
	        		}
	            	temp = none;
	            	
	            	if (window.getShapeType().equals("Free"))
	            		stroke = !stroke;
		        	
	            	repaint();	
	        	}
	        }	      
	      });
	
		// Listener to track movement of mouse
	    addMouseMotionListener(new MouseMotionAdapter()
	      {
	    	/**
	    	 * Tracks the movement of the mouse to draw temp when active
	    	 * @param m the MouseEvent
	    	 */
	         public void mouseDragged(MouseEvent m)
	            {
	        	 	if (window.isArtist()) {
		        	 	// For freehand, place shapes permanently 
	        	 		if (window.getShapeType() == "Free" ) {
	        	 			addShape(x, y, m.getX(), m.getY());
	        	 			
	        	 			int xMod = 0, yMod = 0;
	        	 			if (Math.abs(x-m.getX()) > Math.abs(y-m.getY()))
	        	 				yMod = 1;
	        	 			else
	        	 				xMod = 1;
	        	 			
	        	 			addShape(x+xMod, y+yMod, m.getX()+xMod, m.getY()+yMod);
	        	 			addShape(x-xMod, y-yMod, m.getX()-xMod, m.getY()-yMod);
	        	 			
	        	 			x = m.getX();
	        	 			y = m.getY();
	        	 		}
	        	 		// Otherwise the temp shape will be overwritten by 'none' when not in use
	        	 		else 
	        	 			temp = new Shape(window.getShapeType(),window.getFill(), x, y, m.getX(), m.getY(), window.getColor());
	        	 		// Update panel
	        	 		repaint();
	        	 	}
        	 	}        	 	
	            
	        });
		
	}
	
	/**
	 * Add a shape to the top of the model<br>
	 * Asks the containing window for the shape type/fill/color
	 * 
	 * @param x1 coordinate from first click
	 * @param y1 coordinate from first click
	 * @param x2 coordinate from second click
	 * @param y2 coordinate from second click
	 */
	public void addShape(int x1, int y1, int x2, int y2) {
		
		// Add the shape to the model list	
		//boolean f = (window.getShapeType().equals("Free") ? stroke : window.getFill());
		Shape newShape = new Shape(window.getShapeType(),
							(window.getShapeType().equals("Free") ? stroke : window.getFill())
							,x1,y1,x2,y2,window.getColor());
		model.add(newShape);
		window.uploadShape(newShape.toString());
		//System.out.println(newShape.toString());
		
	}
	/**
	 * Repaints drawing with a new specified shape
	 * @param s - string containing shape information
	 */
	public void addShape(String s) {
		model.add(new Shape(s));
		repaint();
	}
	
	/**
	 * Method that draws everything on the panel
	 * @param Graphics g
	 */
	public void paint(Graphics g) {
		
		super.paint(g);
	
		// Clear the panel 
		g.setColor(Color.white);
		g.drawRect(0, 0, this.getWidth(), this.getHeight());
	
		// Draws each shape in image
		for (int i = 0; i < model.size(); i++) {
			model.get(i).drawShape(g);		
		}		
		// Draw the current trace shape
		temp.drawShape(g);		
	}
	
	/**
	 * Clear the image
	 */
	public void clearDraw() {
		model.clear();
		repaint();		
	}
	
	/**
	 * Deletes the last shape drawn, if there was one
	 */
	public void undoDraw() {
		if (model.size() > 0) {
			
			// Free hand strokes alternate between true and false
			// Delete all same type consecutive frees
			boolean s = model.get(model.size() - 1).getFill();
			if (model.get(model.size() - 1).getType().equals("Free")) {				
				while (model.size() > 0) {
					if (model.get(model.size() - 1).getFill() == s && 
						model.get(model.size() - 1).getType().equals("Free")) {

						model.remove(model.size() - 1);
					}
					else
						break;
				}
			}
			else {		
				model.remove(model.size() - 1);
			}
			
		}
		repaint();		
	}
	
	/**
	 * Save the image model as a binary file
	 * 
	 * @throws IOException 
	 */
	public void saveImage() throws IOException  {
		
		// Create file selector to let user save to target destination
		JFileChooser fileSelect = new JFileChooser(System.getProperty("user.dir")+"/Images");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("SimpleDraw Images", "sdr");
		fileSelect.setFileFilter(filter);
		fileSelect.setApproveButtonText("Save");
		
		// Supply default image name for save file
		int num = 1;
		while ( (new File(System.getProperty("user.dir")+"/Images/image"+num+".sdr")).exists()) {
			num++;
		}
		fileSelect.setSelectedFile(new File(System.getProperty("user.dir")+"/Images/image"+num+".sdr"));
		
		// Only save if user choose "save"
		int returnVal = fileSelect.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			
			
				// Make sure the extension is there
				String name = fileSelect.getSelectedFile().getName();
				String ext = ".sdr";
				if (name.length() > 5) {
					if (".sdr".equals(name.substring(name.length()-4))) {
						ext = "";
					}
				}
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this,"Overwrite "+name+ext+"?",
					"File Exists", JOptionPane.WARNING_MESSAGE)) {
			
				// Write the model ArrayList to the .sdr file
				FileOutputStream saveFile = new FileOutputStream(fileSelect.getSelectedFile().getAbsolutePath()+ext);
				ObjectOutputStream out = new ObjectOutputStream(saveFile);
				out.writeObject(model);
				out.close();
				saveFile.close();			
			}
		}
	}
	
	/**
	 * Loads the image from a binary file
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public void loadImage() throws IOException, ClassNotFoundException {
		
		// Create file selector to let user save to target destination
		JFileChooser fileSelect = new JFileChooser(System.getProperty("user.dir")+"/Images");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("SimpleDraw Images", "sdr");
		fileSelect.setFileFilter(filter);
		fileSelect.setApproveButtonText("Load");
		
		// Open dialog to get file selection
		int returnVal = fileSelect.showOpenDialog(this);
		// Only process if 'Load' is chosen
		if(returnVal == JFileChooser.APPROVE_OPTION ) {
			
			// Make sure it's a valid file
			String name = fileSelect.getSelectedFile().getName();
			if (name.length() < 5) {
				JOptionPane.showMessageDialog(this,"Please select a valid .sdr file.","File Not Found", JOptionPane.ERROR_MESSAGE);
			}
			else if (fileSelect.getSelectedFile().exists() 
					&& ".sdr".equals(name.substring(name.length()-4))) {
				
				// Read in image model from a .sdr file
				FileInputStream loadFile = new FileInputStream(fileSelect.getSelectedFile().getAbsolutePath());
				ObjectInputStream in = new ObjectInputStream(loadFile);
				model = (ArrayList<Shape>) in.readObject();
				in.close();
				loadFile.close();		
				System.out.println("in");
				repaint();
			}
			else
				JOptionPane.showMessageDialog(this,"Please select a valid .sdr file.","File Not Found", JOptionPane.ERROR_MESSAGE);
		}		
	}
	
	/**
	 * Overloaded loadImage to load a designated image
	 * @param s
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public void loadHouse() {
		
		try {
			FileInputStream loadFile = new FileInputStream("/home/tdowd/Desktop/PictionaryProject/Images/house.sdr");
			ObjectInputStream in = new ObjectInputStream(loadFile);
			model = (ArrayList<Shape>) in.readObject();
			in.close();
			loadFile.close();		
			System.out.println("in");
			repaint();
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * A list of shapes that is set to another list.
	 * This list is then repainted with shapes in arrayList
	 * @param a - an arrayList of shapes that is set to another list 
	 */
	public void loadArray(ArrayList<Shape> a) {		
		model = a;		
		repaint();
	}

}
