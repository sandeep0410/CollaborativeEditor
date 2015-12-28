package server.clientendpoints;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import data.Message;

/**
 * Client which connects to the resolver number to get the
 * 
 * @author sandeep
 *
 */
public class ResolverClient {
	Socket socket;
	OutputStream rawOut;
	InputStream rawIn;
	ObjectOutputStream out;
	ObjectInputStream in;

	public ResolverClient() {
		// Change it to respective server URL
		String host = "localhost";
		int port = 8020;
		try {
			socket = new Socket(host, port);
			System.out.println("Connected.");
			rawOut = socket.getOutputStream();
			rawIn = socket.getInputStream();
			out = new ObjectOutputStream(rawOut);
			in = new ObjectInputStream(rawIn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Message getResolverNumber(Message message) {
		Message recMsg = null;
		try {
			// sent obj
			System.out.println("Sending message to resolver server");
			out.writeObject(message);
			// waiting to receive object
			System.out.println("Waiting from resolver server");
			recMsg = (Message) in.readObject();
			System.out.println("In resolver client received: " + recMsg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return recMsg;
	}
}
