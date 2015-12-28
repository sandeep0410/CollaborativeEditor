package server.tcp;

import java.net.Socket;
import java.util.logging.Logger;

import data.Message;
import data.RegMessage;
import server.manager.CoordinatorServerManager;

/**
 * @author Sandeep
 *
 */
public class RegistrationThread extends Thread {
	Logger logger;
	RegMessage message;
	CoordinatorServerManager coordinatorManager;
	public RegistrationThread(Socket connectionSocket, RegMessage msg) {
		logger = Logger.getLogger(RegistrationThread.class.getName());
		message = msg;
		coordinatorManager = CoordinatorServerManager.getInstance();
	}
	@Override
	public void run() {
		logger.info(message.getType());
		logger.info(message.getServerURL());
		coordinatorManager.registerNewCollabServer(message);
		}
}
