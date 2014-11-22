package com.networks.contextprofilecreator;

import android.location.Location;
import java.util.ArrayList;

public class ContextInfo {
	//Initial contact time
	//Final contact time
	//computation speed
	//Network bandwidth
	//Unique ID
	private ArrayList<Location> contextLocation;
	private String contextCPUUsage = "";
	private double contextAcc = 0;
	private long startTime;
	private String mngrID;
	
	public long getStartTime()
	{
		return startTime;
	}
	public void setStartTime()
	{
		startTime = System.currentTimeMillis();
	}
	public String getMngrID()
	{
		return mngrID;
	}
	public void setMngrID(String id)
	{
		mngrID = id;
	}
	public int getContextLocSize()
	{
		return contextLocation.size();
	}
	public float getContextSpeed()
	{
		return contextLocation.get(0).getSpeed();
	}
	public float getContextSpeed(int n)
	{
		return contextLocation.get(n).getSpeed();
	}
	public Location getContextLocation()
	{
		return contextLocation.get(0);
	}
	public Location getContextLocation(int n)
	{
		return contextLocation.get(n);
	}
	public void setContextLocation(Location lat)
	{
		contextLocation.add(lat);
	}
	public double getContextAcc()
	{
		return contextAcc;
	}
	public void setContextAcc(double lat)
	{
		contextAcc = lat;
	}
	public double getContextLatitude()
	{
		return contextLocation.get(0).getLatitude();
	}
	public double getContextLongitude()
	{
		return contextLocation.get(0).getLongitude();
	}
	public double getContextDistanceFrom(Location loc)
	{
		return contextLocation.get(0).distanceTo(loc);
	}
	public double getContextLatitude(int n)
	{
		return contextLocation.get(n).getLatitude();
	}
	public double getContextLongitude(int n)
	{
		return contextLocation.get(n).getLongitude();
	}
	public double getContextDistanceFrom(Location loc, int n)
	{
		return contextLocation.get(n).distanceTo(loc);
	}
	public String getContextCPUUsage()
	{
		return contextCPUUsage;
	}
	public void setContextCPUUsage(String lat)
	{
		contextCPUUsage = lat;
	}
	
	public ContextInfo()
	{
		startTime = System.currentTimeMillis();
	}
	
	public ContextInfo(String temp)
	{
		String[] arrTemp = temp.split(",");	
		if(arrTemp.length < 6)
		{
			return;
		}
		startTime = Long.parseLong(arrTemp[0]);
		contextAcc = Double.parseDouble(arrTemp[1]);
		contextCPUUsage = arrTemp[2];
		for(int j = 3; j < arrTemp.length; j++)
		{
			String[] tempLoc = arrTemp[j].split("|");	
			Location loc = new Location("History_Data");
			loc.setLatitude(Double.parseDouble(tempLoc[0]));
			loc.setLongitude(Double.parseDouble(tempLoc[1]));
			loc.setSpeed(Float.parseFloat(tempLoc[2]));
			contextLocation.add(loc);
		}
	}
	
	public long returnCurrentTime()
	{
		return System.currentTimeMillis();
	}
}
