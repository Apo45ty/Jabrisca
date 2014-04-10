package edu.uprm.ece.icom4015.jabrisca.server.game.brica;

import edu.uprm.ece.icom4015.jabrisca.server.GameLawEnforcer;

public interface GameRoom {
	int getNumberOfPlayers();
	int addPlayer(Player player);
	int getSize();
	boolean isFull();
	Player[] getPlayers();
	String playerMadeMove(Player player,String parameters);
	String setParameters(String KeyValuePairs);
	boolean addGameLawEnforcer(GameLawEnforcer gameLawEnforcer);
	Game getGame();
	boolean setGame(Game game);
	boolean isBeenPlayed();
	String getName();
	void setName(String name);
	void addGameListener(GameListener listener);
}
