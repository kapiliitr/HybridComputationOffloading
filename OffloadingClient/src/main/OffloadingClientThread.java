package main;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import communication.CodeSender;
import communication.ContextInformationClient;
import communication.DataReceiver;
import exchangeTemplates.ContextInformation;
import exchangeTemplates.NodeType;

public class OffloadingClientThread {
	// expected parameters ADD file location, broadcast IP, My IP
	public static void main(String[] args) {
		
		String addFileLocation = "/usr/local/code/android-custom-class-loading-sample-read-only/android-custom-class-loading-sample/assets/secondary_dex.jar";//args[0];
				
		String broadcastIP = "127.0.0.1";//args[1];
		String myIP = "10.0.2.2";//args[2];
		
		//Answer Collector
		DataReceiver drDataCollector = new DataReceiver(1235);
		drDataCollector.start();
	
		// Collect context information (a maximum of 10 and timeout after 2 seconds)
		ContextInformationClient contextInfocmationCollector = new ContextInformationClient(broadcastIP,10);
		LinkedList<ContextInformation> collectedInformation = null;
		getPeers(contextInfocmationCollector);
		collectedInformation = contextInfocmationCollector.getCollectedContextInfomration();
        
        // OPMTIMIZATION GOES HERE
		//////////////////////////////////////////////////
		//////////////////////////////////////////////////
		//////////////////////////////////////////////////
		/// WE HAVE ALL PEERS INFORMATION, LET'S USE IT !!
		//////////////////////////////////////////////////
		//////////////////////////////////////////////////
		//////////////////////////////////////////////////

        // SENDING TASKS TO PEERS
		/////////////////////////////////////////////////
		///////// FOR NOW SEND THE SAME TASK TO ALL PEERS
		/////////////////////////////////////////////////
		
        String address;
        String taskLocation;
		for (Iterator<ContextInformation> iterator = collectedInformation.iterator(); iterator
				.hasNext();) {
			ContextInformation contextInformation = (ContextInformation) iterator
					.next();
			address = contextInformation.getNodeAddress().getCanonicalHostName();
			System.out.println(contextInformation.getNodeAddress().getCanonicalHostName());
						
			////////////////////////////////////////
			// Assume everything is a laptop for now
			contextInformation.setNodeType(NodeType.LAPTOP);
			////////////////////////////////////////
			
			// Lookup and the send the task to send based on optimization and the node type
			if (contextInformation.getNodeType() == NodeType.LAPTOP)
			{
				// lookup the laptop .class file
				taskLocation = addFileLocation;
				new CodeSender(taskLocation,1234,address, 111,myIP);
			}
			else
			{
				// lookup the android dex files
				//send the android stuff
			}
		}

		drDataCollector.endReceiver();
	}
	
	public static void getPeers(ContextInformationClient contextInfocmationCollector)
	{
		ExecutorService pool = Executors.newSingleThreadExecutor();
		Future<LinkedList<ContextInformation>> future = pool.submit(contextInfocmationCollector);
        try {
        	future.get(3, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.out.println("Terminated!");
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pool.shutdownNow();

	}
}
