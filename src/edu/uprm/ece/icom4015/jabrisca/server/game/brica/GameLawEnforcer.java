package edu.uprm.ece.icom4015.jabrisca.server.game.brica;


public interface GameLawEnforcer extends Runnable {
	/*
	 * set the game room in which to exept the rule of war
	 */
	void setGameRoom(BriscaGameRoom briscaGameRoom);
	/*
	 * Expected to throw n exception if cant create thread
	 */
	void start();

}
