package server;

/**
 * 
 * Object to handle processes of an individual game
 * Interacts with list of players
 * 
 * @author Tim Dowd
 * @version 1.0
 * 
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.Scanner;

import client.Shape;

public class GameServer {
	
	// TODO
	// This class handles all the game logic of Pictionary
	// It holds the board state and high scores/guesses
	// It also holds an array of Sockets/Threads of each client to send them information
	
	/**
	 * Array list of Shapes to be able to update the board state
	 */
	private ArrayList<Shape> boardState;
	
	/**
	 * Array list of players to update the player list 
	 */
	private ArrayList<Player> players;
	
	/**
	 * Array list of strings to hold the world list 
	 */
	private ArrayList<String> wordList;
	
	/**
	 * Name of the game
	 */
	private String name;
	
	/** 
	 * Name of the category 
	 *
	 */
	private String category;
	
	/**
	 * Name of the word chosen for the category 
	 */
	private String theWord;
	
	/**
	 * If the game has started or not 
	 */
	private boolean started;
	
	private PictionaryServer server;
	
	/**
	 * Constructor for Game Server to read in from a text file a word bank and get a random word
	 */
	public GameServer(PictionaryServer s) {
		players = new ArrayList<Player>();
		boardState = new ArrayList<Shape>();
		
		wordList = new ArrayList<String>();
				
		server = s;
		started = false;
		
	}
	
	/**
	 * Check if guess is correct
	 * @param p player who submitted guess
	 * @param s String = "<guess>\t<time-stamp>"
	 */
	public void checkGuess(Player g, String s) {
		
		for (Player p : players) {
			p.sendMessage("GUESS");
			p.sendMessage(g.getName()+"\t"+s.split("\t")[0]);
		}
		
		// If yes, handle end of round
		if (theWord.equalsIgnoreCase(s.split("\t")[0])) {
			
			// Award points based on time
			g.addScore(Integer.parseInt(s.split("\t")[1]));
		
			g.sendMessage("WINNER");
			g.sendMessage("You got it! It was " + s.split("\t")[0] + 
					"\nYou scored " + s.split("\t")[1] + " this round");
			
			
			// Make new list of players
			ArrayList<Player> scores = new ArrayList<Player>();
			for (Player ps : players) {
				scores.add(ps);
			}
			// Sort scores
			Collections.sort(scores, new Comparator<Player>() {
				public int compare(Player a, Player b) {
					// Inverted for high scores
					return (-1) * Integer.compare(a.getScore(), b.getScore());
				}				
			});
			// Make a list of the scores
			String scoreboard = "";
			for (Player p : scores) {
				scoreboard += ""+p.getName()+":\t"+p.getScore()+"\n";
			}
			System.out.println(scoreboard);
			
			// Address each player
			for (Player p : players) {
				
				// Notify other players
				if (p != g) {
					p.sendMessage("END_ROUND");
					p.sendMessage(g.getName() + "\t" + theWord);	
				}
				
				// Update everyones score board	
				p.sendMessage("SCORES");
				p.sendMessage(scoreboard);
				p.sendMessage("END_SCORES");
			}
			
			// Start new round process
			newRound();
		
			
			
		}
	}
	
	/**
	 *  Start a new round
	 */
	public void newRound() {
		
		// End the game if no players left
		if (players.isEmpty()) {
			endGame();
			return;
		}
		
		boardState.clear();
		
		// Assign new artist
		ListIterator<Player> i = players.listIterator();
		while (i.hasNext()) {
			Player p = i.next();
			if (p.isArtist()) {
				p.makeGuesser();
				if (i.hasNext()) {
					i.next().makeArtist();
				}
				else {
					players.get(0).makeArtist();
				}
				break;
			}
		}
		
		// Get new random word
		theWord = wordList.get((int)(Math.random()*wordList.size()));
		System.out.println("New word: "+ theWord);
		
		// Send start game notice to player
		// When they respond, start the game with startGame()
		for(Player p: players) {
			if (p.isArtist()) {
				p.sendMessage("WORD");
				p.sendMessage(theWord);
			}
		}
		
		
	}
	
	/**
	 * Starts the game
	 * @param p
	 */
	public void startGame() {
		
		for(Player p : players) {
			if (!p.isArtist()) {
				p.sendMessage("START_GAME");
			}
		}	
	}
	
	/**
	 * Method that is used to add a new player to the thread and decide if they are the artist or 
	 * the guesser. 
	 * @param p
	 */
	public void newPlayer(Player p) {
		players.add(p);	
		
		p.sendMessage("CATEGORY");
		p.sendMessage(category);
		
		System.out.println("players:");
		for (Player e : players) 
			System.out.println(e.getName());
		
		if (players.size() == 1) {
			p.makeArtist();
		}
		else {
			p.makeGuesser();
			p.sendMessage("BOARD");
			for(Shape s : boardState) {
				p.sendMessage(s.toString());
			}			
			p.sendMessage("END_BOARD");
		}
		
		if (!started) {
			if (players.size() >= 2) {
				newRound();
				started = true;
			}
		}
		
	}
	
	/**
	 * Add the shape to the board and update the state of the board based on what the artist is drawing
	 * @param s
	 */
	public void addShape(String s) {
		
		boardState.add(new Shape(s));
		
		// Fancy for loop
		for(Player p: players) {
			if (!p.isArtist()) {
				p.sendMessage("SHAPE");
				p.sendMessage(s);
			}
		}		
	}
	
	/**
	 * Clear the board if the artist is reassigned 
	 */
	public void clearBoard() {
		boardState.clear();
		for(Player p: players) {
			if (!p.isArtist()) {
				p.sendMessage("CLEAR");
			}
		}	
	}
	
	/**
	 * Undo the last addition to the drawing on the board, including when drawing free hand strokes. 
	 * When drawing free hand, undo the entire section from when clicked to no longer clicked.  
	 */
	public void undoBoard() {
		
		//System.out.println("undo");
		if (boardState.size() > 0) {
			
			// Free hand strokes alternate between true and false
			// Delete all same type consecutive frees
			boolean s = boardState.get(boardState.size() - 1).getFill();
			if (boardState.get(boardState.size() - 1).getType().equals("Free")) {				
				while (boardState.size() > 0) {
					if (boardState.get(boardState.size() - 1).getFill() == s && 
						boardState.get(boardState.size() - 1).getType().equals("Free")) {

						boardState.remove(boardState.size() - 1);
					}
					else
						break;
				}
			}
			else {		
				boardState.remove(boardState.size() - 1);
			}
			
		}
		//Undo the board state if the player is no longer the artist. 
		for(Player p: players) {
			if (!p.isArtist()) {
				p.sendMessage("UNDO");
			}
		}	
	}
	
	/**
	 * Remove the player from the game if there is only one player in the game and end the game.
	 * Otherwise, make them the artist of the game. 
	 * @param p
	 */
	public void removePlayer(Player p) {
		System.out.println(players.size());
		if (players.size() <= 1) {
			// End game
			players.remove(p);
			endGame();
			return;
		}
		else if (p.isArtist()) {
			if (players.size()-1 > players.indexOf(p))
				players.get(players.indexOf(p)+1).makeArtist();
			else
				players.get(0).makeArtist();			

		}
		players.remove(p);
		
		
	}
		
	/**
	 * Display the game information including the name of the game and the category 
	 * @return String
	 */
	public String gameInfo() {
		String s = "Game Name: " + name + "\nCategory: " + category + "\n\nPlayers\n";
		for (Player p : players) {
			s += p.getName() + "\n";
		}		
		s += "END_INFO";
		return s;
	}
	
	/**
	 * Return the name of the game 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name
	 * @param s
	 */
	public void setName(String s) {
		name = s;
	}
	
	/**
	 * Set the category
	 * @param s
	 */
	public void setCategory(String s) {
		category = s;
		
		Scanner scan = new Scanner(GameServer.class.getResourceAsStream("/categories/"+
														category.toLowerCase()+".txt"));
		while(scan.hasNext()) 
			wordList.add(scan.nextLine());
		scan.close();
	}
	
	/**
	 * Get the category
	 * @return
	 */
	public String getCategory() {
		return category;
	}
	
	/**
	 * End the game 
	 */
	public void endGame() {
		server.removeGame(this);
	}
	
	// Probably not used
	/**
	 * Create a game to start the game server 
	 * @param args
	 */
	public static void main(String[] args) {
		GameServer game = new GameServer(null);
		game.getName();
	}
	

}
