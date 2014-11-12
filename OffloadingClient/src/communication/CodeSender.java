package communication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.NoRouteToHostException;
import java.net.ServerSocket;
import java.net.Socket;

import exchangeTemplates.DataClass;

public class CodeSender {
	public CodeSender(String FILE_TO_SEND, int portNumber, String serverIp, long taskID, String myIP)
	{
		FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    OutputStream os = null;
	    ServerSocket servsock = null;
	    Socket sock = null;
	    try {
	    	System.out.println(serverIp);
	    	sock = new Socket(serverIp, portNumber);
	    	
	      
	        System.out.println("Connecting...");
	        try {
	          
	          ObjectOutputStream oOS = new ObjectOutputStream(sock.getOutputStream());
	          oOS.writeObject(new String("Add"));
	          
	          DataClass aID = new DataClass();
	          aID.setIntegerParamters(new int[]{1,2});
	          aID.setTaskID(taskID);
	          aID.setDispatchTime(System.currentTimeMillis());
	          aID.setDispatcherIP(myIP);
	          oOS.writeObject(aID);
	          // send file
	          File myFile = new File (FILE_TO_SEND);
	          byte [] mybytearray  = new byte [(int)myFile.length()];
	          fis = new FileInputStream(myFile);
	          bis = new BufferedInputStream(fis);
	          bis.read(mybytearray,0,mybytearray.length);
	          os = sock.getOutputStream();
	          System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
	          os.write(mybytearray,0,mybytearray.length);
	          os.flush();
	          System.out.println("Done.");
	        }
	        finally {
	          if (bis != null) bis.close();
	          if (os != null) os.close();
	          if (sock!=null) sock.close();
	        }
	    } catch (IOException e) {
			// TODO Auto-generated catch block
	    	//System.out.println(((NoRouteToHostException)e).getLocalizedMessage());
			e.printStackTrace();
		}
	    finally {
	      if (servsock != null)
			try {
				servsock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}
