package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
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
public class TestCollaborativeWebClientEndpoint {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private static CountDownLatch latch;

	@OnOpen
	public void onOpen(Session session) {
		logger.info("Connected ... " + session.getId());
		SendInput s = new SendInput(session);
		s.start();
		try {
			// session.getBasicRemote().sendText("start");
			Message startMessage = new Message("", "", "client", "received",
					"test-coordination", "test-start");
			String startText = jsonString(startMessage);
			System.out.println("Sending text while opening connection.. :"
					+ startText);
			session.getBasicRemote().sendText(startText);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(
				System.in));

		// Just print to the console to test
		logger.info("Received ...." + message);

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

		// StringWriter out = new StringWriter();
		// String jsonText = out.toString();
		// System.out.print(jsonText);
		// StringWriter out = new StringWriter();
		// obj.writeJSONString(out);

		return obj.toString();
	}

	public static void main(String[] args) {
		latch = new CountDownLatch(1);

		ClientManager client = ClientManager.createClient();

		try {
			System.out.println("Starting webclient...");
			client.connectToServer(
					TestCollaborativeWebClientEndpoint.class,
					new URI(
							"ws://localhost:8025/websockets/collabserver/paint/abc"));
			latch.await();

		} catch (DeploymentException | URISyntaxException
				| InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Thread to accept input from user and send to the server.
	 * 
	 * @author thirunavukarasu
	 *
	 */
	class SendInput extends Thread {
		Session s;

		public SendInput(Session s) {
			this.s = s;
		}

		public void run() {
			BufferedReader bufferRead = new BufferedReader(
					new InputStreamReader(System.in));
			while (true) {
				try {
					System.out.println("Waiting for input");
					String userInput = bufferRead.readLine();
					System.out.println("Created message..sending msg:");

					Message newMessage = new Message("", "", "client", "date",
							"test-coordination", userInput);
					String msgTxt = jsonString(newMessage);

					s.getBasicRemote().sendText(msgTxt);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

}
