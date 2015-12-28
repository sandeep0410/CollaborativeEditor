package server.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Sandeep
 *
 */
public class CbConstants {
	public static class Action {
		public static final String REGISTRATION = "Registration";
		public static final String BROADCAST_UPDATES = "Broadcast Updates";
		public static final String UPDATE_SESSIONS = "Update Sessions";
	}

	public static class URL_DATA {
		public static final String COORDINATE_URL = "ec2-52-27-184-140.us-west-2.compute.amazonaws.com";
		public static final int REGISTRATION_PORT = 50000;
		public static final int RECIEVE_UPDATE_PORT = 50001;
	}

	public static class AWSUtils {
		public static String retrieveInstanceId() throws Exception {
			String EC2Id = "";
			String inputLine;
			URL EC2MetaData = new URL("http://169.254.169.254/latest/meta-data/instance-id");
			URLConnection EC2MD = EC2MetaData.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(EC2MD.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
				EC2Id = inputLine;
			}
			in.close();
			return EC2Id;
		}

		public static String getInstancePublicDnsName(String instanceID) throws IOException {

			String EC2Id = "";
			String inputLine;
			URL EC2MetaData = new URL("http://169.254.169.254/latest/meta-data/public-hostname");
			URLConnection EC2MD = EC2MetaData.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(EC2MD.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
				EC2Id = inputLine;
			}
			in.close();
			return EC2Id;

		}

	}
}
