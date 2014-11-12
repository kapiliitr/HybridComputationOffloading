package communication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.NoRouteToHostException;
import java.net.Socket;

import exchangeTemplates.DataClass;

public class DataSender {
	
	public DataSender (int portNumber, DataClass dataToBeSent)
	{
        try {
        	Socket sock = new Socket(dataToBeSent.getDispatcherIP(), portNumber);
			ObjectOutputStream oOS = new ObjectOutputStream(sock.getOutputStream());
			oOS.writeObject(dataToBeSent);
			oOS.close();
			sock.close();
        } catch (IOException e) {
			// TODO Auto-generated catch block
	    	System.out.println(((NoRouteToHostException)e).getLocalizedMessage());
			e.printStackTrace();
		}      
	}
}
