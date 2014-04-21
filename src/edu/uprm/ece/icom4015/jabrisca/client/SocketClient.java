package edu.uprm.ece.icom4015.jabrisca.client;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import edu.uprm.ece.icom4015.jabrisca.server.VanillaSocketThread;

public class SocketClient extends VanillaSocketThread {
	BlockingQueue instructionsToModel;
	private String name;

	/**
	 * 
	 * @param hostURL
	 * @param mainPort
	 * @param instructions
	 */
	public SocketClient(String hostURL, int mainPort,
			BlockingQueue instructions, String name) {
		this(hostURL, mainPort);
		this.instructionsToModel = instructions;
		// Start Thread
		Thread a =new Thread(this);
		a.start();
		a.setName(name+"_Client");
		this.name = name;
	}

	/**
	 * @param hostURL
	 * @param mainPort
	 */
	private SocketClient(String hostURL, int mainPort) {
		super(hostURL, mainPort);
	}

	/**
	 * Listen server push request
	 */
	@Override
	public void main(String pushedMessages) {
		try {
			instructionsToModel.put(name + "-" + pushedMessages);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void dispose() {
		done = true;
		instructionsToModel = null;
		
	}

}
