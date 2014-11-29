package com.networks.contextprofilecreator;

import java.io.FileOutputStream;

import org.ejml.simple.SimpleMatrix;

import android.content.Context;
import android.location.Location;
//import android.provider.Settings.System;

public class KalmanFilterMobility {

	//Equations Used:
	//State:
	//x(t)=Ax'(t-1)+Bu(t)
	//A=[{1,t},{0,1}] 
	//B=[t^2/2,t]
	//C=[{1,0},{0,1}]			Made changes to fix issues in initial equation
	//Error Covariance
	//P(t)=AP(t-1)A^T+E(x)
	//Kalman Gain:
	//K(t)=P(t)C^T(CP(t)C^T+E(z))^-1
	//Measurement:
	//x'(t)=x(t)+k(t)(z'(t)-Cx(t))
	//P'(t)=(I-K(t)C)P(t)
	//I=Identity Matrix
	
	private SimpleMatrix A = new SimpleMatrix(2, 2); 	//state transition matrix
	private SimpleMatrix B = new SimpleMatrix(2, 1); 	//input control matrix
	private SimpleMatrix C = new SimpleMatrix(2, 2); 	//measurement matrix
	private double accU = 1;							//Acceleration defaulted to 1
	private SimpleMatrix initState = new SimpleMatrix(2, 1); //Initialize initial position to 0,0
	private SimpleMatrix estimatedState = initState; //Make the estimate and measured state same
	private double minCovarX = 10;				 	//Min covariance to move X along.
	private double minCovarZ = 0.01;					 //Min covariance to move Z along.
	private SimpleMatrix P;							 //position variance
	public Boolean debugMode = false; 				//Writes all prediction to file
	public Context debugCntxt; 
	public int debugFileMode = 0; 
	private int debugCnt = 0; 
	
	public double getaccU()
	{
		return accU;
	}
	public void setaccU(double lat)
	{
		accU = lat;
	}
	
	public void Update(Location loc, double time)
	{
		if(loc != null)
		{
			double[][] temp = new double[][] {{1,time},{0,1}};
			A = new SimpleMatrix(temp);
			temp = new double[][] {{time*time/2},{time}};
			B = new SimpleMatrix(temp);
			temp = new double[][] {{1,0},{0,1}};
			C = new SimpleMatrix(temp);
			temp = new double[][] {{time*time*time*time/4,time*time*time/2},{time*time*time/2,time*time}};
			SimpleMatrix Ex = new SimpleMatrix(temp).scale(minCovarX*minCovarX); 		 //position noise conversion to Covariance matrix
			temp = new double[][] {{1,0},{0,1}};
			Ex = new SimpleMatrix(temp);
			if(P == null)
			{
				P = Ex;
			}	
			temp = new double[][]{{minCovarZ*minCovarZ,0},{0,1}};
			SimpleMatrix Ez = new SimpleMatrix(temp);
			estimatedState = A.mult(estimatedState).plus(accU,B);
			P = A.mult(P).mult(A.transpose()).plus(Ex);
			SimpleMatrix K = new SimpleMatrix(2,2);
			K = P.mult(C.transpose()).mult((C.mult(P).mult(C.transpose()).plus(Ez)).invert());
			temp = new double[][] {{loc.getLatitude()},{loc.getLongitude()}};
			SimpleMatrix tempLoc = new SimpleMatrix(temp);
			estimatedState = estimatedState.plus(K.mult(tempLoc.minus(C.mult(estimatedState))));
			P = (SimpleMatrix.identity(2).minus(K.mult(C))).mult(P);
			if(debugMode)
			{
				FileOutputStream outputStream;

				try {
				  outputStream = debugCntxt.openFileOutput("ContextText.txt",debugFileMode);
				  String kalmanData = "Update: " + debugCnt +  //"Kalman Gain(K): " + K.toString() 
						  //+ "Covariance(P): " + P.toString() + 
						  "Input Location: Latitude: " + loc.getLatitude() + ", Longitude: " + loc.getLongitude() 
						  + "Corrected Prediction(estimatedState): Latitude: " + estimatedState.get(0,0) + " Longitude: " + estimatedState.get(1,0) +" ";
				  outputStream.write(kalmanData.getBytes());
				  outputStream.close();
				} catch (Exception e) {
				  e.printStackTrace();
				}
			}
		}
	}
	
	public Location predictTime(double time,String uniqueID)
	{
		double[][] temp = new double[][] {{1,time},{0,1}};
		A = new SimpleMatrix(temp);
		temp = new double[][] {{time*time/2},{time}};
		B = new SimpleMatrix(temp);	
		SimpleMatrix predState = new SimpleMatrix(2, 1);
		predState = A.mult(estimatedState).plus(B.scale(accU));
		Location output = new Location(uniqueID);
		output.setLatitude(predState.get(0, 0));
		output.setLongitude(predState.get(1,0));
//		output.setSpeed((float)estimatedState.get(2, 1));
		if(debugMode)
		{
			FileOutputStream outputStream;

			try {
			  outputStream = debugCntxt.openFileOutput("ContextText.txt", debugFileMode);
			  String kalmanData = "Prediction: " + debugCnt + "Corrected Prediction(estimatedState): Latitude: " + output.getLatitude() + " Longitude:  " + output.getLongitude() + " ";
			  outputStream.write(kalmanData.getBytes());
			  outputStream.close();
			} catch (Exception e) {
			  e.printStackTrace();
			}
		}
		return output;		
	}
}
