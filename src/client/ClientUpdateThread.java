package client;

/**
 * Client Update Thread
 * Handles all in/out communication with server
 * Handles a variety of inbound message codes
 * Is called to send messages to server
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
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

public class ClientUpdateThread {
	
	/**
	 * PictionaryClient instance that is updated
	 */
	private PictionaryClient parent;
	
	/**
	 * Lobby Client instance that is updated 
	 */
	private LobbyClient lobbyParent;
	
	/**
	 * value determining if thread has ended
	 */
	private boolean end = false;
	
	/**
	 * Socket data-members that handle connection
	 */
	private Socket socket = null;
	
	/**
	 * Buffered Writer output 
	 */
	private BufferedWriter streamOut = null;
	
	/**
	 * Buffered Reader input
	 */
	private BufferedReader streamIn = null;
	
	/**
	 * Boolean to prevent makeArtist dialog from being over-top winner dialog
	 */
	private boolean dialogUp = false;
	
	/**
	 * Constructor for ClientUpdateThread
	 * Creates a new socket and connects it to the server 
	 * @param server 
	 * @param port
	 */
	public ClientUpdateThread(String server, int port) {
		
		System.out.println("Pictionary Client establishing connection. Please wait ...");
		
		try {  
			  
			  socket = new Socket(server, port);
			  System.out.println("Connected: " + socket);
			  startConnection();
			  
			  Thread cThread = new Thread(new Runnable() {
					public void run(){
					
						running();
					}
			  });

			  cThread.start();
			  
	      }
	      catch(UnknownHostException uhe) {  
	    	  System.out.println("Host unknown: " + uhe.getMessage());
	      }
	      catch(IOException ioe) {  
	    	  System.out.println("Server is down: " + ioe.getMessage());
	      }
		
		parent = null;
		lobbyParent = null;
		  
		  
	}
	
	/**
	 * Runs the game
	 * Depending the current status of the game,
	 * the different actions will occur to the game board and the 
	 * players involved
	 */
	public void running() {
		
		while (!end) {  
			try {  
				
				// Game Logic
				String code = "";
				if ((code = streamIn.readLine()) != null) {
					
					switch(code)
					{
					case "SHAPE":
						parent.updateBoard(streamIn.readLine());
						break;
					case "START_GAME":
						parent.clearBoard();
						if (!parent.isArtist()) {
							parent.resetTimer();
							parent.startTimer();
						}
						break;
					case "WORD":
						String s = streamIn.readLine();
						parent.setWord(s);
						
						// Block while (you won / someone else won) dialog is up
						System.out.println(dialogUp+" new round");
						while(dialogUp) {
							System.out.println("waiting");
						}
						
						JOptionPane.showConfirmDialog(parent, 
								"You are the new artist.\nYour word is: " + s 
								, "New Word", JOptionPane.PLAIN_MESSAGE);
						sendMessage("ARTIST_START");
						parent.clearBoard();
						parent.resetTimer();		
						parent.startTimer();
						break;
					case "ARTIST":
						if (streamIn.readLine().equals("Yes")) {
							parent.setArtist();
						}
						else {
							parent.setGuesser();
						}
					case "CLEAR":
						parent.clearBoard();
						break;
					case "UNDO":
						parent.undoBoard();
						break;
					case "BOARD":
						String sBoard = streamIn.readLine();
						while (!sBoard.equals("END_BOARD")) {
							parent.updateBoard(sBoard);
							sBoard = streamIn.readLine();
						}		
						break;
					case "GUESS":
						parent.addNewGuess(streamIn.readLine());
						break;
					case "WINNER":
						parent.stopTimer();
						final String sWin = streamIn.readLine();
						
						// This box should always be on top (priority over new round)
						Thread cThread = new Thread(new Runnable() {
							public void run(){							
								dialogUp = true;
								System.out.println(dialogUp+"Winner");
								JOptionPane.showConfirmDialog(parent, sWin, "Winner!", JOptionPane.PLAIN_MESSAGE);
								System.out.println(dialogUp+"Winnerdown");
								dialogUp = false;
							}
						});

						cThread.start();
						break;
					case "END_ROUND":
						final String sEnd = streamIn.readLine();
						parent.stopTimer();
						
						Thread eThread = new Thread(new Runnable() {
							public void run(){								
								dialogUp = true;
								// Tell user who won
								JOptionPane.showConfirmDialog(parent, 
										sEnd.split("\t")[0]+" guessed "+sEnd.split("\t")[1]+" correctly."
										, "Round Over", JOptionPane.PLAIN_MESSAGE);
								dialogUp = false;
							}
						});
						eThread.start();			
						break;
					case "SCORES":
						String scores = "";
						String sScore = streamIn.readLine();
						while (!sScore.equals("END_SCORES")) {
							scores += sScore + "\n";
							sScore = streamIn.readLine();
						}
						parent.readScores(scores);
						break;
					case "LOBBIES":
						lobbyParent.updateLobbies(streamIn.readLine());
						break;
					case "FAILED":
						JOptionPane.showConfirmDialog(lobbyParent, 
								"Lobby name already exists."
								, "Error", JOptionPane.PLAIN_MESSAGE);	
						break;
					case "SUCCESS":
						sendMessage("LOBBIES");
						System.out.println("Success");
						break;
					case "GAME_INFO":
						String info = "";
						String sInfo = streamIn.readLine();
						while (!sInfo.equals("END_INFO")) {
							info += sInfo + "\n";
							sInfo = streamIn.readLine();
						}
						lobbyParent.setInformation(info);
						break;
					case "ACCEPTED":
						lobbyParent.joinLobby();
						break;
					case "CATEGORY":
						parent.setCategory(streamIn.readLine());
						break;
						
					default:
						System.out.println(code + " not handled.");
					}
										
				}
			}
			catch(IOException ioe) {  	
				System.out.println("Sending error: " + ioe.getMessage());
			}
		}
	}
	
	// Used by client 
	/**
	 * Sends message
	 * @param s
	 */
	public void sendMessage(String s) {
		    try {
				streamOut.write(s);
			    streamOut.newLine();
			    streamOut.flush();	 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  catch (NullPointerException n) {
				
			}
	}
	
	/**
	 * Starts a new connection
	 * Links input and output streams to socket streams
	 * @throws IOException
	 */
	public void startConnection() throws IOException {  
		   streamOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		   streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	/**
	 * Sets the thread to end
	 */
	public void endThread() {
		System.out.println("<<End>>");
		//sendMessage("END!");
		end = true;
	}

	/**
	 * Sets the parent to the PictionaryClient
	 * @param p
	 */
	public void setParent(PictionaryClient p) {
		parent = p;		
	}
	
	/**
	 * Set lobby parent
	 * @param lp LobbyClient
	 */
	public void setLobbyParent(LobbyClient lp) {
		lobbyParent = lp;
	}
}
