package com.networks.contextprofilecreator;

import java.util.ArrayList;

public class HistoricalContext {
	private ArrayList<ContextInfo> lstContextData = new ArrayList<ContextInfo>();
	
	public void appendContextdata(ContextInfo objTemp)
	{
		lstContextData.add(objTemp);		
	}
	
	public ContextInfo getContextdata(int i)
	{
		return lstContextData.get(i);		
	}
	
	public ContextInfo getLastContextdata()
	{
		return lstContextData.get(lstContextData.size() - 1);
	}
	
	public int getContextlength()
	{
		return lstContextData.size();
	}
}
