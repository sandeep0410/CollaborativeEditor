package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.glassfish.tyrus.server.Server;

import server.endpoints.ResolverServerEndPoint;

/**
 * Server to maintain the global ordering of the updates in the document.
 * 
 * @author sandeep
 *
 */
public class ResolverServer {
	public static void main(String[] args) {
		runServer();
	}

	public static void runServer() {
		// TODO: pass the hostname to the program
		Server server = new Server("localhost", 8023, "/websockets",
				ResolverServerEndPoint.class);
		try {
			server.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			System.out.print("Please press a key to stop the server.");
			reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.stop();
		}
	}
}
