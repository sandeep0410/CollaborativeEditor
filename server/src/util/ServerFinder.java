/**
 * 
 */
package util;

import java.util.List;

import model.ServerDetails;
import server.manager.CoordinatorServerManager;

/**
 * @author Sandeep
 *
 */
public class ServerFinder {

	private static final int MAX_CAP = 2;

	/**
	 * @param manager
	 * @param app
	 * @param session
	 * @param clientLatitude
	 * @param clientLongitude
	 * @return
	 */
	public static String getOptimalCostServer(CoordinatorServerManager manager, String app, String session,
			double clientLatitude, double clientLongitude) {
		if(manager==null)
			System.out.println("manager is null");
		else
			System.out.println(manager);
		List<ServerDetails> sessionServer = manager.getServersforSession(app + "_" + session);
		List<ServerDetails> allServers = manager.getListOfCollabServers();

		double minCost = Double.MAX_VALUE;
		ServerDetails minCostServer = null;
		for (ServerDetails s : allServers) {
			double clientCost = calculateDistanceFromClient(s, clientLatitude, clientLongitude);
			double serverCost = addServerAndCalculateCost(s, sessionServer);
			System.out.println("clientcost: " +clientCost);
			System.out.println("serverCost: " +serverCost);
			if (minCost > (clientCost + serverCost)) {
				if (s.getServerLoad() < MAX_CAP) {
					minCost = clientCost + serverCost;
					minCostServer = s;
					System.out.println("server details: " +minCostServer);
				}
			}

		}

		return minCostServer.getServerName();

	}

	/**
	 * @param s
	 * @param sessionServer
	 * @return
	 */
	private static double addServerAndCalculateCost(ServerDetails curServer, List<ServerDetails> sessionServer) {
		double cost = 0;
		if(null == sessionServer)
			return 0;
		for (ServerDetails s : sessionServer) {
			if(s.getIP().equals(curServer.getIP()))
					continue;
			cost = cost + GeoUtil.distance(s.getServerLocation().getLatitude(), s.getServerLocation().getLongitude(),
					curServer.getServerLocation().getLatitude(), curServer.getServerLocation().getLongitude(), "K");
		}
		return cost;
	}

	/**
	 * @param clientLatitude
	 * @param clientLongitude
	 * @return
	 */
	private static double calculateDistanceFromClient(ServerDetails server, double clientLatitude,
			double clientLongitude) {
		return GeoUtil.distance(clientLatitude, clientLongitude, server.getServerLocation().getLatitude(),
				server.getServerLocation().getLongitude(), "K");

	}
}
