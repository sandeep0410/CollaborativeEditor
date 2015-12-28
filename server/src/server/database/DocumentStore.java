package server.database;

import java.util.HashMap;

import data.Message;

/**
 * Database to store the document updates.
 * 
 * @author sandeep
 *
 */
public class DocumentStore {
	// TODO : Maintain a hashmap and write to a file.
	private HashMap<String, HashMap<String, HashMap<Integer, Message>>> db;

	public DocumentStore() {
		db = new HashMap<>();
	}

	public void put(String app, String channel, Message message) {
		if (!db.containsKey(app))
			db.put(app, new HashMap<String, HashMap<Integer, Message>>());
		if (!db.get(app).containsKey(channel))
			db.get(app).put(channel, new HashMap<Integer, Message>());
		db.get(app).get(channel)
				.put(Integer.parseInt(message.getPriority()), message);
	}

//	public List<Message> getMessages(String app, String channel, int priority) {
//		Set<Integer> data = db.get(app).get(channel).keySet();
//		for(Integer key : ){
//			
//		}
//	}
}
