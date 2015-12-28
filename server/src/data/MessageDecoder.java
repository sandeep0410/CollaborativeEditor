package data;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.ServerDetails;

/**
 * Decode the JSON to message Object
 * 
 * @author sandeep
 *
 */
public class MessageDecoder implements Decoder.Text<Message> {

	@Override
	public void init(EndpointConfig config) {
		// TODO Auto-generated method stub
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public Message decode(final String textMessage) {
		System.out.println("Decoding..............");
		Message message = new Message();
		System.out.println("Decoding : txMsg - " + textMessage);
		try {
			JSONObject obj = new JSONObject(textMessage);
			message.setType(obj.getString("type"));
			message.setServerURL(obj.getString("serverurl"));
			message.setPriority(obj.getString("priority"));
			message.setMessage(obj.getString("message"));
			String json = obj.getString("serverList");
			if (json != null && json.length() > 0) {
				Gson gson = new Gson();
				Type type = new TypeToken<ArrayList<Integer>>() {
				}.getType();
				ArrayList<ServerDetails> inList = gson.fromJson(json, type);
				message.setServerList(inList);
			}
			System.out.println("Decoded msg : " + message);
		} catch (Exception e) {
			
		}
		return message;
	}

	@Override
	public boolean willDecode(String s) {
		return true;
	}

}
