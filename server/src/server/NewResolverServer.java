package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.glassfish.tyrus.server.Server;

import data.Message;
import server.endpoints.ResolverServerEndPoint;
import server.manager.ResolverManager;
import util.Util;

/**
 * Server to maintain the global ordering of the updates in the document.
 * 
 * @author sandeep
 *
 */
public class NewResolverServer extends Thread {

	protected Socket s;
	protected ResolverManager resolverManager;

	public NewResolverServer(Socket s, ResolverManager resolverManager) {
		System.out.println("New collaborativeServer connected.");
		this.s = s;
		this.resolverManager = resolverManager;
	}

	public static void main(String[] args) throws IOException {

		int port = 8020;
		System.out.println("Starting on port " + port);
		ServerSocket server = new ServerSocket(8020);
		ResolverManager resolverManager = new ResolverManager();
		while (true) {
			System.out.println("Waiting for a client request");
			Socket client = server.accept();
			System.out.println("Received request from "
					+ client.getInetAddress());
			NewResolverServer s = new NewResolverServer(client, resolverManager);
			s.start();
		}
	}

	public void run() {
		try {
			InputStream istream = s.getInputStream();
			OutputStream ostream = s.getOutputStream();
			ObjectInputStream in = new ObjectInputStream(istream);
			ObjectOutputStream out = new ObjectOutputStream(ostream);
			try {
				while (true) {
					System.out
							.println("In resolver server : Waiting for message : ");
					Message m = (Message) in.readObject();
					resolverManager.getMessagePriorityNumber(m);
					System.out
							.println("In resolver server : sending the message with update num: "
									+ m);
					try {
						out.writeObject(Util.MessageToJSONString(m));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			in.close();
			System.out.println("Client exit.");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				s.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}