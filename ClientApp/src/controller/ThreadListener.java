package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class ThreadListener implements Runnable
	{
		ClientMain client;
		
		public ThreadListener(ClientMain c)
		{
			client = c;
		}

		@Override
		public void run() {
			  while(true)
			  {
				  
				try 
				{
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				client.OperationDb("retrieve"); 
			  }
		}
	}