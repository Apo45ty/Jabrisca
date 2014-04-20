package edu.uprm.ece.icom4015.jabrisca.server;

public abstract class VanillaSocketServer implements Runnable{
	public VanillaSocketServer() {
		initializeUsers();
	}
	public User[] users;
	
	/**
	 * Initialize the users
	 */
	abstract void initializeUsers();
	
	/**
	 * @return
	 */
	public User[] getUsers(){
		return users;
	}
	
}
