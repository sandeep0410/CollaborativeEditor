package server.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import data.Message;
import data.RegMessage;
import util.Util;

/**
 * @author Sandeep
 *
 */
public class CoordinatingTCPServer extends Thread {
	ServerSocket serversocket;

	public CoordinatingTCPServer() {
		try {
			serversocket = new ServerSocket(CbConstants.URL_DATA.REGISTRATION_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		startTCPServer();
	}

	private void startTCPServer() {
		while (true) {
			Socket connectionSocket;
			try {
				System.out.println("waiting for connection on port " +serversocket.getLocalPort());
				connectionSocket = serversocket.accept();
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream());
				
				String message="";
				String str;
				while ((str = inFromClient.readLine()) != null) {
					if(str.equals("done"))
						break;
					message = message +str;
				}
				System.out.println("Received: " + message);
				outToClient.println("Request Received");
				outToClient.println("done");
				outToClient.flush();
				outToClient.close();
				inFromClient.close();
				startRelevantRequest(connectionSocket, message);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	private void startRelevantRequest(Socket connectionSocket, String message) {
		RegMessage msg = Util.JSONToRegMessage(message);
		switch (msg.getType()){
		case "registration":
			(new RegistrationThread(connectionSocket, msg)).start();
			break;
		}
	}
}
