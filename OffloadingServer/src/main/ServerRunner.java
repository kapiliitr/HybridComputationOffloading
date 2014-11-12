package main;

import java.io.IOException;

import communication.BroadcastMessageServer;
import communication.CodeReceiver;
import communication.DataSender;
import exchangeTemplates.CodeClass;
import exchangeTemplates.DataClass;

public class ServerRunner {
	
	
	public static void main(String[] args) {
	
		ClassLoader   parentClassLoader = CodeReceiver.class.getClassLoader();
		CodeReceiver tt = new CodeReceiver("localhost", 1234, parentClassLoader);
		tt.start();
		try {
			new BroadcastMessageServer("brdcastmessage", 1236).start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while(true)
		{
			while (tt.qe.isEmpty()) 
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Waiting for tasks... ");
			}
			System.out.println("Task arrived !!");
			
			try {
				CodeClass x = (CodeClass)tt.qe.poll().newInstance();
				DataClass dx = tt.dataQe.poll();
				x.initialize(dx);
				x.run(dx);
				System.out.println("Task output is : " + dx.getIntegerParamters()[0]);
				new DataSender( 1235,dx);
				
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
