package server.endpoints;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import server.manager.CoordinatorServerManager;
import data.Message;

/**
 * @author Sandeep
 *
 */
@ServerEndpoint(value = "/coordinatorserverupdate, encoders = MessageEncoder.class, decoders = MessageDecoder.class)")
public class CoordinaterServerUpdateEndPoint {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	CoordinatorServerManager coordinatorManager = new CoordinatorServerManager();

	@OnOpen
	public void onOpen(Session session) {
		// Set the details of this session.
		logger.info("Connected ... " + session.getId());
	}

	@OnMessage
	public void onMessage(final Session session, Message message) {
		// Receive a JSON message and check for the command.
		// Message response =
		// coordinatorManager.performServerOperation(message);
		if (session.isOpen())
			try {
				// session.getBasicRemote().sendObject(response);
				System.out.println(message);
				session.getBasicRemote().sendObject(message);
			} catch (IOException | EncodeException e) {
				logger.log(Level.WARNING, "onMessage failed", e);
			}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("Session %s closed because of %s",
				session.getId(), closeReason));
	}
}
