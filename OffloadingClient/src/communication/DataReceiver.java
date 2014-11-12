package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import exchangeTemplates.DataClass;


public class DataReceiver implements Runnable{
    private Thread t;
	boolean endReceiver = true;
	public Queue<DataClass> dataQe;
    ServerSocket servsock = null;
	
	public DataReceiver(int portNumber)
	{
		
		dataQe = new LinkedList<DataClass>();
		try {
			servsock = new ServerSocket(portNumber);

		  	   
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	 }
	
	
	private void startDataReceiver()
	{
	    Socket sock = null;
		while(endReceiver)
		{
			try {
				sock = servsock.accept();
				ObjectInputStream oIS = new ObjectInputStream(sock.getInputStream());
							
				dataQe.add((DataClass) oIS.readObject());
				System.out.println("############## GOT ANSWER ############");
				System.out.println(dataQe.poll().getTaskID());
				System.out.println("######################################");
				sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void start ()
	{
		if (t == null)
		{
			t = new Thread (this, "7amada");
			t.start ();
		}
	}
	
	public void endReceiver()
	{
		endReceiver = false;
	}

	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		startDataReceiver();
	}
	
}

