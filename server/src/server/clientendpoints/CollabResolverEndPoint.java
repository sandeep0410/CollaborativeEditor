package server.clientendpoints;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import server.manager.CollaborativeManager;
import data.Message;

/**
 * @author Sandeep
 *
 */
@ClientEndpoint
public class CollabResolverEndPoint {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Session appSession = null;
	private CollaborativeManager collabManager = new CollaborativeManager();

	public CollabResolverEndPoint() {
		String url = "ws://10.0.0.47:8025/websockets/resolverserver";
		URI endpointURI = null;
		try {
			endpointURI = new URI(url);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			WebSocketContainer container = ContainerProvider
					.getWebSocketContainer();
			container.connectToServer(this, endpointURI);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@OnOpen
	public void onOpen(Session session) {
		logger.info("Connected ... " + session.getId());

		// session.getBasicRemote().sendText("start");
		Message startMessage = new Message("", "", "client", "received",
				"test-collabresolverendpoint", "test-start");
		// String startText = jsonString(startMessage);
		// System.out.println("Sending text while opening connection.. :"
		// + startText);
		// session.getBasicRemote().sendText(startText);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(
				System.in));

		// read details from session
		// send back the counter number.??
		// Just print to the console to test
		logger.info("Received ...." + message);

	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("Session %s close because of %s",
				session.getId(), closeReason));
	}

	public void sendMessage(final String message) {
		appSession.getAsyncRemote().sendText(message);
	}

}
