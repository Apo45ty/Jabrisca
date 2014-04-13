package edu.uprm.ece.icom4015.jabrisca.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class VanillaSocketThread implements Runnable {
	private Socket socket;
	protected PrintWriter out;
	protected BufferedReader in;
	public boolean done;
	private boolean waitingForResponse = false;
	private String response = "";

	public VanillaSocketThread(Socket socket) {
		this.socket = socket;
		try {
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected VanillaSocketThread(String hostURL, int mainPort) {
		try {
			this.socket = new Socket(hostURL, mainPort);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (UnknownHostException e) {
			// TODO Handle connection errors
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Handle connection errors
			e.printStackTrace();
		}

	}
	
	public void run() {
		try {
			while (!done) {
				String input = "";
				input = in.readLine();
				if (waitingForResponse && input != null && input.length() > 0) {
					while(!input.contains(ManagerSocketServer.END_MESSAGE_DELIMETER)){
						input+=in.readLine();
					}
					waitingForResponse = false;
					response = input;
				} else 
					main(input);

			}
		} catch(java.net.SocketException e){
			e.printStackTrace();
		} catch (Exception e) {
		
			System.out.println("Gracefully dealt with error in "
					+ getClass().getTypeName() + ",Excetion" + e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Handle connection errors
				e.printStackTrace();
			}
		}
	}


	public abstract void main(String pushedMessages);

	/**
	 * 
	 * @param message
	 * @param username
	 * @param password
	 * @return
	 */
	public String sendMessageWaitResponse(String message, String username,
			String password,String extraParameters) {
		return sendMessageWaitResponse(message + "@" + username + ","
				+ password+extraParameters);
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public String sendMessageWaitResponse(String message) {
		waitingForResponse = true;
		out.println(message);
		while (waitingForResponse){
		}
		return response;
	}

	public void sendMessage(String message) {
		out.println(message);
	}
	public boolean isConnected(){
		return socket.isConnected();
	}
}
