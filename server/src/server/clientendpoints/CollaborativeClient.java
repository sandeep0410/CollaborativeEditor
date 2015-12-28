package server.clientendpoints;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;

import util.Util;
import data.Message;

/**
 * @author Sandeep
 *
 */
@ClientEndpoint
public class CollaborativeClient {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	String url;

	public CollaborativeClient() throws Exception {
		String instanceID = retrieveInstanceId();
		logger.info("sandeep " + instanceID);
		url = getInstancePublicDnsName(instanceID);
		url = "ws://" + url + ":8025";
		logger.info("sandeep" + url);
		String coordinatorURL = "ws://ec2-52-89-146-237.us-west-2.compute.amazonaws.com:8030/websockets/coordinatorclientupdate/null/null/null";
		ClientManager client = ClientManager.createClient();
		try {
			client.connectToServer(this, new URI(coordinatorURL));

		} catch (DeploymentException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public String retrieveInstanceId() throws Exception {
		String EC2Id = "";
		String inputLine;
		URL EC2MetaData = new URL(
				"http://169.254.169.254/latest/meta-data/instance-id");
		URLConnection EC2MD = EC2MetaData.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				EC2MD.getInputStream()));
		int i = 0;
		while ((inputLine = in.readLine()) != null) {
			EC2Id = inputLine;
			logger.info("sandeep " + i + " " + inputLine);
		}
		in.close();
		return EC2Id;
	}

	@OnOpen
	public void OnOpen(Session session) {
		logger.info("Send the registration to coordinating server");
		Message msg = new Message();
		msg.setType("registration");
		msg.setServerURL(url);
		try {
			session.getBasicRemote().sendText(Util.MessageToJSONString(msg));
			logger.info("Message Sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	String getInstancePublicDnsName(String instanceId) throws IOException {

		String EC2Id = "";
		String inputLine;
		URL EC2MetaData = new URL(
				"http://169.254.169.254/latest/meta-data/public-hostname");
		URLConnection EC2MD = EC2MetaData.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				EC2MD.getInputStream()));
		int i = 0;
		while ((inputLine = in.readLine()) != null) {
			EC2Id = inputLine;
			logger.info("sandeep " + i + " " + inputLine);
		}
		in.close();
		return EC2Id;

	}
}
