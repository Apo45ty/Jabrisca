package edu.uprm.ece.icom4015.jabrisca.server.game.brica;

public interface Game {
	void startNewGame();
	boolean initialParametersSet();
	boolean isRoundOver();
	boolean isOver();
	
	/**
	 * @return the seat number 
	 */
	int addPlayer(Player player);
	String play(Player player, String parameters);
	String addParameters(String keyValuePairs);
	boolean hasStarted();
	String showHand(Player player);
}
