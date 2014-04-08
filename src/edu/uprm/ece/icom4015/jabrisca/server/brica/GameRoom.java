package edu.uprm.ece.icom4015.jabrisca.server.brica;

public interface GameRoom {
	int getNumberOfPlayers();
	int addPlayer(Player player);
	int getSize();
	boolean isPlaying();
	Player[] getPlayers();
}
