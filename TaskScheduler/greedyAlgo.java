import java.util.*;
import java.lang.*;
import java.io.*;

class TaskScheduler
{
  public static void main (String[] args) throws java.lang.Exception
  {
    //Dummy Resources
    Resource[] resources = new Resource[3];

    //Dummy inititator
    resources[0]=new Resource();
    resources[0].peerId=0;
    resources[0].compSpeed=10;
    resources[0].firstContactTime=0;
    resources[0].lastContactTime=Integer.MAX_VALUE;
    resources[0].latency=0;
    resources[0].networkBandwidth=Integer.MAX_VALUE;

    //Dummy peer
    resources[1]=new Resource();
    resources[1].peerId=1;
    resources[1].compSpeed=10;
    resources[1].firstContactTime=0;
    resources[1].lastContactTime=50;
    resources[1].latency=.01;
    resources[1].networkBandwidth=20;

    //Dummy inititator
    resources[2]=new Resource();
    resources[2].peerId=2;
    resources[2].compSpeed=100;
    resources[2].firstContactTime=50;
    resources[2].lastContactTime=Integer.MAX_VALUE;
    resources[2].latency=.1;
    resources[2].networkBandwidth=25;

    //Dummy Tasks
    List<Task> all_tasks = new ArrayList<Task>();
    Task[] tasks = new Task[5];

    tasks[0]=new Task();
    tasks[0].taskId=0;
    tasks[0].cyclesReqd=10;
    tasks[0].inputSize=8;
    tasks[0].outputSize=4;
    all_tasks.add(tasks[0]);

    tasks[1]=new Task();
    tasks[1].taskId=1;
    tasks[1].cyclesReqd=50;
    tasks[1].inputSize=128;
    tasks[1].outputSize=32;
    all_tasks.add(tasks[1]);

    tasks[2]=new Task();
    tasks[2].taskId=2;
    tasks[2].cyclesReqd=150;
    tasks[2].inputSize=64;
    tasks[2].outputSize=4;
    all_tasks.add(tasks[2]);

    tasks[3]=new Task();
    tasks[3].taskId=3;
    tasks[3].cyclesReqd=25;
    tasks[3].inputSize=1024;
    tasks[3].outputSize=1024;
    all_tasks.add(tasks[3]);

    tasks[4]=new Task();
    tasks[4].taskId=4;
    tasks[4].cyclesReqd=4000;
    tasks[4].inputSize=32;
    tasks[4].outputSize=8;
    all_tasks.add(tasks[4]);

    HashMap<Integer,Integer> result = new TaskScheduler().newGreedyAlgo(resources,all_tasks);
    System.out.println(result.toString());
    HashMap<Integer,Integer> random_result = new TaskScheduler().randomAlgorithm(resources,all_tasks);
    System.out.println(random_result.toString());
/*    for (TypeKey name: example.keySet())
    {
      String key =name.toString();
      String value = example.get(name).toString();  
      System.out.println(key + " " + value);  
    } */
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
      if(!assigned)
      {
    	  int rand = rn.nextInt(2);
    	  if(newDevFinishTime[rand] < resources[rand].lastContactTime)
    		  offloadedTasks.put(t.taskId,resources[rand].peerId);
    	  else
    		  offloadedTasks.put(t.taskId,resources[(rand+1)%2].peerId);
    		  
      }
      
      
      //curOffloadedTasks = offloadedTasks.put(resources[curMinPos].peerId);
      //curOffloadedTasks.add(t.taskId);
      
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

