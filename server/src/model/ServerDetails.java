package model;

import java.util.HashMap;
import java.util.Map;

import util.GeoLocation;
import util.GeoUtil;

/**
 * @author Sandeep
 *
 */
public class ServerDetails {
	private String serverName;
	private String ipv4;
	private GeoLocation locationDetails;
	Map<String, Map<String, Integer>> appSessionCount;

	public ServerDetails(String serverName) {
		this.serverName = serverName;
		ipv4=null;
		appSessionCount = new HashMap<>();
	}
	public ServerDetails(String serverName, String ip) {
		this.serverName = serverName;
		this.ipv4 = ip;
		this.locationDetails = GeoUtil.findLocation(ipv4);
		appSessionCount = new HashMap<>();
	}

	/**
	 * Gives the total number of clients connected to this server.
	 * 
	 * @return
	 */
	public int getServerLoad() {
		int load = 0;
		for (String app : appSessionCount.keySet())
			load += getAppLoadInServer(app);
		return load;
	}

	/**
	 * Gives number of clients for specified app is assigned to this server.
	 * 
	 * @param app
	 * @return
	 */
	public int getAppLoadInServer(String app) {
		if (!appSessionCount.containsKey(app))
			return 0;
		return appSessionCount.get(app).size();
	}

	/**
	 * Increment the count of the app and the client.
	 * 
	 * @param app
	 * @param session
	 */
	public void addAppSession(String app, String session) {
		if (!appSessionCount.containsKey(app))
			appSessionCount.put(app, new HashMap<String, Integer>());
		if (!appSessionCount.get(app).containsKey(session))
			appSessionCount.get(app).put(session, 1);
		else
			appSessionCount.get(app).put(session,
					appSessionCount.get(app).get(session) + 1);
	}

	/**
	 * Increment the count of the app and the client.
	 * 
	 * @param app
	 * @param session
	 */
	public void removeAppSession(String app, String session) {
		if (appSessionCount.containsKey(app)
				&& appSessionCount.get(app).containsKey(session)) {
			appSessionCount.get(app).put(session,
					appSessionCount.get(app).get(session) - 1);
			if (appSessionCount.get(app).get(session) <= 0)
				appSessionCount.get(app).remove(session);
		}
	}

	/**
	 * Gets the current server name.
	 * 
	 * @return
	 */
	public String getServerName() {
		return serverName;
	}
	/**
	 * @return
	 */
	public String getIP() {
		// TODO Auto-generated method stub
		return ipv4;
	}
	
	/**
	 * getSeverLocatioDetails
	 */
	
	public GeoLocation getServerLocation(){
		return locationDetails;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return serverName;
	}

}
