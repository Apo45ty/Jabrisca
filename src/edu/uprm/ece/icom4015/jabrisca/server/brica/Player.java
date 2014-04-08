/**
 * 
 */
package edu.uprm.ece.icom4015.jabrisca.server.brica;

import edu.uprm.ece.icom4015.jabrisca.server.User;

/**
 * @author EltonJohn
 *
 */
public class Player {
	private int score;
	private User user;
	
	/**
	 * @param score
	 * @param user
	 */
	private Player(int score, User user) {
		super();
		this.score = score;
		this.user = user;
	}
	/**
	 * @return the score
	 */
	public synchronized int getScore() {
		return score;
	}
	/**
	 * @param score the score to add
	 */
	public synchronized void addScore(int score) {
		this.score += score;
	}
	/**
	 * @return the user
	 */
	public synchronized User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public synchronized void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * Construct a player in a synchronious manners
	 * @param score
	 * @param user
	 * @return
	 */
	public static synchronized Player getInstance(int score, User user){
		return new Player(score, user);
	}
}
