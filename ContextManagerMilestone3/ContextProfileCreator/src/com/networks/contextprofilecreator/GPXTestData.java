package com.networks.contextprofilecreator;

import java.util.ArrayList;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.content.Context;
import java.io.File;

public class GPXTestData {
	public ArrayList<double[]> arrGPXData = new ArrayList<double[]>();
	private int counter = 0;
	private Boolean flipDirection = false;
	
	public GPXTestData(Context objAct)
	{
        
        try {
        	File fileDirName = objAct.getFilesDir();
            InputStream inputStream = objAct.openFileInput("TestData.txt");
             
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                 
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    String[] arrTemp = receiveString.split(",");
                    double[] arrData = {Double.parseDouble(arrTemp[0]),Double.parseDouble(arrTemp[1])};
                    arrGPXData.add(arrData);                    
                }
                 
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
//            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
//            Log.e(TAG, "Can not read file: " + e.toString());
        }
	}
	
	public double[] getTestData()
	{
		if(!flipDirection)
		{
			if(counter < arrGPXData.size())
			{
				counter++;
				return arrGPXData.get(counter);				
			}
			else
			{
				counter--;
				flipDirection = true;
				return arrGPXData.get(counter);
			}
		}
		else
		{
			if(counter > -1)
			{
				counter--;
				return arrGPXData.get(counter);				
			}
			else
			{
				counter++;
				flipDirection = false;
				return arrGPXData.get(counter);
			}			
		}
	}
}
