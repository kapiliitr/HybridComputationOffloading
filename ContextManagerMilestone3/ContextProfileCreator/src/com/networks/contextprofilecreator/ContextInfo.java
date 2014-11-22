package com.networks.contextprofilecreator;

import android.location.Location;

public class ContextInfo {
	//Initial contact time
	//Final contact time
	//computation speed
	//Network bandwidth
	//Unique ID
	private Location contextLocation;
	private String contextCPUUsage = "";
	private double contextAcc = 0;
	private long startTime;
	
	public long getStartTime()
	{
		return startTime;
	}
	public void setStartTime()
	{
		startTime = System.currentTimeMillis();
	}
	public float getContextSpeed()
	{
		return contextLocation.getSpeed();
	}
	public Location getContextLocation()
	{
		return contextLocation;
	}
	public void setContextLocation(Location lat)
	{
		contextLocation = lat;
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
		return contextLocation.getLatitude();
	}
	public double getContextLongitude()
	{
		return contextLocation.getLongitude();
	}
	public double getContextDistanceFrom(Location loc)
	{
		return contextLocation.distanceTo(loc);
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
		Location loc = new Location("History_Data");
		loc.setLatitude(Double.parseDouble(arrTemp[2]));
		loc.setLongitude(Double.parseDouble(arrTemp[3]));
		loc.setSpeed(Float.parseFloat(arrTemp[4]));
		contextLocation = loc;
		contextCPUUsage = arrTemp[5];
	}
	
	public long returnCurrentTime()
	{
		return System.currentTimeMillis();
	}
}
