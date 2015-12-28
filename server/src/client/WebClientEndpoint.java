package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.json.JSONException;
import org.json.JSONObject;

@ClientEndpoint
public class WebClientEndpoint {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private static CountDownLatch latch;
	Session session;
	boolean close = true;
	public WebClientEndpoint(String url){

		logger.info("sandeep"+url);

		ClientManager client = ClientManager.createClient();
		try {
			client.connectToServer(this, new URI(url));


		} catch ( Exception e) {
			e.printStackTrace();
		}

	}
	@OnOpen
	public void onOpen(Session session) {
		logger.info("Connected ... " + session.getId());
		this.session = session;
		close=false;
		JSONObject json = new JSONObject();
		json.put("latitude", 44.975533);
		json.put("longitude", -93.269511);
		try {
			sendMessage(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@OnMessage
	public void onMessage(String message, Session session) {
		logger.info("Received ...." + message);
		/*try {
			final JSONObject json = new JSONObject(message);
			if(json.getString("type").equals("message")){
					else{
					Thread t  = new Thread(new Runnable() {

						@Override
						public void run() {
							//MainActivity.getInstance().receivedQueue.add(Utils.readJSON(json.toString()));

						}
					});
					t.start();
				}
			}else if(json.getString("type").equals("URL")){
				logger.info("sandeep"+json.getString("serverurl"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}


	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("Session %s close because of %s",
				session.getId(), closeReason));
		close = true;
	}

	public void sendMessage(JSONObject message){
		try {
			session.getBasicRemote().sendObject(message.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isClosed(){
		return close;
	}

}
