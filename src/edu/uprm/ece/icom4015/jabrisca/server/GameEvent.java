package edu.uprm.ece.icom4015.jabrisca.server;

import edu.uprm.ece.icom4015.jabrisca.server.game.brica.Game;

public class GameEvent {
	
	public Game game;

	public GameEvent(Game game) {
		this.game = game;
	}

	public String sourceGame;
}
