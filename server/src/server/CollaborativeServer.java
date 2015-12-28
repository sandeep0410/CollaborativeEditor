package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.glassfish.tyrus.server.Server;

import server.endpoints.CollaborativeServerEndPoint;
import server.tcp.CbConstants;
import server.tcp.CollaborativeClientTCP;
import server.tcp.CollaborativeTCPServer;

/**
 * Server to collaborate with the clients to receive and push updates.
 * 
 * @author sandeep
 *
 */
public class CollaborativeServer {
	public static void main(String[] args) {
		// TODO : Need to access the coordinating server and get the other
		// associated servers.
		registerWithCoordinatorServer();
		runCollaborativeTCPServer();
		runServer();
	}

	/**
	 * 
	 */
	private static void runCollaborativeTCPServer() {
		(new CollaborativeTCPServer()).start();
		
	}

	private static void registerWithCoordinatorServer() {
		(new CollaborativeClientTCP(CbConstants.Action.REGISTRATION)).start();
	}

	static String getInstancePublicDnsName() throws IOException {

		String EC2Id = "";
		String inputLine;
		URL EC2MetaData = new URL("http://169.254.169.254/latest/meta-data/public-hostname");
		URLConnection EC2MD = EC2MetaData.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(EC2MD.getInputStream()));
		int i = 0;
		while ((inputLine = in.readLine()) != null) {
			EC2Id = inputLine;
		}
		in.close();
		return EC2Id;

	}

	public static void runServer() {
		// TODO: pass the hostname to the program
		Server server = null;
		try {
			server = new Server(getInstancePublicDnsName(), 8025, "/websockets", CollaborativeServerEndPoint.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			server.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Please press a key to stop the server.");
			reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.stop();
		}
	}
}
