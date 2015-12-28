package server.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.Session;

import data.Message;
import server.manager.CollaborativeManager;
import util.Util;

/**
 * @author Sandeep
 *
 */
public class CollaborativeTCPServer extends Thread {
	ServerSocket servsocket;
	CollaborativeManager manager;

	public CollaborativeTCPServer() {
		try {
			servsocket = new ServerSocket(CbConstants.URL_DATA.RECIEVE_UPDATE_PORT);
			manager = CollaborativeManager.getInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		startListeningForUpdates();
	}

	private void startListeningForUpdates() {

		while (true) {
			Socket connectionSocket;
			try {
				System.out.println("waiting for connection on port " + servsocket.getLocalPort());
				connectionSocket = servsocket.accept();
				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));
				PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream());

				String message = "";
				String str;
				while ((str = inFromClient.readLine()) != null) {
					if (str.equals("done"))
						break;
					message = message + str;
					System.out.println(message);
				}
				System.out.println("Received: " + message);
				outToClient.println("Request Received");
				outToClient.println("done");
				outToClient.flush();
				outToClient.close();
				inFromClient.close();
				System.out.println("Collab Server Side received message: "+message);
				Message msg = Util.JSONToMessage(message);
				if (msg.getType().equals("Update Session-Servers"))
					manager.updateSessionServers(msg);
				else
					sendMessageToFriendClients(Util.JSONStringToBroadCastMessage(message));

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 
	 */
	private void sendMessageToFriendClients(Message msg) {
		String app = msg.getApp();
		String channel = msg.getChannel();
		int i=0;
		System.out.println("Will Begin to start updating the clients associated with me");
		for (Session s : CollaborativeManager.getInstance().getOpenSessions()) {
			System.out.println(i + " UserProperty app : " + s.getUserProperties().get("app")+", message value app: "+app);
			System.out.println(i + " UserProperty channel : " + s.getUserProperties().get("channel")+", message value channe: "+channel);
			System.out.println(i + " UserProperty DeviceID : " + s.getUserProperties().get("deviceID"));
			if (s.getUserProperties().get("app").equals(app) && s.getUserProperties().get("channel").equals(channel)) {
				try {
					s.getBasicRemote().sendText(Util.MessageToJSONString(msg));
				} catch (IOException e) {
					Logger.getLogger("sandeep").log(Level.WARNING, "onMessage failed", e);
				}
				i++;
			}

		}
		System.out.println("I have updated "+i+" clients. Good luck with debugging.");
	}
}