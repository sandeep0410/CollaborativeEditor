package server.manager;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

import model.ServerDetails;
import data.Message;

/**
 * @author Sandeep
 *
 */
public class CoordinatorBackupManager {

	private Map<String, Session> appDeviceSessionMap;
	private Map<String, ServerDetails> serverDetMap;
	public CoordinatorBackupManager() {
		appDeviceSessionMap = new HashMap<>();
		serverDetMap = new HashMap<>();
	}

	public synchronized void addAppSessionToMap(String sessionKey,
			Session session) {
		appDeviceSessionMap.put(sessionKey, session);
	}

	public synchronized Session getAppSessionToMap(String sessionKey,
			Session session) {
		return appDeviceSessionMap.get(sessionKey);
	}

	public Message performClientOperation(Message message, String app,
			String channel) {
		Message replyMsg = new Message();
		switch (message.getType()) {
		case "GetServer":
			// Find which server to be returned.
			String server = getCollaborativeServer(app, channel);
			replyMsg.setServerURL(server);
			break;
		}
		return replyMsg;
	}

	/**
	 * Gets the server based on the load of the servers available.
	 * 
	 * @param app
	 * @param channel
	 * @return
	 */
	public String getCollaborativeServer(String app, String channel) {
		// TODO : Enhance this to provide the server based on the location of
		// the device.
		String minLoadServer = "localhost";
		int min = Integer.MIN_VALUE;

		for (String server : serverDetMap.keySet()) {
			int serverLoad = serverDetMap.get(server).getServerLoad();
			if (min < serverLoad) {
				minLoadServer = server;
				min = serverLoad;
			}
		}
		return minLoadServer;
		// return "localhost";
	}

	/**
	 * This performs operation like registering new server to the system, keep
	 * record of the connections open and closed.
	 * 
	 * @param message
	 * @return
	 */
	public Message performServerOperation(Message message) {
		Message replyMsg = new Message();
		switch (message.getType()) {
		case "NewServer":
			registerNewCollabServer(message.getServerURL());
			replyMsg.setMessage("Added New server");
			break;
		}
		return replyMsg;

	}

	public void registerNewCollabServer(String server) {
		serverDetMap.put(server, new ServerDetails(server));
	}
	
	public void receiveServerUpdateFromCoordinator(String server ){
		registerNewCollabServer(server);
		
	}
	
	public void receiveClientUpdateFromCoordinator(String sessionKey, Session session){
		addAppSessionToMap(sessionKey, session);
	}
}
