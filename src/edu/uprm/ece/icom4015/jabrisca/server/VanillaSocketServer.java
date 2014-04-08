package edu.uprm.ece.icom4015.jabrisca.server;

public abstract class VanillaSocketServer implements Runnable{
	public VanillaSocketServer() {
		initializeUsers();
	}
	public static User[] users;
	
	/**
	 * Initialize the users
	 */
	abstract void initializeUsers();
	
	
}
