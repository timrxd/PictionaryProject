package server;

/**
 * 
 * Object to represent a player
 * Handles in/out messages with players client
 * 
 * @author Tim Dowd
 * @version 1.0
 * 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Player {
	
	// Represents a player in Pictionary
	// Handles communication with that player
	
	/**
	 * Socket object 
	 */
	private Socket socket;
	
	/**
	 * Determine if it is the artist 
	 */
	private boolean artist;
	
	/**
	 * Determine if it is the end of the game 
	 */
	private boolean end;
	
	/**
	 * Score of the game 
	 */
	private int score;
	
	/**
	 * Name of the given game 
	 */
	private String name;
	
	/**
	 * Reader for the server 
	 */
	private BufferedReader in;
	
	/**
	 * Writer for the server 
	 */
	private BufferedWriter out;
	
	/**
	 * Create a game server game 
	 */
	private GameServer game;
	
	/**
	 * Pictionary Server object 
	 */
	private PictionaryServer server;
	
	/**
	 * Player constructor to initialize all variables and create a new thread 
	 * @param p
	 * @param s
	 */
	public Player(PictionaryServer p, Socket s) {
		server = p;
		end = false;
		score = 0;
		socket = s;
		artist = false;		
		
		try {
			in 	= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			killPlayer();
			System.out.println("Kill " + name);
		}
	
		// Input thread
		Thread inThread = new Thread(new Runnable()
		{
			public void run(){
			
				playerIn();
			}
		});
		inThread.start();
	
	}
	
	/**
	 * Read in the different options that could happen by the player and perform those operations 
	 * in the game. 
	 */
	public void playerIn() {
		
		String code = "";
		
		while (!end) {
			try {
				
				code = in.readLine();
				//System.out.println(code);
				if (code == null) {
					killPlayer();
					return;
				}
				switch(code) 
				{
				case "SHAPE":
					String shape = in.readLine();
					game.addShape(shape);
					break;
				case "LOBBIES":
					sendMessage("LOBBIES");
					String s = "";
					for(GameServer g : server.getGames()) 
					{
						System.out.println(g.getName());
						s += g.getName()+"\t";
					}
					sendMessage(s);
					break;
				case "CLEAR":
					game.clearBoard();
					break;
				case "UNDO":
					game.undoBoard();
					break;
				case "ARTIST_START":
					game.startGame();
					break;
				case "GUESS":
					game.checkGuess(this, in.readLine());
					break;
				case "INTRO":
					name = in.readLine();
					sendMessage("LOBBIES");
					String sIntro = "";
					for(GameServer g : server.getGames()) {
						System.out.println(g.getName());
						sIntro += g.getName()+"\t";
					}
					sendMessage(sIntro);
					break;
				case "NEW_LOBBY":
					String n = in.readLine();
					
					if (server.addGame(n.split("\t")[0], n.split("\t")[1])) {
						sendMessage("SUCCESS");
						System.out.println("Succeeded");
					}
					else {
						sendMessage("FAILURE");
						System.out.println("Failed");
					}
					break;
				case "QUERY":
					GameServer g = server.getGame(in.readLine());
					if (g != null) {
						sendMessage("GAME_INFO");
						sendMessage(g.gameInfo());
					}
					break;
				case "JOIN":
					String j = in.readLine();
					System.out.println(name + " joins "+j);
					if (server.getGame(j) != null) {
						System.out.println("accepted");
						sendMessage("ACCEPTED");
						addToGame(server.getGame(j));
					}
					else
						sendMessage("REJECTED");
					break;
				case "JOINED":
					addToGame(game);
					break;
				case "LEAVE_GAME":
					game.removePlayer(this);
					break;
				default:
					end = true;
					
				}
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("Error in playerIn : Player");
				killPlayer();
			}
		}
		System.out.println("End");
		killPlayer();

	}
	
	/**
	 * Send a message to the Buffered Writer
	 * @param s
	 */
	public void sendMessage(String s) {
		
		try {

			out.write(s);
		    out.newLine();
		    out.flush();
		    
		} catch (IOException e) {
			System.out.println("Error in sendMessage : Player");
		}
		
	}
	
	/**
	 * If the player is the artist or not 
	 * @return boolean
	 */
	public boolean isArtist() {
		return artist;
	}
	
	/**
	 * Make the player the artist 
	 */
	public void makeArtist() {
		artist = true;
		sendMessage("ARTIST");
		sendMessage("Yes");
	}
	
	/**
	 * Make the player the guesser 
	 */
	public void makeGuesser() {
		artist = false;
		sendMessage("ARTIST");
		sendMessage("No");
	}
	
	/**
	 * Set the name of the game 
	 * @param s
	 */
	public void setName(String s) {
		name = s;
	}
	
	/**
	 * Get the name of the game 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the most recent score 
	 * @return int
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Add a new game to the game server 
	 * @param g
	 */
	public void addToGame(GameServer g) {
		System.out.println(g);
		game = g;
		g.newPlayer(this);
	}
	
	/**
	 * Add a score to the scores list 
	 * @param x
	 */
	public void addScore(int x) {
		score += x;
	}
	
	/**
	 * Kill a player and remove them from the game
	 */
	public void killPlayer() {
		end = true;
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("Error in killPlayer : Player");
		}
		if (game != null)
			game.removePlayer(this);
	}
	

}
