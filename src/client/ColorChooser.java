package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;
/**
 * Allows Color choosing functionality in the drawing panel.
 * @author Tim Dowd
 * @author Ryan Sharpe
 */
public class ColorChooser extends JPanel {
	
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Main JFrame of the color chooser button
	 */
	private static JFrame mainFrame;
	
	/**
	 * Buttons to choose color or exit
	 */
	private JButton exitButton, confirmButton;
	
	/**
	 * Panels to hold the old color and newly selected color
	 */
	private JPanel oldColor, newColor;
	
	/**
	 * Locations of where the mouse is 
	 */
	private int x, y;
	
	/**
	 * Image to hold the color wheel
	 */
	private BufferedImage spectrum;
	
	/**
	 * Color that is chosen 
	 */
	private Color color;
	
	/**
	 * Old vs. New Color 
	 */
	private static Color colorValueNew, colorValueOld;
	
	/**
	 * Pictionary Client object 
	 */
	public PictionaryClient parent;

	/**
	 * Constructs the Panel that displays the color values
	 * @param pc
	 */
	public ColorChooser(PictionaryClient pc) {
		
		x = 800;
		y = 10;
		color = Color.red;
		parent = pc;
		
		try {
			spectrum = ImageIO.read(PictionaryClient.class.getResource("/res/spectrum_chart.jpg"));
		} catch (IOException e) {
			System.out.println("Cannot upload chart image");
		}
		
		JPanel bottomPanel = new JPanel();
		exitButton = new JButton("Cancel");
		exitButton.addActionListener(new ButtonListener());
		confirmButton = new JButton("OK");
		confirmButton.addActionListener(new ButtonListener());
		
		bottomPanel.add(exitButton);
		bottomPanel.add(confirmButton);
		
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new GridLayout(2,1));
		
		oldColor = new JPanel();
		colorValueOld = parent.getColor();
		oldColor.setBackground(colorValueOld);
		
		newColor = new JPanel();
		colorValueNew = parent.getColor();
		newColor.setBackground((colorValueNew));
		
		sidePanel.add(oldColor);
		sidePanel.add(newColor);
		
		mainFrame = new JFrame();
		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(this, BorderLayout.CENTER);
		mainFrame.add(bottomPanel, BorderLayout.SOUTH);
		mainFrame.add(sidePanel, BorderLayout.EAST);
				
		// Start the applett
		mainFrame.setUndecorated(true);
		mainFrame.setSize(200, 200);
		mainFrame.setLocation(parent.getX()+250, parent.getY()+250);
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setAlwaysOnTop(true);
		
		mainFrame.setVisible(true);
        
        repaint();
        
        /**
         * Mouse listener picks up the values from mouse clicks 
         */
	    addMouseMotionListener(new MouseMotionAdapter() 
	      {
	    	/**
	    	 * Tracks the movement of the mouse to draw temp when active
	    	 * @param m the MouseEvent
	    	 */
	         public void mouseDragged(MouseEvent m)
	            {
	        	 	if (m.getX() >= 10 && m.getX() < 160) {	        	 
	        	 		x = m.getX();
	        	 	}
	        	 	
	        	 	if (m.getY() >= 10 && m.getY() < 160) {
	        	 		y = m.getY();
	        	 	}
	        	 	
	        	 	int code = spectrum.getRGB(x-10, y-10);

	        	 	int  red   = (code & 0x00ff0000) >> 16;
	        	 	int  green = (code & 0x0000ff00) >> 8;
	        	 	int  blue  =  code & 0x000000ff;
	        	 		        	 	
	        	 	color = new Color(red, green, blue);
	        	 	colorValueNew = color;
	        	 	newColor.setBackground(colorValueNew);
	        	 	oldColor.setBackground(colorValueOld);
    	 			repaint();
        	 	}        	 	
	        });
	}
	
	/**
     * Button listener enables opening and closing the color chooser
     */
    private class ButtonListener implements ActionListener
    {
    	/**
    	 * Identifies source button and performs action based on source
    	 * @param e the ActionEvent
    	 */
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(exitButton)) {
            	System.out.println("this");
            	parent.setEnabled(true);
            	parent.setFocusable(true);
            	mainFrame.dispose();
            }	
            else if (e.getSource().equals(confirmButton)) {
            	System.out.println("here");
            	parent.setColor(color);
            	parent.setEnabled(true); 
            	parent.setFocusable(true);
            	mainFrame.dispose();
        	}
        }
    }
	/**
	 * Paint function paints the circle that corresponds 
	 * to where the user is clicking on the panel
	 * @param graphics
	 */
    public void paint(Graphics gr) {

		super.paint(gr);
    	
    	gr.drawImage(spectrum, 10, 10, null);
    	
    	gr.setColor(color);
    	gr.fillOval(x-3, y-3, 6, 6);
    	gr.setColor(Color.black);
    	gr.drawOval(x-4, y-4, 8, 8);
    	gr.setColor(Color.white);
    	gr.drawOval(x-5, y-5, 10, 10);
    	
    }
	
}
