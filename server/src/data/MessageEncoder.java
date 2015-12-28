package data;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * Encode the message object to JSON
 * 
 * @author sandeep
 *
 */

public class MessageEncoder implements Encoder.Text<Message> {

	@Override
	public void destroy() {
	}

	@Override
	public void init(EndpointConfig arg0) {
	}

	@Override
	public String encode(Message message) throws EncodeException {
		JSONObject obj = new JSONObject();
		obj.put("type", message.getType());
		obj.put("serverurl", message.getServerURL());
		obj.put("priority", message.getPriority());
		obj.put("message", message.getMessage());
		if(message.getServerList()!=null && message.getServerList().size()>0){
			Gson gson = new Gson();
			String json = gson.toJson(message.getServerList());
			obj.put("serverList", message.getServerList());
		}else{
			obj.put("serverList", "");
		}
		
		return obj.toString();
	}
}
