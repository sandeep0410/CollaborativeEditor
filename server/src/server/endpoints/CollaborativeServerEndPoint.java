package server.endpoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

import com.google.gson.Gson;

import data.Message;
import data.MessageDecoder;
import data.MessageEncoder;
import model.ServerDetails;
import server.manager.CollaborativeManager;
import server.tcp.CbConstants;
import server.tcp.CollaborativeClientTCP;
import util.Util;

/**
 * @author Sandeep
 *
 */
@ServerEndpoint(value = "/collabserver/{application}/{channel}/{deviceID}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class CollaborativeServerEndPoint {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Session appUserSession = null;
	private CollaborativeManager collaborativeManager = CollaborativeManager.getInstance();

	@OnOpen
	public void onOpen(Session session, @PathParam("application") String application,
			@PathParam("channel") final String channel, @PathParam("deviceID") final String deviceID) {
		System.out.println("----> Connection established : " + application + "/" + channel + "/" + deviceID);
		session.getUserProperties().put("app", application);
		session.getUserProperties().put("channel", channel);
		session.getUserProperties().put("deviceID", deviceID);
		this.appUserSession = session;
		collaborativeManager.addSession(session);
		logger.info("Connected ... " + session.getId());
		buildBatchAndSendJson(session);
		
	}

	private void buildBatchAndSendJson(Session session) {
		session.getUserProperties().put(
                "org.apache.tomcat.websocket.BLOCKING_SEND_TIMEOUT",
                Long.valueOf(5000));
		Basic remote = session.getBasicRemote();
		try {
			remote.setBatchingAllowed(true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        String s = "{\"message\":\"{\\\"deleteCharacter\\\":false,\\\"erase\\\":false,\\\"label\\\":\\\"end\\\",\\\"mIsAntiAlias\\\":true,\\\"mMotionEvent\\\":2,\\\"mPaintColor\\\":-10092544,\\\"mStrokeCap\\\":\\\"ROUND\\\",\\\"mStrokeJoin\\\":\\\"ROUND\\\",\\\"mStrokeWidth\\\":60.0,\\\"mStyle\\\":\\\"STROKE\\\",\\\"mXPos\\\":688.0,\\\"mYPos\\\":336.0,\\\"messageType\\\":11,\\\"startTime\\\":\\\"1451190790155\\\",\\\"type\\\":0}\",\"priority\":\"0\",\"type\":\"message\",\"serverurl\":\"none\"}";
        JSONObject j = new JSONObject(s);
        String msg = j.getString("message");
        j.put("message1", msg);
        j.put("message2", msg);

        
        
        List<String> list = new ArrayList<>();
        /*for(int i=0; i<3; i++){
            list.add(s);
        }*/
        JSONObject object = new JSONObject();
        object.put("priority", "none");
        object.put("type", "message");
        object.put("serverurl", "none");
        String myjsonarray = (new Gson().toJsonTree(list).getAsJsonArray()).toString();
        object.put("message", myjsonarray);
        String string = null;
			string = j.toString();
		
        for(int i=0; i<40; i++){
        try {
            remote.sendText(string);
            Thread.sleep(200);
            logger.info("sending: "+i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
        try {
			remote.flushBatch();
			//remote.setBatchingAllowed(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	@OnMessage
	public void onMessage(String message, Session session) {

		// TODO: Receive the message from client

		System.out.println("Received message : " + message);
		Message recMsg = Util.JSONToMessage(message);

		if (recMsg.getType().equals("close")) {
			try {
				session.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (recMsg.getType().equals("connection"))
			return;
		recMsg.setApp((String) session.getUserProperties().get("app"));
		recMsg.setChannel((String) session.getUserProperties().get("channel"));
		Message msgWithUpdateNumber = recMsg;

		// Message msgWithUpdateNumber = collaborativeManager.getResolverNumber(
		// recMsg, true);

		// Store and Multicast

		// receive the timestamp counter.
		// Store in the datastore.
		// Multicast the message.
		//System.out.println("In collab server Received update number : " + msgWithUpdateNumber);
		int i = 1;
		System.out.println("Printing session Size: " + (collaborativeManager.getOpenSessions().size()));
		Set<String> devices = new HashSet<>();
		for (Session s : collaborativeManager.getOpenSessions()) {
			if (!s.equals(session)) {
				if (devices.contains(s.getUserProperties().get("deviceID") + "_" + s.getUserProperties().get("app")
						+ "_" + s.getUserProperties().get("channel"))) {
					try {
						s.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Found true:          ");
					continue;
				} else {
					devices.add(s.getUserProperties().get("deviceID") + "_" + s.getUserProperties().get("app") + "_"
							+ s.getUserProperties().get("channel"));
				}

			}
			System.out.println(i + " UserProperty app : " + s.getUserProperties().get("app"));
			System.out.println(i + " UserProperty channel : " + s.getUserProperties().get("channel"));
			System.out.println(i + " UserProperty DeviceID : " + s.getUserProperties().get("deviceID"));
			if (!(s.getUserProperties().get("deviceID").equals(session.getUserProperties().get("deviceID")))) {
				System.out.println(" " + s.getUserProperties().get("deviceID") + " NOT EQUAL "
						+ session.getUserProperties().get("deviceID"));
				System.out.println(i + "UserProperty app : " + s.getUserProperties().get("app"));
				System.out.println(i + "UserProperty channel : " + s.getUserProperties().get("channel"));
				// Send message to the respective clients.
				if (msgWithUpdateNumber.getApp().equals(s.getUserProperties().get("app"))
						&& msgWithUpdateNumber.getChannel().equals(s.getUserProperties().get("channel"))) {
					try {
						// session.getBasicRemote().sendObject(response);
						System.out.println("In server : " + msgWithUpdateNumber.toString());
						s.getBasicRemote().sendText(Util.MessageToJSONString(msgWithUpdateNumber));
					} catch (IOException e) {
						logger.log(Level.WARNING, "onMessage failed", e);
					}
				}

			}
			i++;
		}
		sendUpdateToFriendServers(msgWithUpdateNumber.getApp()+"_"+msgWithUpdateNumber.getChannel(), msgWithUpdateNumber);
		// return message;
	}

	private void sendMessage(final Message message) {
		for (Session s : appUserSession.getOpenSessions()) {
			if (s.isOpen()) {
				// Send message to the respective clients.
				if (message.getApp().equals(s.getUserProperties().get("app"))
						&& message.getChannel().equals(s.getUserProperties().get("channel"))) {
					try {
						// session.getBasicRemote().sendObject(response);
						System.out.println("In server : " + message.toString());
						s.getBasicRemote().sendText(Util.MessageToJSONString(message));
					} catch (IOException e) {
						logger.log(Level.WARNING, "onMessage failed", e);
					}
				}

			}
		}
	}

	private void sendUpdateToFriendServers(String appSession, Message message) {
		List<ServerDetails> servers = collaborativeManager.getSessionServers(appSession);
		System.out.println(servers.size());
		for (ServerDetails s : servers) {
			System.out.println("collaborativeManager.getMyServerDetails().getServerName(): " +collaborativeManager.getMyServerDetails().getServerName());
			System.out.println("s.getServerName(): "+s.getServerName());
			if (collaborativeManager.getMyServerDetails().getServerName().equals(s.getServerName()))
				continue;
			(new CollaborativeClientTCP(CbConstants.Action.BROADCAST_UPDATES, s.getServerName(),message)).start();
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
		collaborativeManager.removeSession(session);
	}
}
