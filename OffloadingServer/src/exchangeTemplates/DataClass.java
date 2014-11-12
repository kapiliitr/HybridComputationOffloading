package exchangeTemplates;

import java.io.Serializable;

public class DataClass implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String [] stringParamters;
	int [] integerParamters;
	double [] doubleParameters;
	long taskID;
	long dispatchTime;
	String dispatcherIP;
	
	
	public String getDispatcherIP() {
		return dispatcherIP;
	}
	public void setDispatcherIP(String dispatcherIP) {
		this.dispatcherIP = dispatcherIP;
	}
	public long getTaskID() {
		return taskID;
	}
	public void setTaskID(long taskID) {
		this.taskID = taskID;
	}
	public long getDispatchTime() {
		return dispatchTime;
	}
	public void setDispatchTime(long dispatchTime) {
		this.dispatchTime = dispatchTime;
	}
	public String[] getStringParamters() {
		return stringParamters;
	}
	public void setStringParamters(String[] stringParamters) {
		this.stringParamters = stringParamters;
	}
	public int[] getIntegerParamters() {
		return integerParamters;
	}
	public void setIntegerParamters(int[] integerParamters) {
		this.integerParamters = integerParamters;
	}
	public double[] getDoubleParameters() {
		return doubleParameters;
	}
	public void setDoubleParameters(double[] doubleParameters) {
		this.doubleParameters = doubleParameters;
	}
	
	
	
}
