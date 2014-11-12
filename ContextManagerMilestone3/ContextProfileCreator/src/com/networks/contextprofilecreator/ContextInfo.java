package com.networks.contextprofilecreator;

public class ContextInfo {
	//Initial contact time
	//Final contact time
	//computation speed
	//Network bandwidth
	//Unique ID
	private double contextLatitude = 0;
	private double ContextUniqueID = 0;
	private double contextLongitude = 0;
	private double contextDistanceFrom = -1;
	private int contextCPUUsage = 0;
	
	public ContextInfo()
	{
		
	}
	
	public double getContextUniqueID()
	{
		return ContextUniqueID;
	}
	public void setContextUniqueID(double ID)
	{
		ContextUniqueID = ID;
	}
	public double getContextLatitude()
	{
		return contextLatitude;
	}
	public void setContextLatitude(double lat)
	{
		contextLatitude = lat;
	}
	public double getContextLongitude()
	{
		return contextLongitude;
	}
	public void setContextLongitude(double lat)
	{
		contextLongitude = lat;
	}
	public double getContextDistanceFrom()
	{
		return contextDistanceFrom;
	}
	public void setContextDistanceFrom(double lat)
	{
		contextDistanceFrom = lat;
	}

	public int getContextCPUUsage()
	{
		return contextCPUUsage;
	}
	public void setContextCPUUsage(int lat)
	{
		contextCPUUsage = lat;
	}
	}
	
//	public String getContextCPUUsage()
//	{
//		return contextCPUUsage;
//	}
//	public void setContextCPUUsage(String lat)
//	{
//		contextCPUUsage = lat;
//	}
//}
