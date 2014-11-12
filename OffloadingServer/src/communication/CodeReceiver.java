package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.lang.ClassLoader;

import exchangeTemplates.DataClass;


public class CodeReceiver extends ClassLoader implements Runnable{
    private Thread t;

	ServerSocket servsock = null;
	boolean endReceiver = true;
	public Queue<Class> qe;
	public Queue<DataClass> dataQe;
	
	
	public CodeReceiver(String SERVER, int portNumber, ClassLoader parentClassLoader)
	{
		super(parentClassLoader );
		qe = new LinkedList<Class>();
		dataQe = new LinkedList<DataClass>();
	    try {
		  servsock = new ServerSocket(portNumber);

	       
	      System.out.println("Connecting...");
	    } catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	 }
	
	
	private void startCodeReceiver()
	{
		int FILE_SIZE = 1000000;
		int bytesRead;
		int current = 0;
		String operationName = "";
		byte [] mybytearray  = new byte [FILE_SIZE];
		Socket sock = null;
		while(endReceiver)
		{
			
			
			
			try {
				sock = servsock.accept();
				ObjectInputStream oIS = new ObjectInputStream(sock.getInputStream());
				operationName = (String) oIS.readObject();
				System.out.println("Operation send is called: " + operationName);
				
				dataQe.add((DataClass) oIS.readObject());
				System.out.println("Reading data ...");
				
				InputStream is = sock.getInputStream();
				bytesRead = is.read(mybytearray,0,mybytearray.length);
				current = bytesRead;
				do {
					bytesRead = is.read(mybytearray, current, (mybytearray.length-current));					
					if(bytesRead >= 0) current += bytesRead;
				} while(bytesRead > -1);
				
				Class operationClass = findLoadedClass("sampleImplementations."+operationName);
				if (operationClass == null)
					qe.add(defineClass("sampleImplementations."+operationName,mybytearray, 0, current));
				else
					qe.add(operationClass);
				
				System.out.println("Reading class ...");
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
		startCodeReceiver();
	}
	
}

