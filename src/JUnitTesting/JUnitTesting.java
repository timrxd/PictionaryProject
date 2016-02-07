package JUnitTesting;

/**
 * JUnitTesting class made to test various aspects of the 
 * @author Ryan Sharpe
 */
import static org.junit.Assert.*;

import java.awt.Color;
import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import client.*;
import server.*;

public class JUnitTesting
{
	/**
	 * Static members to test creating instances of
	 */
	private static Timer timerInstance;
	
	private static Shape shapeInstance;
	
	/**
	 * Create a new instance of timer for testing purposes
	 * @throws InterruptedException
	 */
	public void setTimer() throws InterruptedException
	{
		timerInstance = new Timer();
	}
	
	/**
	 * Tests creating a new instance of shape and the results for its
	 * toString, and testable get methods
	 */
	@Test
	public void testShape() 
	{
		shapeInstance = new Shape("Square", false, 1, 2, 3, 4, Color.red);
		assertEquals(shapeInstance.toString(), "Square"+ " " + 0  + " " + 1  + " " + 2  + " " + 3  + " " + 4 + " "+ Color.red.getRGB());
		assertEquals(shapeInstance.getFill(), false);
		assertEquals(shapeInstance.getType(), "Square");
	}
	
	/**
	 * Tests pausing and continuing the timer to make sure that it runs correctly
	 * after resumed and paused.
	 * @throws InterruptedException
	 */
	@Test
	public void testUpdateTime() throws InterruptedException
	{
		setTimer();
		assertEquals(timerInstance.getTime(), 100);
		//enable timer and wait 1 second
		timerInstance.resumeTime();
		Thread.sleep(2000);
		//decrement check
		assertEquals(timerInstance.getTime(), 99);
		//2 seconds later
		Thread.sleep(2000); 
		assertEquals(timerInstance.getTime(), 97);
		//pause timer
		timerInstance.pauseTime();
		//wait for a 4 more seconds
		Thread.sleep(4000);
		//should equal 96 because of slight time delay with threads
		assertEquals(timerInstance.getTime(), 96);
		//resume timer
		timerInstance.resumeTime();
		Thread.sleep(1500);
		assertEquals(timerInstance.getTime(), 95);
		timerInstance.endTimerThread();
	} 
	
	@Test
	public void changeTime() throws InterruptedException
	{
		setTimer();
		timerInstance.resetTime();
		assertEquals(timerInstance.getTime(), 99);
		timerInstance.endTimer();
		assertEquals(timerInstance.getTime(), 0);
		timerInstance.endTimerThread();
	}
	
	/**
	 * Tests the construction and instantiation of server classes
	 * and some of their retrieval methods.
	 * @throws IOException
	 */
	@Test
	public void testServers() throws IOException
	{
		PictionaryServer picServer = new PictionaryServer(4444);
		Socket suckit = new Socket();
	    GameServer server = new GameServer(null);
	    server.newRound();
	    server.endGame();
	     
	   server.newRound(); 
	   System.out.println("this");
	   System.out.println(server.gameInfo());
	   assertEquals(server.getName(), null);
	   assertEquals(server.getCategory(), "pokemon"); 
	   picServer.close();
	   assertTrue(picServer.getGames() != null);
	} 
	
	/**
	 * Tests and construction and instantiation of client classes 
	 * and some of their other methods.
	 */
	@Test 
	public void testClients()
	{
		LobbyClient client = new LobbyClient();
		DrawControl control = new DrawControl(new PictionaryClient(null));
		ClientUpdateThread cthread = new ClientUpdateThread("server", 123);
		PictionaryClient picClient = new PictionaryClient(cthread);
		ColorChooser chooser = new ColorChooser(picClient);
		cthread.endThread();
	} 	
	
}