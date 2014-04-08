/**
 * 
 */
package edu.uprm.ece.icom4015.jabrisca.server;

import java.util.Date;

import edu.uprm.ece.icom4015.jabrisca.server.brica.Player;

/**
 * @author EltonJohn
 * 
 */
public class GameResults {
	private Player[] players = new Player[4];
	private Player winner;
	private Date gameStartTime;
	private Date gameEndTime;
	private String parameters; // was timed, had blackhand, was played in teams
								// ...

	/**
	 * @param gameStartTime
	 * @param gameEndTime
	 * @param parameters
	 */
	private GameResults(Date gameStartTime, Date gameEndTime, String parameters) {
		super();
		this.gameStartTime = gameStartTime;
		this.gameEndTime = gameEndTime;
		this.parameters = parameters;
	}
	
	/**
	 * Try to set the player to the given position
	 * 
	 * @param player
	 * @param index
	 * @return true if player could be seated in game.
	 */
	public synchronized boolean seatPlayer(Player player, int index)
			throws IndexOutOfBoundsException {
		if (index > players.length)
			throw new IndexOutOfBoundsException(
					"The max number of players is four.");
		if (this.players[index] == null)
			this.players[index] = player;
		else
			return false;
		return true;
	}

	/**
	 * Get one of the players of the game
	 * 
	 * @param index
	 *            of the player to fetch
	 * @return the player
	 */
	public synchronized Player getPlayer(int index)
			throws IndexOutOfBoundsException {
		if (index > players.length)
			throw new IndexOutOfBoundsException(
					"The max number of players is four.");
		return players[index];
	}

	/**
	 * @return the winner
	 */
	public synchronized Player getWinner() {
		return winner;
	}

	/**
	 * @param index
	 *            of the winner to set
	 */
	public synchronized void setWinner(int winnerIndex)
			throws IndexOutOfBoundsException {
		if (winnerIndex > players.length)
			throw new IndexOutOfBoundsException(
					"The max number of players is four.");
		this.winner = players[winnerIndex];
	}

	/**
	 * @return the gameStartTime
	 */
	public synchronized Date getGameStartTime() {
		return gameStartTime;
	}

	/**
	 * @param gameStartTime
	 *            the gameStartTime to set
	 */
	public synchronized void setGameStartTime(Date gameStartTime) {
		this.gameStartTime = gameStartTime;
	}

	/**
	 * @return the gameEndTime
	 */
	public synchronized Date getGameEndTime() {
		return gameEndTime;
	}

	/**
	 * @param gameEndTime
	 *            the gameEndTime to set
	 */
	public synchronized void setGameEndTime(Date gameEndTime) {
		this.gameEndTime = gameEndTime;
	}

	/**
	 * @return the parameters
	 */
	public synchronized String getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public synchronized void setParameters(String parameters) {
		this.parameters = parameters;
	}
	/**
	 * Get the duration of the game
	 * @return
	 */
	public synchronized long getGameDuration() {
		return gameEndTime.getTime() - gameStartTime.getTime();
	}
	
	/**
	 * Constructs an instance of this class in a safe way
	 * @param gameStartTime
	 * @param gameEndTime
	 * @param parameters
	 * @return
	 */
	public static synchronized GameResults getInstance(Date gameStartTime, Date gameEndTime, String parameters){
		return new GameResults(gameStartTime, gameEndTime, parameters);
	}
}
