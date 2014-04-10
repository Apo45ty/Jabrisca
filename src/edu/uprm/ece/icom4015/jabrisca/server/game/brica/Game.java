package edu.uprm.ece.icom4015.jabrisca.server.game.brica;

public interface Game {
	void startNewGame();
	boolean initialParametersSet();
	boolean isTurnOver();
	boolean isOver();
	
	/**
	 * @return the seat number 
	 */
	int addPlayer();
	String play(int seatNumber, String parameters);
	String addParameters(String keyValuePairs);
	boolean hasStarted();
}
