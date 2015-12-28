package server.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.websocket.Session;

import data.Message;
import model.ServerDetails;
import model.TimeStampedUpdate;
import server.clientendpoints.ResolverClient;
import server.tcp.CbConstants;
import util.Util;

/**
 * @author Sandeep
 *
 */
public class CollaborativeManager {

	ResolverClient resolverClient;
	private HashMap<String, HashMap<String, Integer>> counterMap;
	private DocumentStoreManager documentStoreManager;
	private Map<String, List<ServerDetails>> sessionServerMap;
	private Map<String, List<TimeStampedUpdate>> sessionUpdatesMap;
	private static CollaborativeManager _instance;
	private ServerDetails myDetails;
	private Set<Session> mOpenSessions;

	public CollaborativeManager() {
		// TODO : Need to have
		// Resolver Endpoint.
		// resolverClient = new ResolverClient();
		// Other servers Multicast endpoints
		sessionServerMap = new HashMap<>();
		documentStoreManager = new DocumentStoreManager();
		counterMap = new HashMap<String, HashMap<String, Integer>>();
		sessionServerMap = new HashMap<>();
		mOpenSessions = new HashSet<>();
		_instance = this;
		myDetails = setMyServerDetails();
	}

	/**
	 * @return
	 */
	private ServerDetails setMyServerDetails() {
		try {// TODO Auto-generated method stub
			String instanceID;
			instanceID = CbConstants.AWSUtils.retrieveInstanceId();
			String url = CbConstants.AWSUtils.getInstancePublicDnsName(instanceID);
			String ipv4 = Util.getPublicIpAddress();
			return new ServerDetails(url, ipv4);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public ServerDetails getMyServerDetails() {
		return myDetails;
	}

	public Message getResolverNumber(Message message) {
		// How would you send the JSON String.
		Message msg = resolverClient.getResolverNumber(message);
		return msg;
	}

	public static CollaborativeManager getInstance() {
		if (_instance == null){
			_instance = new CollaborativeManager();
			System.out.println("Creating new collabinstance");
		}
		return _instance;
	}

	// TODO: Add methods to multicast to other servers.
	public Message getResolverNumber(Message message, boolean inCollab) {
		// How would you send the JSON String.
		String app = message.getApp();
		String channel = message.getChannel();
		if (!counterMap.containsKey(app)) {
			counterMap.put(app, new HashMap<String, Integer>());
		}
		if (!counterMap.get(app).containsKey(channel)) {
			counterMap.get(app).put(channel, 0);
		}

		int newTimestamp = counterMap.get(app).get(channel) + 1;
		System.out.println("Setting priority...");
		message.setPriority(Integer.toString(newTimestamp));
		System.out.println("In resolver m : " + message);
		counterMap.get(app).put(channel, newTimestamp);
		return message;
	}

	public void updateSessionServers(Message msg) {
		System.out.println("Updating Sessions: " +msg.toString());
		String session = msg.getMessage();
		List<ServerDetails> serverList = msg.getServerList();
		sessionServerMap.put(session, serverList);
	}
	
	public List<ServerDetails> getSessionServers(String session){
		System.out.println("Size of Sessions Map: " +sessionServerMap.size());
		
		for(Entry<String, List<ServerDetails>> e: sessionServerMap.entrySet()){
			System.out.println("entry set: \n\tkey: "+e.getKey()+"\n\tvalue: " +e.getValue());
		}
		return sessionServerMap.get(session);
	}

	public void sessionUpdates(List<TimeStampedUpdate> updateList, String session) {
		List<TimeStampedUpdate> destination;
		if (sessionUpdatesMap.containsKey(session)) {
			destination = sessionUpdatesMap.get(session);
			if (destination != null) {
				destination.addAll(updateList);
			} else
				destination = new ArrayList<>(updateList);
		} else {
			destination = new ArrayList<>(updateList);
		}
		sessionUpdatesMap.put(session, destination);
	}

	public void addSession(Session s) {
		mOpenSessions.add(s);
	}

	public void removeSession(Session s) {
		mOpenSessions.remove(s);
	}

	public Set<Session> getOpenSessions() {
		return mOpenSessions;
	}

}
