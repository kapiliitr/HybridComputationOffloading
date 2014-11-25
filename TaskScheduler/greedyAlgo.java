import java.util.*;
import java.lang.*;
import java.io.*;

class TaskScheduler
{
  public static void main (String[] args) throws java.lang.Exception
  {
	int maxResources = 20;
	int maxTasks = 50;
	
	/*Resource[] resources = new generateScenario().generateResources(5, 5);
	List<Task> all_tasks = new generateScenario().generateTasks(5);
	Task[] tasks = all_tasks.toArray(new Task[all_tasks.size()]);
	
	for(int i=0;i<5;i++)
	{
		System.out.println(tasks[i].taskId+" "+tasks[i].cyclesReqd+" "+tasks[i].inputSize+" "+tasks[i].outputSize);
	}*/
	
	for(int p=5; p<=maxTasks; p+=5)
	{
		for(int q=5; q<=maxResources; q+=5)
		{
			Resource[] resources = new generateScenario().generateResources(q, p);
			List<Task> all_tasks = new generateScenario().generateTasks(p);
			Task[] tasks = all_tasks.toArray(new Task[all_tasks.size()]);
			
		    int i;
		    double max;
		   
		    HashMap<Integer,Integer> result = new TaskScheduler().newGreedyAlgo(resources,all_tasks);
		    max = new TaskScheduler().calculateFinishTime(result, resources, tasks);
		    System.out.println("Greedy: "+result.toString()+" Time:"+max);

		    HashMap<Integer,Integer> random_result = new TaskScheduler().randomAlgorithm(resources,all_tasks);
		    max = new TaskScheduler().calculateFinishTime(random_result, resources, tasks);
		    System.out.println("Random: "+random_result.toString()+" Time:"+max);

		    HashMap<Integer,Integer> naive_result = new TaskScheduler().naiveAlgo(resources,all_tasks);
		    max = new TaskScheduler().calculateFinishTime(naive_result, resources, tasks);
		    System.out.println("Naive: "+naive_result.toString()+" Time:"+max);

		}
	}

  }

  private double calculateFinishTime(HashMap<Integer,Integer> result, Resource[] resources, Task[] tasks)
  {
	  double max=0;
	  double[] timeOnResource = new double[resources.length];
	  for(int i=0;i<resources.length;i++)
		  timeOnResource[i]=resources[i].firstContactTime;
	  
	  for(int i=0;i<tasks.length;i++)
	  {
		  timeOnResource[result.get(tasks[i].taskId)] += new TaskScheduler().calculateTaskTime(resources[result.get(tasks[i].taskId)],tasks[i]);
	  }
	  
	  for(int i=0;i<resources.length;i++)
	  {
		  if(timeOnResource[i] > max)
			  max = timeOnResource[i];
	  }
	  
	  return max;
  }
  
  private HashMap<Integer,Integer> newGreedyAlgo(Resource[] resources, List<Task> taskList) 
  {
    double inf = Double.POSITIVE_INFINITY;
    int size = resources.length;
    double[] curDevFinishTime = new double[size];
    double[] newDevFinishTime = new double[size];
    HashMap<Integer,Integer> offloadedTasks = new HashMap<Integer,Integer>();

    for(int i=0;i<size;i++)
    {
      curDevFinishTime[i] = resources[i].firstContactTime;
    }
    double curMinFinTime;
    int curMinPos;

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
      Pair pair = new Pair(t.taskId,resources[curMinPos].peerId);
      //curOffloadedTasks = offloadedTasks.put(resources[curMinPos].peerId);
      //curOffloadedTasks.add(t.taskId);
      offloadedTasks.put(t.taskId,resources[curMinPos].peerId);
    }
    return  offloadedTasks;
  }

  private HashMap<Integer,Integer> naiveAlgo(Resource[] resources, List<Task> taskList) 
  {
    double inf = Double.POSITIVE_INFINITY;
    int size = resources.length;
    double[] curDevFinishTime = new double[size];
    double[] newDevFinishTime = new double[size];
    HashMap<Integer,Integer> offloadedTasks = new HashMap<Integer,Integer>();
    Random rn = new Random();
    
    for(int i=0;i<size;i++)
    {
      curDevFinishTime[i] = resources[i].firstContactTime;
    }
    double curMinFinTime;
    int curMinPos;

    for(Task t: taskList)
    {
      int assigned = 0;
      for(int i=0;i<size;i++)
      {
        newDevFinishTime[i] = calculateTaskTime(resources[i],t) + curDevFinishTime[i];
      }
      for(int i=2;i<size;i++)
      {
        if(newDevFinishTime[i] < resources[i].lastContactTime)
        {
          curDevFinishTime[i] = newDevFinishTime[i];
          assigned = 1;
          offloadedTasks.put(t.taskId,resources[i].peerId);
          break;
        }
      }
      if(assigned==0)
      {
    	  int rand = rn.nextInt(2);
    	  if(newDevFinishTime[rand] < resources[rand].lastContactTime)
    		  offloadedTasks.put(t.taskId,resources[rand].peerId);
    	  else
    		  offloadedTasks.put(t.taskId,resources[(rand+1)%2].peerId);
    		  
      }
      
    }
    return  offloadedTasks;
  }

  
  
  private HashMap<Integer,Integer> randomAlgorithm(Resource[] resources, List<Task> taskList)
  {
    int size = resources.length;
    double[] curDevFinishTime = new double[size];
    double[] newDevFinishTime = new double[size];
    HashMap<Integer,Integer> offloadedTasks = new HashMap<Integer,Integer>();
    List<Integer> curOffloadedTasks;

    Random rn = new Random();

    for(int i=0;i<size;i++)
    {
      curDevFinishTime[i] = resources[i].firstContactTime;
    }
    for(Task t: taskList)
    {
      List<Integer> validResources = new ArrayList<Integer>();
      for(int i=0;i<size;i++)
      {
        newDevFinishTime[i] = calculateTaskTime(resources[i],t) + curDevFinishTime[i];
        if(newDevFinishTime[i]<resources[i].lastContactTime)
        {
          validResources.add(i);
        }
      }

      int assignedDeviceIndex = rn.nextInt(validResources.size());
      int assignedDevice = validResources.get(assignedDeviceIndex);
      curDevFinishTime[assignedDevice] = newDevFinishTime[assignedDevice];
      //curOffloadedTasks = offloadedTasks.put(resources[curMinPos].peerId);
      //curOffloadedTasks.add(t.taskId);
      offloadedTasks.put(t.taskId,assignedDevice);
    }

    return offloadedTasks;
  }

  private double calculateTaskTime(Resource r, Task t)
  {
    double dataTransferTime = (t.inputSize + t.outputSize) / r.networkBandwidth;
    double computationTime = t.cyclesReqd/r.compSpeed;
    double result = computationTime + dataTransferTime + r.latency;
    return result; 
  }
}

