package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.json.JSONObject;

import data.Message;

/**
 * @author Sandeep
 *
 */
@ClientEndpoint
public class TestCoordinatorWebClientEndpoint {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private static CountDownLatch latch;

	@OnOpen
	public void onOpen(Session session) {
		logger.info("Connected ... " + session.getId());
	}

	@OnMessage
	public String onMessage(String message, Session session) {
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(
				System.in));
		try {
			logger.info("Received ...." + message);
			System.out.println("Waiting for input : ");
			String userInput = bufferRead.readLine();
			System.out.println("Created message..sending msg:");
			// JSONObject obj = new JSONObject();
			//
			// obj.put("sender", "client");
			// obj.put("received", new Date());
			// obj.put("operation", "test");
			// obj.put("update", userInput);

			Message newMessage = new Message("", "", "client", "date",
					"test-coordination", userInput);
			String msgTxt = jsonString(newMessage);
			return msgTxt;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("Session %s close because of %s",
				session.getId(), closeReason));
	}

	public String jsonString(Message msg) {
		JSONObject obj = new JSONObject();

		obj.put("type", msg.getType());
		obj.put("serverurl", msg.getServerURL());
		obj.put("priority", msg.getPriority());
		obj.put("message", msg.getMessage());

		return obj.toString();
	}

	public static void main(String[] args) {
		latch = new CountDownLatch(1);

		ClientManager client = ClientManager.createClient();
		try {
			System.out.println("Starting webclient...");
			client.connectToServer(
					TestCoordinatorWebClientEndpoint.class,
					new URI(
							"ws://localhost:8030/websockets/coordinatorclientupdate/paint/abc/d123"));

			// TODO : Send the JSON to the server and send back.
			// Create the JSON
			// Decide the JSON format.

			// JSONObject obj = new JSONObject();
			//
			// obj.put("sender", "client");
			// obj.put("received", new Date());
			// obj.put("operation", "test");
			// obj.put("update", "message");
			//
			// StringWriter out = new StringWriter();
			// // obj.writeJSONString(out);
			//
			// String jsonText = out.toString();

			latch.await();

		} catch (DeploymentException | URISyntaxException
				| InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
