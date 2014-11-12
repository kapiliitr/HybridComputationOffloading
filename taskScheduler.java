import java.util.*;
import java.lang.*;
import java.io.*;

class TaskScheduler
{
	public static void main (String[] args) throws java.lang.Exception
	{
		// your code goes here
	}

	

	public List<int> schedule(Resource local, Resource peer, Resource cloud, List<Task> tasks)
	{
		if (peer != NULL)
		{
			List<taskTime> timeTakenByTasksOnPeer = findTaskPeerNetworkRatio(peer, tasks);
			
			Collections.sort(timeTakenByTasks, new Comparator<taskTime>(){
				public int compare(taskTime t1, taskTime t2){
					return (t2.ratio - t1.ratio);
				}});	
	
			if (cloud == NULL)
				return greedyTasks(peer, timeTakenByTasksOnPeer);
			else
			{
				List<taskTime> timeTakenByTasksOnCloud = findTaskPeerNetworkRatio(cloud, tasks);
				return greedyTasks(peer, cloud, timeTakenByTasksOnPeer, timeTakenByTasksOnCloud);	
			}
		}
	}
	
	private List<taskTime> findTaskPeerNetworkRatio(Resource peer, List<Task> tasks)
	{
		List<taskTime> timeForTaskOnPeer = new List<taskTime>;
		for (Task task : tasks)
		{
			float dataTransferTime = (task.inputSize + task.outputSize) / peer.networkBandwidth;
			taskTime taskTimeNetworkRatio = new taskTime();
			taskTimeNetworkRatio.taskId = task.taskId;
			taskTimeNetworkRatio.time = dataTransferTime + peer.latency + (task.cyclesReqd / peer.compSpeed);
			taskTimeNetworkRatio.ratio = taskTimeNetworkRatio.time / dataTransferTime;
			timeForTaskOnPeer.add(taskTimeNetworkRatio);
		}
		return timeForTaskOnPeer;
	}

	private List<int> greedyTasks(Resource peer, List<taskTime> tasks)
	{
		List<int> offloadedTasks = new List<int>;
		int remDuration = peer.lastContactTime - peer.firstContactTime;
		for(taskTime task : tasks)
		{
			if(task.time <= remDuration)
			{
				offloadedTasks.add(task.taskId);
				remDuration -= task.time;
			}
		}	
		return offloadedTasks;
	}

	private List<int> greedyTasks(Resource peer, Resource cloud, List<taskTime> tasks, List<taskTime> timeTakenByTasksOnCloud)
	{
		List<int> offloadedTasks = new List<int>;

		int totalTimeCloud = cloud.firstContactTime;
		for(taskTime task : timeTakenByTasksOnCloud)
		{
			totalTimeCloud += task.time;
		}
		int totalTimePeer = peer.firstContactTime;
		int remDuration = peer.lastContactTime - peer.firstContactTime;

		for(taskTime task : tasks)
		{
			totalTimePeer += task.time;
			if(task.time <= remDuration && totalTimeCloud > totalTimePeer)
			{
				offloadedTasks.add(task.taskId);
				remDuration -= task.time;
				totalTimeCloud -= findTimeForTask(task.taskId, timeTakenByTasksOnCloud);
				
			}
			else
				totalTimePeer -= task.time;
		}

		return offloadedTasks;
	}

	private List<int> greedyTasks(Resource local, Resource peer, Resource cloud, List<taskTime> taskTimeLocal, List<taskTime> taskTimePeer, List<taskTime> taskTimeCloud)
	{
		List<int> offloadedTasks = new List<int>;

		List<int> doOnLocal = greedyTasks(local, cloud, taskTimeLocal, taskTimeCloud);
		int FinishTime = findTimeForTaskList(local, cloud, doOnLocal, taskTimeLocal, taskTimeCloud);
		int totalTimePeer = peer.firstContactTime;
		int remDuration = peer.lastContactTime - peer.firstContactTime;
		
		for(taskTime task : taskTimePeer)
		{
			totalTimePeer += task.time;
			if(task.time <= remDuration && FinishTime > totalTimePeer)
			{
				offloadedTasks.add(task.taskId);
				remDuration -= task.time;
				FinishTime = calculateNewFinishTime(local, cloud, offloadedTask, taskTimeLocal, taskTimeCloud)
			}
			else
				totalTimePeer -= task.time;

		}
		
		return offloadedTasks;

	}
	
	
	private HashMap<Integer,List<Integer>> newGreedyAlgo(Resource[] resources, List<Task> taskList) 
	{
		double inf = Double.POSITIVE_INFINITY;
		int size = resources.size();
		int[] curDevFinishTime = new int[size];
		int[] newDevFinishTime = new int[size];
		HashMap offloadedTasks = new HashMap<Integer,List<Integer>>();
		
		for(int i=0;i<size;i++)
		{
			curDevFinishTime[i] = resources[i].firstContactTime;
			offloadedTasks.put(resources[i].peerId, Collections.<Integer>emptyList());
		}
		double curMinFinTime;
		int curMinPos;
		List<Integer> curOffloadedTasks;
		
		for(Task t: taskList)
		{
			curMinFinTime = inf;
			curMinPos = -1;
			for(int i=0;i<size;i++)
			{
				newDevFinishTime[i] = calculateTaskTime(resources[i],t) + curDevFinishTime[i];
			}
			for(int i=0;i<size;i++)
			{
				if(newDevFinishTime[i] < resources[i].lastContactTime && newDevFinishTime[i] < curMinFinTime)
				{
					curMinFinTime = newDevFinishTime[i];
					curMinPos = i;
				}
			}
			curDevFinishTime[curMinPos] = newDevFinishTime[curMinPos];
			curOffloadedTasks = offloadedTasks.get(resources[curMinPos].peerId);
			curOffloadedTasks.add(t.taskId);
		}
		return  offloadedTasks;
	}
	
	private HashMap<Integer,List<Integer>> randomAlgorithm(Resource[] reources, List<Task> taskList)
	{
		int size = resources.size();
		int[] curDevFinishTime = new int[size];
		int[] newDevFinishTime = new int[size];
		HashMap offloadedTasks = new HashMap<Integer,List<Integer>>();
	
		for(int i=0;i<size;i++)
		{
			curDevFinishTime[i] = resources[i].firstContactTime;
			offloadedTasks.put(resources[i].peerId, Collections.<Integer>emptyList());
		}
		for(Task t: taskList)
		{
			
		}
	}
	
	private int calculateTaskTime(Resource r, Task t)
	{
		float dataTransferTime = (t.inputSize + t.outputSize) / r.networkBandwidth;
		float compuatationTime = t.cyclesReqd/r.compSpeed;
		return (compuatationTime + dataTransferTime + r.latency); 
	}
	
	
	private List<int> newGreedyTasks(Resource local, Resource peer, Resource cloud, List<taskTime> taskTimeLocal, List<taskTime> taskTimePeer, List<taskTime> taskTimeCloud)
	{
		List<int> offloadedTasks = new List<int>;
		int finishTimeLocal = local.firstContactTime;
		int finishTimeCloud = cloud.firstContactTime;
		int finishTimePeer = peer.firstContactTime;
		int remDuration = peer.lastContactTime - peer.firstContactTime;
		int minFinishTime, timeOnLocal, timeOnCloud;
		
		for(taskTime task : taskTimePeer)
		{
			finishTimePeer += task.time;
			timeOnLocal = findTimeForTask(task.taskId,taskTimeLocal);
			finishTimeLocal += timeOnLocal;
			timeOnCloud = findTimeForTask(task.taskId,taskTimeCloud);
			finishTimeCloud += timeOnCloud; 
			minFinishTime = min(finishTimePeer,min(finishTimeLocal,finishTimeCloud));
			if(finishTimePeer  == minFinishTime)
			{
				offloadedTasks.add(task.taskId);
				finishTimeLocal -= timeOnLocal;
				finishTimeCloud -= timeOnCloud;
			}
			else if(finishTimeLocal  == minFinishTime)
			{
				finishTimePeer -= task.time;
				finishTimeCloud -= timeOnCloud;
			}
			else
			{
				finishTimePeer -= task.time;
				finishTimeLocal -= timeOnLocal;
			}
		}
		
		return offloadedTasks;
	}
	
	
	private int calculateNewFinishTime(Resource local, Resource cloud, List<int> taskList, List<taskTime> taskTimeLocal, List<taskTime> taskTimeCloud)
	{
		//remove from the list of cloud and local this task
		for(int task : taskList)
		{
			for(taskTime taskLocal : taskTimeLocal)
			{
				if(task == taskLocal.taskId)
				{
					taskTime.remove(taskLocal);
					break;
				}
			}
		
			for(taskTime taskCloud : taskTimeCloud)
			{
				if(task == taskCloud.taskId)
				{
					taskTime.remove(taskLocal);
					break;
				}
			}
		}
		
		//calculate new finishTime
		List<int> doOnLocal = greedyTasks(local, cloud, taskTimeLocal, taskTimeCloud);
		return findTimeForTaskList(local, cloud, doOnLocal, taskTimeLocal, taskTimeCloud);	
	}



	

	private int findTimeForTaskList(resource local, resource cloud, List<int> taskIds, List<taskTime> localTimeTasks, List<taskTime> cloudTimeTasks)
	{
		int totalTimeLocal = local.firstContactTime;
		int totalTimeCloud = cloud.firstContactTIme;
		int found;
		for(taskTime taskLocal : localTimeTasks)
		{	
			found = 0;
			for(int id : taskIds)
			{
				if(taskLocal.taskId == id)
				{
					totalTimeLocal += taskLocal.time;
				}
				found = 1;
			}
			if(found!=1)
			{
				for(taskTime taskCloud : cloudTimeTasks)
				{
					if(taskCloud.taskId == taskLocal.taskId)
					{
						totalTimeCloud += taskCloud.time;
					}
				}
			}
			
		}

		if(totalTimeCloud < totalTimeLocal)
			return totalTimeLocal;
		else
			return totalTimeCloud;
	}

	private int findTimeForTask(int taskId, List<taskTime> tasks)
	{
		for(taskTime task : tasks)
		{
			if(task.taskId == taskId)
			{
				return task.time;
			}
		}
	}

	public List<Task> removeOffloadedTasks(List<Task> tasks, List<int> offloadedTasks)
	{
		List<Task> newTasks = new List<Task>;
		for(Task task : tasks)
		{	
			bool offload = false;
			for(int id : offloadedTasks)
			{
				if(task.taskId == id)
				{
					offload = true;
					break;
				}
			}
			if(offload == false)
				newTasks.add(task);
		}
		return newTasks;
	}
}

 


class Task
{
	int taskId;
	int cyclesReqd;
	int inputSize;
	int outputSize;
}

class Resource
{
	int peerId;
	float compSpeed;
	int firstContactTime;
	int lastContactTime;
	int latency;
	float networkBandwidth;
}

class taskTime
{
	int taskId;
	float time;
	float ratio;
}

