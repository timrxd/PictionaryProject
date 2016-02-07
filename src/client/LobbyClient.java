package client;

/**
 * 
 * Class to hold the lobby where all games are found.
 * 
 * @author Kelsey Dramis
 * @version 1.0
 * 
 */

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LobbyClient extends JFrame 
{

	/**
	 * Serial ID 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Panels that make up the lobby GUI
	 */
	private JPanel lobbyPanel, bottomPanel, namePanel, operationPanel, centerPane, displayPanel, joinButtonPanel;
	
	/**
	 * Buttons that enable user to interact with lobby GUI
	 */
	private JButton addButton, exitButton, joinButton;
	
	/**
	 * Labels that identify various parts of the lobby GUI
	 */
	private JLabel name, lobby, lobbyInfo;
	
	/**
	 * String values that contains player name
	 */
	private static String playerName;
	
	/**
	 * JTextarea displays lobby information
	 */
	private JTextArea information;
	
	/**
	 * Various fonts that make up the Lobby GUI
	 */
	private Font font1, font2, font3;
	
	/**
	 * Color is set to a default value of green
	 */
	private final Color GREEN = new Color(224, 255, 228);
	
	/**
	 * thread responsible for client updates
	 */
	private ClientUpdateThread thread;
	
	/**
	 * String to hold the selected game by the player 
	 */
	private String selectedGame;
	
	/**
	 * Array List to hold the list of lobby button 
	 */
	private ArrayList<JButton> lobbies;
	
	 
	/**
	 * Constructor for the LobbyCLient
	 * Sets up the Lobby GUI, and information from existing games
	 */
	public LobbyClient() 
	{
		
		// Initialize lobbies list
		lobbies = new ArrayList<JButton>();
		
		// Multiple fonts to choose from 
		font1 = new Font("Serif", Font.BOLD, 30);
		font2 = new Font("Serif", Font.PLAIN, 18);
		font3 = new Font("SansSerif", Font.ITALIC, 18); 

		// Panel that holds the heading
		namePanel = new JPanel();
		namePanel.setLayout(new BorderLayout());
		namePanel.setBackground(GREEN);

		// Label for the heading 
		name = new JLabel("Welcome to Pictionary!", SwingConstants.CENTER);
		name.setVerticalAlignment(SwingConstants.CENTER);
		name.setFont(font1);
		namePanel.add(name, BorderLayout.NORTH);
		
		// Panel to hold the add lobby and exit buttons 
		operationPanel = new JPanel();
		operationPanel.setBackground(GREEN);
		operationPanel.setLayout(new GridLayout(1, 16));
		
		// Add Lobby Button 
		addButton = new JButton(" Add Lobby ");
		addButton.setFont(font3);
		addButton.setBackground(Color.white);
		addButton.setFocusPainted(false);
		addButton.addActionListener(new ButtonListener());
		
		// Exit Button
		exitButton = new JButton(" Exit Pictionary ");
		exitButton.setFont(font3);
		exitButton.setBackground(Color.white);
		exitButton.setFocusPainted(false);
		exitButton.addActionListener(new ButtonListener());
		
		operationPanel.add(addButton);
		operationPanel.add(exitButton);
		
		// Panel to hold the information about selected lobby 
		displayPanel = new JPanel(new BorderLayout());
		displayPanel.setBackground(GREEN);
		
		joinButtonPanel = new JPanel(new BorderLayout());
		joinButtonPanel.setBackground(GREEN);

		lobbyInfo = new JLabel("              Lobby Information:");
		lobbyInfo.setFont(font1);
		displayPanel.add(lobbyInfo, BorderLayout.NORTH);
		
		information = new JTextArea();
		information.setBorder(BorderFactory.createLineBorder(GREEN, 30));
		information.setEditable(false);
		displayPanel.add(information, BorderLayout.CENTER);
		
		joinButton = new JButton("Join Game");
		joinButton.setFont(font2);
		joinButton.addActionListener(new ButtonListener());
		
		displayPanel.add(joinButtonPanel, BorderLayout.SOUTH);
		joinButtonPanel.add(joinButton, BorderLayout.EAST);
		
		// Panel to hold the existing lobby
		lobbyPanel = new JPanel(new GridLayout(10, 1));
		lobbyPanel.setBackground(GREEN);
		
		lobby = new JLabel("                 Lobby List:     ");
		lobby.setFont(font1);
				
		// Panel to store the operation panel 
		bottomPanel = new JPanel();
		bottomPanel.setBackground(GREEN);
		bottomPanel.add(operationPanel);
		
		// Panel to hold all the panels in the center 
		centerPane = new JPanel();
		centerPane.setBackground(GREEN);
		centerPane.setLayout(new GridLayout(1,0));
		centerPane.add(lobbyPanel, BorderLayout.WEST);
		centerPane.add(displayPanel, BorderLayout.EAST);
		
		// Use a border layout for the applet 
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(namePanel, BorderLayout.NORTH);
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		getContentPane().add(centerPane, BorderLayout.CENTER);
		
		// Start the applet 
		setSize(900, 500);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - getWidth()) / 4);
	    int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);
		setResizable(false);
		setUndecorated(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);		
		
		
		if (playerName == null)
			while (playerName == null) {
				playerName = JOptionPane.showInputDialog("Enter your username: ");
			}
		
		thread = new ClientUpdateThread("pigpen.cs.loyola.edu", 4444);
		thread.setLobbyParent(this);
		thread.sendMessage("INTRO");
		thread.sendMessage(playerName);	
		
	}
	
	/**
	 * Instantiates a new instance of the Lobby Client
	 * @param args
	 */
	public static void main(String [] args)
	{
		new LobbyClient();
	}
	
	/**
	 * Adds connections to various lobby buttons
	 * Enables the creations of new lobbies, prompts user input and category
	 */
	private class ButtonListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if(e.getSource().equals(addButton))
			{
				addLobby();				
			}
			else if(e.getSource().equals(exitButton))
			{
				onClose();
			}
			else if(e.getSource().equals(joinButton))
			{
				thread.sendMessage("JOIN");
				thread.sendMessage(selectedGame);
			}
			else if(! (e.getSource().equals(joinButton) || e.getSource().equals(addButton)
			   || e.getSource().equals(exitButton)) )
			{
				selectedGame = ((JButton)e.getSource()).getText();
				thread.sendMessage("QUERY");
				thread.sendMessage(selectedGame);
			}
			
		}
		
	}
	
	/**
	 * Adds a lobby to the game.
	 * Sets the lobby qualities such as category and name
	 */
	public void addLobby()
	{			
		// All the options
		Object[] possibilities = {"Animals", "Autumn", "Disney", "Food", "LoonyTunes", 
									"Pokemon", "Sports", "Spring", "Summer", "Winter"};
		
		String lobbyName = JOptionPane.showInputDialog("Name of the lobby:");
		if (lobbyName == null) return;
		if (lobbyName.length() == 0) return;
		
		String category = (String) JOptionPane.showInputDialog(null, "Choose a category:",
		        "Category Dialog", JOptionPane.QUESTION_MESSAGE, null, 
		        possibilities, // Array of choices
		        possibilities[5]); // Initial choice, "Choose a category: ");
		
		if (category == null) return;
		
		thread.sendMessage("NEW_LOBBY");
		thread.sendMessage(lobbyName+"\t"+category);
			
	}
	
	/**
	 * Updates the Lobby list every time someone joins the game 
	 * @param s
	 */
	public void updateLobbies(String s) {
		System.out.println("Lobbies: " +s.length()+".");

		
		for (JButton b : lobbies) {
			lobbyPanel.remove(b);
		}
		lobbyPanel.revalidate();		
		
		if (s.length() == 0) {
			
			
			return;
		}
		
		String[] l = s.split("\t");
		for(int x = 0; x < l.length; x++) {
			JButton newLobby = new JButton(l[x]);
			newLobby.addActionListener(new ButtonListener());
			newLobby.setFont(font2);
			lobbyPanel.add(newLobby);
			lobbies.add(newLobby);
			lobbyPanel.revalidate();
			lobbyPanel.repaint();
		}
		
	}
	
	/** 
	 * Start pictionary client and close lobby client
	 */
	public void joinLobby() {
		System.out.println("joined");
		new PictionaryClient(thread);
		this.dispose();
	}
	
	/**
	 * Sets information about game with info from server
	 */
	public void setInformation(String s) {
		information.setText(s);
	}

	/**
	 * Update the thread for the server 
	 * @return ClientUpdateThread
	 */
	public ClientUpdateThread getThread() {
		return thread;
	}
	
	public void onClose()
	{
		thread.endThread();
		dispose();
		System.exit(0);
	}
	
	
}
