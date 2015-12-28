package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.glassfish.tyrus.server.Server;

import server.endpoints.CoordinaterClientUpdateEndPoint;
import server.manager.CoordinatorServerManager;
import server.tcp.CoordinatingTCPServer;

/**
 * Server to balance the load and assign servers to clients
 *
 * @author sandeep
 *
 */
public class CoordinatorServer {
	public static void main(String[] args) {
		(new CoordinatingTCPServer()).start();
		runServer();
		
	}
	static String getInstancePublicDnsName() throws IOException {

		String EC2Id = "";
		String inputLine;
		URL EC2MetaData = new URL(
				"http://169.254.169.254/latest/meta-data/public-hostname");
		URLConnection EC2MD = EC2MetaData.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				EC2MD.getInputStream()));
		while ((inputLine = in.readLine()) != null) {
			EC2Id = inputLine;
		}
		in.close();
		return EC2Id;

	}
	public static void runServer() {
		// TODO: pass the hostname to the program
		Server clientUpdateCoordinator = null;
		//initializing CoordinatorServerMnagaer;
		new CoordinatorServerManager();
		try {
			clientUpdateCoordinator = new Server(getInstancePublicDnsName(), 8030,
					"/websockets", CoordinaterClientUpdateEndPoint.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Server serverUpdateCoordinator = new Server("localhost", 8031,
		// "/websockets", CoordinaterServerUpdateEndPoint.class);
		try {
			clientUpdateCoordinator.start();
			// serverUpdateCoordinator.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			System.out.print("Please press a key to stop the server.");
			reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			clientUpdateCoordinator.stop();
			// serverUpdateCoordinator.stop();
		}
	}
}
