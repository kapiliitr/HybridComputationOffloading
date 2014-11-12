package com.networks.contextprofilecreator;

import org.ejml.simple.SimpleMatrix;
import android.location.Location;

public class KalmanFilterMobility {

	//Equations Used:
	//State:
	//x(t)=Ax'(t-1)+Bu(t)
	//A=[{1,t},{0,1}] 
	//B=[t^2/2,t]
	//C=[1,0]
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
	private SimpleMatrix C = new SimpleMatrix(1, 2); 	//measurement matrix
	private double accU = 1;							//Acceleration defaulted to 1
	private SimpleMatrix initState = new SimpleMatrix(2, 2); //Initialize initial position to 0,0
	private SimpleMatrix estimatedState = initState; //Make the estimate and measured state same
	private double minCovarX = 0.01;				 //Min covariance to move X along.
	private double minCovarZ = 10;					 //Min covariance to move Z along.
	private SimpleMatrix P;							 //position variance
	
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
		double[][] temp = new double[][] {{1,time},{0,1}};
		A = new SimpleMatrix(temp);
		temp = new double[][] {{time*time/2},{time}};
		B = new SimpleMatrix(temp);
		temp = new double[][] {{1,0}};
		C = new SimpleMatrix(temp);
		temp = new double[][] {{time*time*time*time/4,time*time*time/2},{time*time*time/2,time*time}};
		SimpleMatrix Ex = new SimpleMatrix(temp).scale(minCovarX*minCovarX); 		 //position noise conversion to Covariance matrix
		if(P == null)
		{
			P = Ex;
		}	
		temp = new double[][]{{minCovarZ*minCovarZ}};
		SimpleMatrix Ez = new SimpleMatrix(temp);
		estimatedState = A.mult(estimatedState).plus(B.scale(accU));
		P = A.mult(P).mult(A.transpose()).plus(Ex);
		SimpleMatrix K = new SimpleMatrix();
		K = P.mult(C.transpose()).mult(C.mult(P).mult(C.transpose()).plus(Ez).invert());
		temp = new double[][] {{loc.getLatitude(),loc.getLongitude()},{loc.getSpeed(),0}};
		SimpleMatrix tempLoc = new SimpleMatrix(temp);
		estimatedState = estimatedState.plus(K.mult(tempLoc).minus(C.mult(estimatedState)));
		P = (SimpleMatrix.identity(2).minus(C.mult(C))).mult(P);
	}
	
	public Location predictTime(double time,String uniqueID)
	{
		double[][] temp = new double[][] {{1,time},{0,1}};
		A = new SimpleMatrix(temp);
		temp = new double[][] {{time*time/2},{time}};
		B = new SimpleMatrix(temp);
		temp = new double[][] {{1,0}};		
		estimatedState = A.mult(estimatedState).plus(B.scale(accU));
		Location output = new Location(uniqueID);
		output.setLatitude(estimatedState.get(1, 1));
		output.setLongitude(estimatedState.get(1, 2));
		output.setSpeed((float)estimatedState.get(2, 1));
		return output;		
	}
}
