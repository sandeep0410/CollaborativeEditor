package server.manager;

import java.util.HashMap;
import data.Message;

/**
 * Manages the resolving conflicts between the messages from different client.
 * 
 * @author sandeep
 *
 */
public class ResolverManager {

	private HashMap<String, HashMap<String, Integer>> counterMap;

	public ResolverManager() {
		counterMap = new HashMap<String, HashMap<String, Integer>>();
		// Start the new endpoint
	}

	/**
	 * Method which gives the update sequence number for any application
	 * message.
	 * 
	 * @param message
	 * @param app
	 * @param channel
	 * @return
	 */
	public void getMessagePriorityNumber(Message message) {
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
	}

}
