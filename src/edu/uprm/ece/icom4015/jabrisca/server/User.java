package edu.uprm.ece.icom4015.jabrisca.server;

import java.net.Socket;

public class User {
	public static final int MAX_SAVED_GAMES = 10;
	private String username;
	private String password;
	private long score=0;
	private GameResults[] pastGames = new GameResults[MAX_SAVED_GAMES]; 
	private VanillaSocketThread mainSocket;
	private VanillaSocketThread chatSocket;
	private VanillaSocketThread gameSocket;
	private boolean loggedIn=true;
	private int userNumber;
	/**
	 * @param username
	 * @param password
	 * @param score
	 */
	private User(String username, String password, long score) {
		super();
		this.username = username;
		this.password = password;
	}
	/**
	 * @return the username
	 */
	public synchronized String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public synchronized void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public synchronized String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public synchronized void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the score
	 */
	public synchronized long getScore() {
		return score;
	}
	/**
	 * @param score the score to add
	 */
	public synchronized void addScore(long score) {
		this.score += score;
	}
	
	/**
	 * @return the loggedIn
	 */
	public synchronized boolean isLoggedIn() {
		return loggedIn;
	}
	/**
	 * @param loggedIn the loggedIn to set
	 */
	public synchronized void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	/**
	 * @return the mainSocket
	 */
	public synchronized VanillaSocketThread getMainSocket() {
		return mainSocket;
	}
	/**
	 * @param mainSocket the mainSocket to set
	 */
	public synchronized void setMainSocket(VanillaSocketThread mainSocket) {
		this.mainSocket = mainSocket;
	}
	/**
	 * @return the chatSocket
	 */
	public synchronized VanillaSocketThread getChatSocket() {
		return chatSocket;
	}
	/**
	 * @param chatSocket the chatSocket to set
	 */
	public synchronized void setChatSocket(VanillaSocketThread chatSocket) {
		this.chatSocket = chatSocket;
	}
	/**
	 * @return the gameSocket
	 */
	public synchronized VanillaSocketThread getGameSocket() {
		return gameSocket;
	}
	/**
	 * @param gameSocket the gameSocket to set
	 */
	public synchronized void setGameSocket(VanillaSocketThread gameSocket) {
		this.gameSocket = gameSocket;
	}
	
	/**
	 * @return the pastGames
	 */
	public synchronized GameResults[] getPastGames() {
		return pastGames;
	}
	/**
	 * @param pastGames the pastGames to set
	 */
	public synchronized void addPastGame(GameResults pastGame) {
		GameResults[] temp = new GameResults[pastGames.length];
		for(int i=0;i<pastGames.length-1;i++){
			temp[i+1] = pastGames[i];
		}
		temp[0]=pastGame;
		pastGames = temp;
	}
	
	/**
	 * Syncronize user creation
	 * @param username
	 * @param password
	 * @param score
	 * @return
	 */
	public static synchronized User getInstance(String username, String password, long score){
		return new User(username, password, score);
	}
	
	/**
	 * only the allocate method calls this one
	 * @param number
	 */
	public synchronized void setUserNumber(int number) {
		this.userNumber = number;
	}
	/**
	 * 
	 * @param number
	 */
	public synchronized int getUserNumber() {
		return this.userNumber;
	}
	
	
}
