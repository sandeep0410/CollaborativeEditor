package server.endpoints;

import java.io.IOException;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import data.Message;
import server.manager.ResolverManager;
import util.Util;

/**
 * @author Sandeep
 *
 */
@ServerEndpoint(value = "/resolverserver")
public class ResolverServerEndPoint {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private ResolverManager resolverManager = new ResolverManager();

	@OnOpen
	public void onOpen(Session session) {
		logger.info("Connected ... " + session.getId());
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		// Receive the message and send back the timestamp
		// Call ResolverManager and get the number and sendback the update.
		Message recMsg = Util.JSONToMessage(message);
//		resolverManager.getMessagePriorityNumber(recMsg, recMsg.getApp(),
//				recMsg.getChannel());
		try {
			session.getBasicRemote().sendText(Util.MessageToJSONString(recMsg));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("Session %s closed because of %s",
				session.getId(), closeReason));
	}

}
