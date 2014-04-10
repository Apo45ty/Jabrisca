package edu.uprm.ece.icom4015.jabrisca.server.game.brica;

import edu.uprm.ece.icom4015.jabrisca.server.GameEvent;

public interface GameListener {
	void onGameStart(GameEvent e);
	void onGameEnd(GameEvent e);
}
