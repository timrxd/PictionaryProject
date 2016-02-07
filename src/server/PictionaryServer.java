/**
 * The PictionaryServer class sets up the server that allows the client connect different users 
 * interact with each in order to have a functional Pictionary Game. 
 * 
 */

package server;


import java.net.*;
import java.util.ArrayList;
import java.io.*;


public class PictionaryServer implements Runnable
{  
	/** 
	 * Socket server object 
	 */
	private ServerSocket     server = null;
	
	/**
	 * Thread object 
	 */
	private Thread       thread = null;
	
	/**
	 * Input stream for the server 
	 */
	private DataInputStream streamIn =  null;
	//private DataOutputStream streamOut = null;
	
	/**
	 * Socket object 
	 */
	private Socket socket = null;
	
	/**
	 * Array List of games 
	 */
	private ArrayList<GameServer> games;

	/**
	 * Pictionary Server constructor to set the game and start the connection
	 * @param port
	 */
	public PictionaryServer(int port) {  
		
		games = new ArrayList<GameServer>();
	    addGame("Default", "Animals");
		
		
	   try {  
		   System.out.println("Binding to port " + port + ", please wait  ...");
		   server = new ServerSocket(port);  
		   System.out.println("Server started: " + server);
		   start();
	   }
      catch(IOException ioe) {  
    	  System.out.println(ioe);
    	  System.out.println("ServerSocket Error");
      }
   }
	/**
	 * Run the server and wait for players to join the game, if no one joins through an exception	
	 */
	public void run() {  
		while (thread != null) {  
			try {  
				System.out.println("Waiting for a players ..."); 
				addThread(server.accept());
			}
			catch(IOException ie) {  
				System.out.println("Acceptance Error: " + ie); }
		}

	}
   
	/**
	 * Add a thread once a player joins the game
	 * @param s
	 */
	public void addThread(Socket s)   {  
		System.out.println("Player found: " + s);
		new Player(this, s);
      
		//games.get(0).newPlayer(client);
	}
   
	/**
	 * Add a game to the server and have it updated when a new player joins. 
	 * @param _name
	 * @param _catg
	 * @return boolean 
	 */
	public boolean addGame(String _name, String _catg) {
		for (GameServer g : games) {
			if (g.getName().equals(_name)) {
				return false;
			}
		}
	   
		GameServer game = new GameServer(this);
		game.setName(_name);
		game.setCategory(_catg);
	   
		games.add(game);
		System.out.println(game);
	   
		return true;
	}

	/**
	 * Add a game if that option is chosen 
	 */
	public void addGame() {
		GameServer game = new GameServer(this);
		games.add(game);
	}
   
	/**
	 * Start the game
	 */
	public void start()   {  
		if (thread == null) {  
			thread = new Thread(this); 
			thread.start();
		}
	}
   
	/**
	 * Stop the threads to end the game 
	 */
	public void stop()   {  
		if (thread != null) {  
			//thread.stop(); 
			thread = null;
		}
	}

	/**
	 * Open the socket 
	 * @throws IOException
	 */
	public void open() throws IOException  {  
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	}
   
	/**
	 * Close the socket 
	 * @throws IOException
	 */
	public void close() throws IOException   {  
		if (socket != null)    
			socket.close();
		if (streamIn != null)  
			streamIn.close();
	}
   
	/**
	 * Main method to open the port 
	 * @param args
	 */
	public static void main(String args[])   {  
		if (args.length != 1)
			System.out.println("Usage: java PictionaryServer port");
		else {
			new PictionaryServer(Integer.parseInt(args[0]));
		}
	}
  
	/**
	 * Get the list of games that are being used 
	 * @return ArrayList<GameServer>
	 */
	public ArrayList<GameServer> getGames() {
		return games;
	}
   
	/**
	 * Get the new game created and add it to the Game Server 
	 * @param s
	 * @return Game Server 
	 */
	public GameServer getGame(String s) {
		for (GameServer g : games) {
			if (g.getName().equals(s))
				return g;
		}
		return null;
	}
	
	public void removeGame(GameServer g) {
		games.remove(g);
	}

	/**
	 * Get the information about the given game 
	 * @return String
	 */
	public String getGameInfo() {
		String s = "";
		for (GameServer g : games) {
			s += g.getName() + " ";
		}
		return s;
	}
}
