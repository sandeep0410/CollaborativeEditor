package server.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

import data.Message;
import model.ServerDetails;
import server.manager.CoordinatorServerManager;
import util.Util;

/**
 * @author Sandeep
 *
 */
public class CoordinatingClientTCP extends Thread {
	String type;
	Logger logger;

	public CoordinatingClientTCP(String type, String sessionID) {
		this.type = type;
		logger = Logger.getLogger(CoordinatingClientTCP.class.getName());
		switch (this.type) {
		case CbConstants.Action.UPDATE_SESSIONS:
			sendSessionServerBroadcast(sessionID);
			break;
		}
	}

	private void sendSessionServerBroadcast(String sessionID) {
		
		CoordinatorServerManager manager = CoordinatorServerManager.getInstance();
		if (manager == null)
			return;
		List<ServerDetails> servers = manager.getServersforSession(sessionID);
		logger.info("inside sendsession function");
		if (servers != null) {
			for (ServerDetails s : servers) {
				Runnable runnable = new Runnable() {

					@Override
					public void run() {
						Socket connection;
						System.out.println(s);
						try {
							connection = new Socket(s.getServerName(), CbConstants.URL_DATA.RECIEVE_UPDATE_PORT);
							PrintWriter outToServer = new PrintWriter(connection.getOutputStream(), true);
							Message msg = new Message();
							msg.setType("Update Session-Servers");
							msg.setMessage(sessionID);
							msg.setServerList(servers);
							outToServer.println(Util.MessageToJSONString(msg));
							outToServer.println("done");
							outToServer.flush();
							
							BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
							String str;
							while ((str = br.readLine()) != null) {
								if(str.equals("done"))
									break;
								System.out.println(str);
							}
							br.close();
							outToServer.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				Thread thread = new Thread(runnable);
				thread.start();
			}
		}
	}
}
