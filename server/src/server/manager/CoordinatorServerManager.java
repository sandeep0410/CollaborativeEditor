package server.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import data.Message;
import data.RegMessage;
import model.ServerDetails;
import server.tcp.CbConstants;
import server.tcp.CoordinatingClientTCP;
import util.ServerFinder;

/**
 * Service that manages the coordinator server
 * 
 * @author sandeep
 *
 */
public class CoordinatorServerManager {
	private Map<String, List<ServerDetails>> sessionServersMap;
	private Map<String, ServerDetails> serverDetailsMap;
	private ResolverManager resolverManager;
	private static CoordinatorServerManager _instance;

	public CoordinatorServerManager() {
		sessionServersMap = new HashMap<>();
		serverDetailsMap = new HashMap<>();
		resolverManager = new ResolverManager();
		_instance = this;
		Logger.getLogger("sandeep").info("constructor of CoordinatorManager called");
	}

	public static CoordinatorServerManager getInstance() {
		return _instance;
	}

	public Message performClientOperation(String app, String channel, String deviceID) {
		Message replyMsg = new Message();
		// Find which server to be returned.
		String server = getCollaborativeServer(app, channel, deviceID);
		replyMsg.setType("URL");
		replyMsg.setServerURL(server);
		return replyMsg;
	}

	/**
	 * Gets the server based on the load of the servers available.
	 * 
	 * @param app
	 * @param channel
	 * @param deviceID
	 * @return
	 */
	public String getCollaborativeServer(String app, String channel, String deviceID) {
		// TODO : Enhance this to provide the server based on the location of
		// the device.
		String minLoadServer = "localhost";
		int min = Integer.MIN_VALUE;
		Logger.getLogger("sandeep").info("**********Just before for loop: " + serverDetailsMap.size());
		for (String server : serverDetailsMap.keySet()) {
			Logger.getLogger("sandeep").info("**********Entering for loop: " + serverDetailsMap.size());
			int serverLoad = serverDetailsMap.get(server).getServerLoad();
			Logger.getLogger("SANDEEP").info(server + " " + serverLoad);
			if (min < serverLoad) {
				minLoadServer = server;
				min = serverLoad;
			}
		}
		updateLoadOfServers(minLoadServer, app, channel);
		updateSessionsServers(app, channel, serverDetailsMap.get(minLoadServer));
		String outputUrl = minLoadServer + ":8025/websockets/collabserver/" + app + "/" + channel + "/" + deviceID;
		return outputUrl;
		// return "localhost";
	}
	
	public String getCollaborativeServer(String app, String channel, String deviceID, double latitude, double longitude) {
		String minLoadServer = "localhost";
		Logger.getLogger("sandeep").info("**********Just before for loop: " + serverDetailsMap.size());
		minLoadServer = ServerFinder.getOptimalCostServer(this, app, channel, latitude, longitude);
		updateLoadOfServers(minLoadServer, app, channel);
		updateSessionsServers(app, channel, serverDetailsMap.get(minLoadServer));
		String outputUrl = minLoadServer + ":8025/websockets/collabserver/" + app + "/" + channel + "/" + deviceID;
		Logger.getLogger("sandeep").info("****Best chosen server and the url generated is " +outputUrl);
		return outputUrl;
	}

	private void updateSessionsServers(String app, String channel, ServerDetails minLoadServer) {
		String key = app + "_" + channel;
		if (sessionServersMap.containsKey(key)) {
			List<ServerDetails> serverList = sessionServersMap.get(key);
			serverList.add(minLoadServer);
			sessionServersMap.put(key, serverList);
		} else {
			List<ServerDetails> serverList = new ArrayList<>();
			serverList.add(minLoadServer);
			sessionServersMap.put(key, serverList);
		}
		(new CoordinatingClientTCP(CbConstants.Action.UPDATE_SESSIONS, key)).start();
	}

	private void updateLoadOfServers(String minLoadServer, String app, String session) {
		ServerDetails details = serverDetailsMap.get(minLoadServer);
		details.addAppSession(app, session);
		serverDetailsMap.put(minLoadServer, details);
	}

	/**
	 * This performs operation like registering new server to the system, keep
	 * record of the connections open and closed.
	 * 
	 * @param message
	 * @return
	 */
/*	public Message performServerOperation(Message message) {
		Message replyMsg = new Message();
		switch (message.getType()) {
		case "NewServer":
			registerNewCollabServer(message.getMessage());
			replyMsg.setMessage("Added New server");
			break;
		// case "clientClosed":
		// // Update server details.
		// break;
		}
		return replyMsg;

	}*/

	public void registerNewCollabServer(RegMessage message) {
		String server = message.getServerURL();
		String ip = message.getIpv4();
		serverDetailsMap.put(server, new ServerDetails(server, ip));
		Logger.getLogger("sandeep")
				.info("registered new collab server: size " + server + " " + serverDetailsMap.size());

	}

	public List<ServerDetails> getServersforSession(String sessionID) {
		if (sessionServersMap.containsKey(sessionID))
			return sessionServersMap.get(sessionID);
		else
			return null;
	}
	
	public List<ServerDetails> getListOfCollabServers(){
		List<ServerDetails> list = new ArrayList<>();
		list.addAll(serverDetailsMap.values());
		return list;
	}

	/**
	 * @param string
	 * @param string2
	 * @param string3
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public Message performClientOperation(String app, String channel, String deviceID, double latitude,
			double longitude) {
		Message replyMsg = new Message();
		// Find which server to be returned.
		String server = getCollaborativeServer(app, channel, deviceID, latitude, longitude);
		replyMsg.setType("URL");
		replyMsg.setServerURL(server);
		return replyMsg;
	}

}
