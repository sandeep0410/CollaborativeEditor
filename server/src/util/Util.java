package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import data.Message;
import data.RegMessage;
import model.ServerDetails;

/**
 * Class to have util methods.
 * 
 * @author sandeep
 *
 */
public class Util {
	public static String getAppSessionDeviceID(String app, String channel, String deviceID) {
		return app + ":" + channel + ":" + deviceID;
	}

	/**
	 * Converts jsonString to message object
	 * 
	 * @param jsonString
	 * @return
	 */
	public static Message JSONToMessage(String jsonString) {
		Message message = new Message();
		System.out.println("Decoding : jsonMessage - " + jsonString);
		try {
			JSONObject obj = new JSONObject(jsonString);
			message.setType(obj.getString("type"));
			message.setServerURL(obj.getString("serverurl"));
			message.setPriority(obj.getString("priority"));
			message.setMessage(obj.getString("message"));
			if (message.getType().equals("Update Session-Servers") || message.getType().equals("registration")) {
				String jArray = obj.getString("serverlist");
				List<ServerDetails> list = new Gson().fromJson(jArray.toString(), new TypeToken<List<ServerDetails>>() {
				}.getType());
				message.setServerList(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}

	public static RegMessage JSONToRegMessage(String jsonString) {
		RegMessage message = new RegMessage();
		System.out.println("Decoding : jsonMessage - " + jsonString);
		try {
			JSONObject obj = new JSONObject(jsonString);
			message.setType(obj.getString("type"));
			message.setServerURL(obj.getString("serverurl"));
			message.setPriority(obj.getString("priority"));
			message.setMessage(obj.getString("message"));
			message.setIpv4(obj.getString("ipv4"));
			if (message.getType().equals("Update Session-Servers") || message.getType().equals("registration")) {
				String jArray = obj.getString("serverlist");
				List<ServerDetails> list = new Gson().fromJson(jArray.toString(), new TypeToken<List<ServerDetails>>() {
				}.getType());
				message.setServerList(list);
			}
			System.out.println("Decoded msg : " + message);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * Converts Message to JSON String
	 * 
	 * @param message
	 * @return
	 */
	public static String MessageToJSONString(Message message) {
		JSONObject obj = new JSONObject();
		obj.put("type", message.getType());
		obj.put("serverurl", message.getServerURL());
		obj.put("priority", message.getPriority());
		obj.put("message", message.getMessage());
		if (message.getType().equals("Update Session-Servers") || message.getType().equals("registration")) {
			String myjsonarray = (new Gson().toJsonTree(message.getServerList()).getAsJsonArray()).toString();
			obj.put("serverlist", myjsonarray);
		}
		return obj.toString();
	}

	public static String RegMessageToJSONString(RegMessage message) {
		JSONObject obj = new JSONObject();
		obj.put("type", message.getType());
		obj.put("serverurl", message.getServerURL());
		obj.put("priority", message.getPriority());
		obj.put("message", message.getMessage());
		if (message.getType().equals("Update Session-Servers") || message.getType().equals("registration")) {
			String myjsonarray = (new Gson().toJsonTree(message.getServerList()).getAsJsonArray()).toString();
			obj.put("serverlist", myjsonarray);
		}
		obj.put("ipv4", message.getIpv4());
		return obj.toString();
	}

	public static double distFrom(double cLat, double cLon, double sLat, double sLon) {
		double radiusEarth = 3958.75;
		double diffLat = Math.toRadians(sLat - cLat);
		double diffLon = Math.toRadians(sLon - cLon);
		double sindiffLat = Math.sin(diffLat / 2);
		double sindiffLon = Math.sin(diffLon / 2);
		double a = Math.pow(sindiffLat, 2)
				+ Math.pow(sindiffLon, 2) * Math.cos(Math.toRadians(cLat)) * Math.cos(Math.toRadians(sLat));
		double tandiff = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = radiusEarth * tandiff;

		return dist;
	}

	public static String getPublicIpAddress() throws IOException {

		String EC2Id = "";
		String inputLine;
		URL EC2MetaData = new URL("http://169.254.169.254/latest/meta-data/public-ipv4");
		URLConnection EC2MD = EC2MetaData.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(EC2MD.getInputStream()));
		while ((inputLine = in.readLine()) != null) {
			EC2Id = inputLine;
		}
		in.close();
		return EC2Id;

	}

	/**
	 * @param message
	 * @return
	 */
	public static String BroadCastMessageToJSONString(Message message) {
		JSONObject obj = new JSONObject();
		obj.put("type", message.getType());
		obj.put("serverurl", message.getServerURL());
		obj.put("priority", message.getPriority());
		obj.put("message", message.getMessage());
		obj.put("app", message.getApp());
		obj.put("channel", message.getChannel());
		return obj.toString();
	}
	
	public static Message JSONStringToBroadCastMessage(String jsonString) {
		Message message = new Message();
		System.out.println("Decoding : jsonMessage - " + jsonString);
		try {
			JSONObject obj = new JSONObject(jsonString);
			message.setType(obj.getString("type"));
			message.setServerURL(obj.getString("serverurl"));
			message.setPriority(obj.getString("priority"));
			message.setMessage(obj.getString("message"));
			message.setApp(obj.getString("app"));
			message.setChannel(obj.getString("channel"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
}
