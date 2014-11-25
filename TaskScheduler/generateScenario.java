import java.util.ArrayList;
import java.util.List;


public class generateScenario {
	
	int cyclesReqd;
	int inputSize;
	int outputSize;
	double compSpeedLocal;
	double compSpeedPeer;
	double compSpeedCloud;
	int firstContactTime;
	int lastContactTime;
	int lastContactTimeFirstPeer;
	double latencyLocal;
	double latencyPeer;
	double latencyCloud;
	double networkBandwidthLocal;
	double networkBandwidth;
	double taskDuration;
	double cloudArrivalTime;
	
	public generateScenario()
	{
		cyclesReqd = 500;
		inputSize = 500;
		outputSize = 500;
		compSpeedLocal = 100;
		compSpeedPeer = 100;
		compSpeedCloud = 1000;
		firstContactTime = 0;
		lastContactTime = Integer.MAX_VALUE;
		lastContactTimeFirstPeer=10;
		latencyLocal = 0;
		latencyPeer = 0.01;
		latencyCloud = 1;
		networkBandwidth = 1000;
		networkBandwidthLocal = Integer.MAX_VALUE;
		taskDuration = cyclesReqd / compSpeedPeer;
		cloudArrivalTime = 0.65;
	}
	
	public List<Task> generateTasks(int numTasks)
	{
		List<Task> all_tasks = new ArrayList<Task>();
		
		for(int i=0; i<numTasks; i++)
		{
			Task task = new Task();
		    task.taskId=i;
		    task.cyclesReqd=cyclesReqd;
		    task.inputSize=inputSize;
		    task.outputSize=outputSize;
		    all_tasks.add(task);
		}
		return all_tasks;
	}
	
	public Resource[] generateResources(int numResources, int numTasks)
	{
		Resource[] resources = new Resource[numResources];
		
		// Inititator
	    resources[0]=new Resource();
	    resources[0].peerId=0;
	    resources[0].compSpeed=compSpeedLocal;
	    resources[0].firstContactTime=firstContactTime;
	    resources[0].lastContactTime=lastContactTime;
	    resources[0].latency=latencyLocal;
	    resources[0].networkBandwidth=networkBandwidthLocal;

	    // Cloud
	    resources[1]=new Resource();
	    resources[1].peerId=1;
	    resources[1].compSpeed=compSpeedCloud;
	    resources[1].firstContactTime=(int)(numTasks*taskDuration*cloudArrivalTime);
	    resources[1].lastContactTime=lastContactTime;
	    resources[1].latency=latencyCloud;
	    resources[1].networkBandwidth=networkBandwidth;
		
	    // Peers
	    for(int i=2; i<numResources; i++)
	    {
	    	resources[i]=new Resource();
		    resources[i].peerId=i;
		    resources[i].compSpeed=compSpeedPeer;
		    resources[i].firstContactTime=firstContactTime;
		    //resources[i].lastContactTime=(int)(lastContactTimeFirstPeer+(i-2)*(numTasks*taskDuration-lastContactTimeFirstPeer)/(numResources-2));
		    resources[i].lastContactTime=(int)(numTasks*taskDuration-(i-2)*(numTasks*taskDuration-lastContactTimeFirstPeer)/(numResources-2));
		    resources[i].latency=latencyPeer;
		    resources[i].networkBandwidth=networkBandwidth;
	    }
	    
		return resources;
	}

}
