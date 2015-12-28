package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.google.gson.Gson;

import util.GeoLocation;

/**
 * @author Sandeep
 *
 */
public class ClientTest {
	private static void buildBatchAndSendJson(WebClientEndpoint client) {
        String s = "{\"message\":\"{\\\"deleteCharacter\\\":false,\\\"erase\\\":false,\\\"label\\\":\\\"end\\\",\\\"mIsAntiAlias\\\":true,\\\"mMotionEvent\\\":2,\\\"mPaintColor\\\":-10092544,\\\"mStrokeCap\\\":\\\"ROUND\\\",\\\"mStrokeJoin\\\":\\\"ROUND\\\",\\\"mStrokeWidth\\\":60.0,\\\"mStyle\\\":\\\"STROKE\\\",\\\"mXPos\\\":688.0,\\\"mYPos\\\":336.0,\\\"messageType\\\":11,\\\"startTime\\\":\\\"1451190790155\\\",\\\"type\\\":0}\",\"priority\":\"0\",\"type\":\"message\",\"serverurl\":\"none\"}";
        List<String> list = new ArrayList<>();
        for(int i=0; i<3; i++){
            list.add(s);
        }
        JSONObject object = new JSONObject();
        try {
            object.put("priority", "none");
            object.put("type", "message");
            object.put("serverurl", "none");
            String myjsonarray = (new Gson().toJsonTree(list).getAsJsonArray()).toString();
            object.put("message", myjsonarray);
            if(client.isClosed())
                client=new WebClientEndpoint("ws://ec2-52-32-128-100.us-west-2.compute.amazonaws.com:8025/websockets/collabserver/PAINT_APP/md899sf1473caqu9isgr9oj271/2c9b34b14750149c");
            Thread.sleep(50);
            client.sendMessage(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static void main(String[] args) {
	/*	GeoLocation loc = null;
		try {
			loc = GeoIPv4.getLocation(InetAddress.getByName("54.169.240.164"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(loc.toString());*/
	WebClientEndpoint client = new WebClientEndpoint("ws://ec2-52-32-128-100.us-west-2.compute.amazonaws.com:8025/websockets/collabserver/PAINT_APP/md899sf1473caqu9isgr9oj271/2c9b34b14750149c");
	//buildBatchAndSendJson(client);
	Scanner sc = new Scanner(System.in);
	sc.next();
	/*	String line = "";
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(
				"http://ipinfo.io/52.32.128.100");
			getRequest.addHeader("accept", "application/json");

			HttpResponse response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
				   + response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(
	                         new InputStreamReader((response.getEntity().getContent())));

			String output;
			
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				line = line+output;
			}
			System.out.println(line);
			httpClient.getConnectionManager().shutdown();

		  } catch (ClientProtocolException e) {
		
			e.printStackTrace();

		  } catch (IOException e) {
		
			e.printStackTrace();
		  }
		
		Gson gson = new Gson();
       
		GeoLocation b = gson.fromJson( line, GeoLocation.class ); 
		b.setLocation();
		System.out.println("Printing values");
		System.out.println(b.getCity());
		System.out.println(b.getLoc());
		System.out.println(b.getLatitude());
		System.out.println(b.getLongitude());*/
	}
}
