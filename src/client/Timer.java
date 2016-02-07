package client;

import javax.swing.*;

/**
 *Timer class decrements time left to guess and draw
 * 
 * @author Ryan Sharpe 
 * @version 0.3, 11/2/15 
 **/
public class Timer extends JLabel
{
	/**
	 * Serial ID 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Time management values **/
	private int currentTime;
	private int SENTINEL = 9998;
	
	/** boolean if timer is running **/
	private boolean running;

	/** Begins timer thread to decrement time 
	 * @param none
	 * @throws InterruptedException
	 * */
	public Timer() throws InterruptedException
	{
		currentTime = 100;
		running = false;
		setText("" + currentTime); 
		Thread timerThread = new Thread(new Runnable()
		{
			public void run(){
			
				try {
					updateTime();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		timerThread.start();	 
	}
	
	/**
	 * Updates the time, waits for a second before decrementing again
	 * @param none
	 * @throws InterruptedException
	 */
	public void updateTime() throws InterruptedException
	{
		while(currentTime != SENTINEL)
		{		
			if (running)
			{
				Thread.sleep(1000);
				if(currentTime > 0)
					currentTime = currentTime - 1;
				setText("" + currentTime);
			}
		}
	}
	
	/**
	 * Access the time
	 * @return the current time
	 */
	public int getTime()
	{
		return currentTime;
	}
	
	/**
	 * Resets the time
	 * @return none
	 */
	public void resetTime()
	{
		currentTime = 99;
	}
	
	/**
	 * Stops the timer from running
	 * @return the none
	 */
	public void pauseTime()
	{
		running = false;
	}
	
	/**
	 * Timer resumes running
	 * @return none
	 */
	public void resumeTime()
	{
		running = true;
	}
	
	/**
	 * Stops the timer
	 * @return none
	 */
	public void endTimer()
	{
		currentTime = 0;
	}
	
	/**
	 * Stops the timer thread
	 * @return the current time
	 */
	public void endTimerThread()
	{
		currentTime = SENTINEL + 1;
	}

}


