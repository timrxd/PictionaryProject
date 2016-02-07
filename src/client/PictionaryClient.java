/**
 * The Pictionary Client creates the user interface for the Pictionary game. 
 * 
 *
 * @author Tim Dowd
 * @author Kelsey Dramis
 * @author Ryan Sharpe
 * @version 1.0
 */


package client;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.net.URL;

public class PictionaryClient extends JFrame implements KeyListener{
	
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -805902399768140112L;
	
	/**
	 * Space filler
	 */
	private final String space = "                    ";
	
	/**
	 * Timer to countdown how many seconds until the game is over 
	 */
	private Timer clock;
	
	/**
	 * Boolean of if the player is artist or not 
	 */
	private boolean artist;
	
	/**
	 * Shape of the object to be drawn
	 */
	private String shape, category;
	
	/**
	 * Color of shape to to be drawn
	 */
	private Color color;
	
	/**
	 * Board that displays the picture and allows artist to interact with picture
	 */
	private DrawControl board;
	
	/** 
	 * Thread that runs the client processes
	 */
	private ClientUpdateThread clientThread;
	

	/**
	 * JPanels, that hold either artist, guesser, or general attributes
	 */
	private JPanel artistPanel, guesserPanel, bottomPanel;	
	
	/**
	 * Buttons that connect to various of components of the program
	 */
	private JButton submitButton, lineButton, rectButton, circleButton, freeButton, colorButton,
					clearButton, undoButton, lobbyButton;
	
	/**
	 * TextAreas that display score data and guess history
	 */
	private JTextArea scoresText, guessesText;

	/**
	 * ScrollPane that allows user to scroll through guess history
	 */
	private JScrollPane scroll;
	
	/**
	 * Label that displays the word that the artist is drawing and guessers guessing
	 */
	private JLabel theWord;
	
	/**
	 * The textfield that the guesser writes into
	 */
	private JTextField promptText;
	
	/**
	 * Color is set to a default value of blue 
	 */
	private final Color BLUE = new Color(210, 245, 255);
	
	/**
	 * Color is set to a default value of salmon
	 */
	private final Color SALMON = new Color(250, 231, 225);
	
	/**
	 * Color is set to a default value of yellw
	 */
	private final Color YELLOW = new Color(255, 239, 201);
	
	/**
	 * Color is set to a default value of green 
	 */
	private final Color GREEN = new Color(224, 255, 228);
	
	/**
	 * Constructor for PictionaryClient class 
	 * Sets up the various JPanels, and GUI elements
	 */
	public PictionaryClient(ClientUpdateThread t) {
		
		super("Pictionary");		
		
		shape = "Free";
		color = Color.black;
		
		// Side panel
		// Holds all drawing options ie. shape, color, fill
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new GridLayout(2,1));
		
		// Panel that holds the high scores of each player
		JPanel scoresPanel = new JPanel();
		scoresPanel.setLayout(new BorderLayout());

		JLabel scoresLabel = new JLabel(space+"   Scoreboard"+space);
		scoresPanel.add(scoresLabel, BorderLayout.NORTH);
		
		scoresText = new JTextArea();		
		scoresText.setEditable(false);
		scoresText.setBackground(SALMON);
		scoresText.setMargin( new Insets(10,10,10,10) );
		scoresPanel.add(scoresText, BorderLayout.CENTER);
		readScores(""); //Placeholder
		
		sidePanel.add(scoresPanel);		
		
		// Panel that holds the previous guesses in the round
		JPanel guessesPanel = new JPanel();
		guessesPanel.setLayout(new BorderLayout());
		JLabel guessLabel = new JLabel(space+"Previous Guesses"+space);
		guessesPanel.add(guessLabel, BorderLayout.NORTH);		
		
		guessesText = new JTextArea();		
		guessesText.setEditable(false);
		guessesText.setBackground(BLUE);
		guessesText.setMargin( new Insets(10,10,10,10) );
		
		scroll = new JScrollPane(guessesText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(100,100));
		scroll.setVisible(true);
		guessesPanel.add(scroll, BorderLayout.CENTER);

		guessesText.setText("\n");
		sidePanel.add(guessesPanel);
		
		// Bottom panel to hold drawing tools / guess box
		bottomPanel = new JPanel();
		bottomPanel.setBackground(YELLOW);
		
		// Two panels can fill bottom panel depending on artist/guesser state
		// Guesser Panel:
		guesserPanel = new JPanel();
		guesserPanel.setBackground(YELLOW);
		promptText = new JTextField(20);
		promptText.addKeyListener(this);
		submitButton = new JButton("Submit Guess");
		submitButton.addActionListener(new ButtonListener());
		guesserPanel.add(promptText);
		guesserPanel.add(submitButton);
		guesserPanel.setVisible(false);
		
		// Artist Panel:
		artistPanel = new JPanel();
		artistPanel.setBackground(YELLOW);
		artistPanel.setLayout(new GridLayout(1, 16));
		
		freeButton = new JButton();
		URL squiggle = PictionaryClient.class.getResource("/res/squiggle.png");
		freeButton.setIcon(new ImageIcon(squiggle));
		freeButton.setBackground(Color.white); 
		freeButton.setFocusPainted(false); 
		freeButton.addActionListener(new ButtonListener());
		freeButton.setBackground(Color.black);
		
		lineButton = new JButton();
		URL line = PictionaryClient.class.getResource("/res/line.png");
		lineButton.setIcon(new ImageIcon(line));
		lineButton.setBackground(Color.white); 
		lineButton.setFocusPainted(false); 
		lineButton.addActionListener(new ButtonListener());

		rectButton = new JButton();
		URL rect = PictionaryClient.class.getResource("/res/rectangle.png");
		rectButton.setIcon(new ImageIcon(rect));
		rectButton.setBackground(Color.white); 
		rectButton.setFocusPainted(false); 
		rectButton.addActionListener(new ButtonListener());
		
		circleButton = new JButton();
		URL circle = PictionaryClient.class.getResource("/res/circle.png");
		circleButton.setIcon(new ImageIcon(circle));
		circleButton.setBackground(Color.white); 
		circleButton.setFocusPainted(false); 	
		circleButton.addActionListener(new ButtonListener());
		
		colorButton = new JButton();
		URL color = PictionaryClient.class.getResource("/res/color.png");
		colorButton.setIcon(new ImageIcon(color));
		colorButton.setBackground(Color.white); 
		colorButton.setFocusPainted(false); 	
		colorButton.addActionListener(new ButtonListener());
		
		clearButton = new JButton();
		URL clear = PictionaryClient.class.getResource("/res/trash.png");
		clearButton.setIcon(new ImageIcon(clear));
		clearButton.setBackground(Color.white); 
		clearButton.setFocusPainted(false); 	
		clearButton.addActionListener(new ButtonListener());

		undoButton = new JButton();
		URL undo = PictionaryClient.class.getResource("/res/undo.png");
		undoButton.setIcon(new ImageIcon(undo));
		undoButton.setBackground(Color.white); 
		undoButton.setFocusPainted(false); 	
		undoButton.addActionListener(new ButtonListener());

		artistPanel.add(freeButton);
		artistPanel.add(lineButton);
		artistPanel.add(rectButton);	
		artistPanel.add(circleButton);
		artistPanel.add(new JPanel());
		artistPanel.add(colorButton);
		artistPanel.add(new JPanel());
		artistPanel.add(undoButton);
		artistPanel.add(clearButton);	
		artistPanel.setVisible(false);
		
		bottomPanel.add(artistPanel);
		bottomPanel.add(guesserPanel);
		setArtist();
		//!! ^^ change this for guesser client
		
		// Top panel to hold the timer/word/etc.
		final JPanel topPanel = new JPanel();
		topPanel.setBackground(GREEN);
		topPanel.setLayout(new BorderLayout());
		theWord = new JLabel("   Waiting for players...   ");
		theWord.setFont(new Font("Serif", Font.BOLD, 28));
		theWord.setBorder(BorderFactory.createLineBorder(Color.black, 3));
		JPanel wordPanel = new JPanel();
		wordPanel.add(theWord);
		wordPanel.setBackground(GREEN);
		topPanel.add(wordPanel);
		
		//Start timer thread
		try 
		{
			clock = new Timer();
			//clock.
			topPanel.add(clock, BorderLayout.EAST);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		} 

		lobbyButton = new JButton("↩ Lobby");
		lobbyButton.addActionListener(new ButtonListener());
		lobbyButton.setBackground(GREEN);
		
		topPanel.add(lobbyButton, BorderLayout.WEST);
		
		// Create a DrawControl that handles all drawing functions
		board = new DrawControl(this);	
		// board.loadHouse();
		
		// Center panel for drawing pane and bottom panel
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());	
		centerPanel.add(board, BorderLayout.CENTER);
		centerPanel.add(bottomPanel, BorderLayout.SOUTH);
		centerPanel.add(topPanel, BorderLayout.NORTH);
		
		// Use border layout for applet
		getContentPane().setLayout(new BorderLayout());
			
		// Expands to fill space in center
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		
		// Add the side panel
		getContentPane().add(sidePanel, BorderLayout.EAST);
		
		// Start the program
		setSize(900, 500);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - getWidth()) / 4);
	    int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        
        clientThread = t;
        if (clientThread == null) {
			clientThread = new ClientUpdateThread("snoopy.cs.loyola.edu", 4444);
			clientThread.setParent(this);
		}
		else {
			clientThread.setParent(this);
		}
        
        //clientThread.sendMessage("JOINED");
        
	}
	
	/**
     * Event handler for the clear, undo, save, and load buttons.
     */
    private class ButtonListener implements ActionListener
    {
    	/**
    	 * Identifies source button and performs action based on source
    	 * 
    	 * @param e the ActionEvent
    	 */
        public void actionPerformed(ActionEvent e)
        {
        	if (e.getSource().equals(rectButton)) {
        		shape = "Rect";
        		resetButtons();
        		rectButton.setBackground(Color.black);
        	}
        	else if (e.getSource().equals(circleButton)) {
        		shape = "Oval"; 
        		resetButtons();
        		circleButton.setBackground(Color.black);       		
        	}
        	else if (e.getSource().equals(lineButton)) {
        		shape = "Line"; 
        		resetButtons();
        		lineButton.setBackground(Color.black);       		
        	}
        	else if (e.getSource().equals(freeButton)) {
        		shape = "Free"; 
        		resetButtons();
        		freeButton.setBackground(Color.black);       		
        	}
        	else if (e.getSource().equals(colorButton)) {
        		getNewColor();
        	}
        	else if (e.getSource().equals(clearButton)) {
        		clearBoard();
        		clientThread.sendMessage("CLEAR");
        	}
        	else if (e.getSource().equals(undoButton)) {
        		board.undoDraw();
        		clientThread.sendMessage("UNDO");
        	}
        	else if (e.getSource().equals(lobbyButton)) {
        		clientThread.sendMessage("LEAVE_GAME");
        		onClose();
        		new LobbyClient();
        		
        	}
        	else if (e.getSource().equals(submitButton)) {
        		submitGuess();
        	}
        }
    }
    

    /**
     * Prints the guess onto the guess history
     * checks if the guess is equal to the active word
     * If user got guess correct, approval message appears
     */
    public void submitGuess() {
  		//newGuess("Snoopy:\t"+promptText.getText());
  		//clientThread.sendMessage(promptText.getText());
  		
  		if (promptText.getText().toLowerCase().equals("house"))
  			JOptionPane.showMessageDialog(board, "You got it!", "Correct", JOptionPane.PLAIN_MESSAGE);
  		//clear text after submission
  		
  		clientThread.sendMessage("GUESS");
  		clientThread.sendMessage(promptText.getText().toLowerCase() +"\t" + clock.getTime());
  		
  		promptText.setText("");
    }
    /**
     * Starts the applet
     * 
     * @param args no arguments necessary
     */
	public static void main(String[] args) {
		new PictionaryClient(null);		
	}
	
	/**
	 * Retrieves new color by enabling the ColorChooser panel
	 */
	public void getNewColor() {
		this.setEnabled(false);
		this.setFocusable(false);
		new ColorChooser(this);
	}
	
	/**
	 * Checks shape button group and returns shape choice
	 * 
	 * @return "Rect" | "Oval" | "Line" | "Free"
	 */
	public String getShapeType() {
		return shape;
	}
	
	/**
	 * Checks color button group and returns color choice
	 * 
	 * @return Color 
	 */
	public Color getColor() {
		return color;
	}
	
	 /** Checks fill button group and returns fill choice
	 * 
	 * @return boolean
	 */
	public boolean getFill() {
		return true;
	}
	
	/**
	 * 
	 * @param c new color for shapes
	 */
	protected void setColor(Color c) {
		color = c;
	}
	
	/**
	 * Get the x position of the color button
	 * @return int x
	 */
	public int getColorX() {
		return colorButton.getX();
	}
	
	/**
	 * Get the y position of the color button
	 * @return int y
	 */
	public int getColorY() {
		return colorButton.getY();
	}
	
	/**
	 * Reset all buttons to white backgrounds
	 */
	public void resetButtons() {
		rectButton.setBackground(Color.white);
		circleButton.setBackground(Color.white);
		lineButton.setBackground(Color.white);
		freeButton.setBackground(Color.white);
	}
	
	/**
	 * Read in high scores list from [server]
	 */
	public void readScores(String scores) { 
		scoresText.setText(scores);
	}
	
	/**
	 * Read in guesses and add them to the history
	 */
	public void addNewGuess(String guess) {
		guessesText.setText(guessesText.getText() + "\n" + guess);
	}

	/**
	 * Detects if the window is closing. Destroys all associated threads
	 * @param e - WindowEvent
	 */
	public void processWindowEvent(WindowEvent e)
	{
		//get rid of all threads
		if(e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			System.out.println("Close");
			//onClose();
			System.exit(0);
		}	
	}
	
	/**
	 * The textfield retrieves the guess from guesser
	 * @return string value containing the guess
	 */
	public String getGuess() {
		return promptText.getText();
	}
	
	/**
	 * Sets the panel to be associated with the artist
	 */
	public void setArtist() {
		//bottomPanel.add(artistPanel);
		guesserPanel.setVisible(false);
		artistPanel.setVisible(true);
		artist = true;
	}
	
	/**
	 * Sets the panel to be associated with a guesser
	 */
	public void setGuesser() {
		//bottomPanel.add(guesserPanel);
		theWord.setText(category);
		artistPanel.setVisible(false);
		guesserPanel.setVisible(true);
		artist = false;
	}
	
	public boolean isArtist() {
		return artist;
	}
	
	/**
	 * Clears the board of all drawings
	 */
	public void clearBoard() {
		board.clearDraw();
	}
	
	
	/**
	 * Reverts to the board to it's last drawn state
	 */
	public void undoBoard() {
		board.undoDraw();
	}
	
	
	/**
	 * Updates the board to add the shape 
	 * @param s
	 */
	public void updateBoard(String s) {
		board.addShape(s);
	}
	
	/**
	 * Sets the word that player will be drawing, and guessers will 
	 * have to guess
	 * @param s - the word
	 */
	public void setWord(String s) {
		theWord.setText(s);
	}
	
	public void setCategory(String s) {
		category = s;
	}
	
	/**
	 * Resets the timer
	 */
	public void resetTimer() {
		clock.resetTime();
	}
	
	/**
	 * Resume the timer
	 */
	public void startTimer() {
		clock.resumeTime();
	}
	
	/**
	 * Pause the timer
	 */
	public void stopTimer(){
		clock.pauseTime();
	}

	/**
	 * Ends the timer
	 * Ends the client thread
	 */
	public void onClose()
	{
		clock.endTimer();
		clientThread.endThread();
		dispose();
	}
	
	/**
	 * Uploads shape to the client thread
	 * @param s - string value representing the shape
	 */
	public void uploadShape(String s) {
		clientThread.sendMessage("SHAPE");
		clientThread.sendMessage(s);
	}
	
	//Key Listener methods
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub	
	}

	/**
	 * Once guesser hits enter, the guess is sent to guessHandle, where
	 * it will be posted to the guess history and and checked if correct
	 * @param e - Enter key
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		  if(e.getKeyCode() == KeyEvent.VK_ENTER)
          {
			  submitGuess();
          }
	   }
	
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
