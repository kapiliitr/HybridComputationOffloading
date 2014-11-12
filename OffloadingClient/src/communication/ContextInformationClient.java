package communication;
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import exchangeTemplates.ContextInformation;
import exchangeTemplates.NodeType;

public class ContextInformationClient implements Callable<LinkedList<ContextInformation>>{

	LinkedList<ContextInformation> collectedContextInfomration;
	public LinkedList<ContextInformation> getCollectedContextInfomration() {
		return collectedContextInfomration;
	}

	String broadcastIp;
	int maximumNumberOfPeers;
	
	public ContextInformationClient(String broadcast, int peersNumber)
	{
		collectedContextInfomration = new LinkedList<ContextInformation>();
		broadcastIp = broadcast;
		maximumNumberOfPeers = peersNumber;
	}
	
    private void getContextInformation() {
    	
        
        DatagramSocket socket;
		try {
			socket = new DatagramSocket();
		
	        // send request
	        byte[] buf = new byte[256];
	        InetAddress address = InetAddress.getByName(broadcastIp);
	        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 1236);
	        socket.send(packet);
	        socket.setSoTimeout(1000);
	        // get responses or timeout
	        packet = new DatagramPacket(buf, buf.length);
	        for (int i = 0; i < maximumNumberOfPeers && !Thread.currentThread().isInterrupted(); i++) {
	        	try
	        	{
	        		socket.receive(packet);
		        	// **************Populate the linked list here !!******************* //
	        		// BEWARE OF THE FORMATING OF THE MESSAGE .... WHEN CHANGED IN THE CLIENT CHANGE HERE (BroadcastMessageServer.JAVA)
	        		ContextInformation nodeFound = new ContextInformation();
	        		nodeFound.setNodeAddress(packet.getAddress());
	        		System.out.println(packet.getAddress());
	        		String machineType = new String(packet.getData(), 0, packet.getLength());
	        		
	        		if (machineType.equals("LAPTOP"))
	        		{
	        			System.out.println("NODE IS A LAPTOP");
	        			nodeFound.setNodeType(NodeType.LAPTOP);
	        		}
	        		else
	        		{
	        			System.out.println("NODE IS AN ANDROID");
	        			nodeFound.setNodeType(NodeType.AndroidPhone);
	        		}
	        		collectedContextInfomration.add(nodeFound);
	        		
	        	}
	        	catch (SocketTimeoutException e)
	        	{
	        		System.out.println("no peers found !!");
	        	}
	        	
			} 
	        socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public LinkedList<ContextInformation> call() throws Exception {
		// TODO Auto-generated method stub
		getContextInformation();
		return collectedContextInfomration;
			
	}
}