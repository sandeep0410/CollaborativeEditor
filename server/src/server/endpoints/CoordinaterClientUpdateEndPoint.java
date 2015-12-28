package server.endpoints;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

import data.Message;
import data.MessageDecoder;
import data.MessageEncoder;
import model.ServerDetails;
import server.manager.CoordinatorServerManager;
import util.GeoLocation;
import util.GeoUtil;
import util.Util;

/**
 * @author Sandeep
 *
 */
@ServerEndpoint(value = "/coordinatorclientupdate/{app}/{channel}/{deviceID}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class CoordinaterClientUpdateEndPoint {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	CoordinatorServerManager coordinatorManager;
		
	@OnOpen
	public void onOpen(Session session, @PathParam("app") final String app, @PathParam("channel") final String channel,
			@PathParam("deviceID") final String deviceID) {
		coordinatorManager = CoordinatorServerManager.getInstance();
		logger.info(" Opening connection for Application : " + app + " Channel :" + channel + "DeviceID : " + deviceID);
		if (null != deviceID) {
			session.getUserProperties().put("app", app);
			session.getUserProperties().put("channel", channel);
			session.getUserProperties().put("deviceID", deviceID);
			String key = Util.getAppSessionDeviceID(app, channel, deviceID);

		}
		logger.info("Connected ... " + session.getId());
	}

	@OnMessage
	public void onMessage(final Session session, String message) {
		/*********************************** test ***********************/
		// temporarily changed message to sString
		logger.info(message);
		JSONObject json = new JSONObject(message);
		if (json.getString("type").equals("close"))
			try {
				session.close();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		double latitude = json.getDouble("latitude");
		double longitude = json.getDouble("longitude");
		List<ServerDetails> servers = coordinatorManager.getListOfCollabServers();
		for (ServerDetails s : servers) {
			logger.info("server name: " + s.getServerName());
			logger.info("server ip: " + s.getIP());
			logger.info("distance from client: " + findDistance(s, latitude, longitude));
		}
		/*********************************** test ***********************/
		Message response = coordinatorManager.performClientOperation(session.getUserProperties().get("app").toString(),
				session.getUserProperties().get("channel").toString(), session.getUserProperties().get("deviceID").toString(), latitude, longitude);

		try {
			logger.info(Util.MessageToJSONString(response));
			session.getBasicRemote().sendText(Util.MessageToJSONString(response));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param s
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	private String findDistance(ServerDetails s, double latitude, double longitude) {
		GeoLocation loc = null;

		try {
			loc = GeoUtil.findLocation(InetAddress.getByName(s.getIP()).toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// logger.info(loc.getLoc()+" " +loc.getLatitude()+"
		// "+loc.getLongitude());
		return Double.toString(Util.distFrom(loc.getLatitude(), loc.getLongitude(), latitude, longitude));
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
	}
}
