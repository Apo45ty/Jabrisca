package edu.uprm.ece.icom4015.jabrisca.server;

import edu.uprm.ece.icom4015.jabrisca.server.game.brica.BriscaGame;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.Game;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.TournamentBrisca;

/**
 * @author EltonJohn
 */
public class BriscaGameFactory {

	public static final String VANILLA_BRISCA_GAME = "brisca";
	public static final String TOURNAMENT_GAME = "tournament";

	/**
	 * Generate the proper game
	 * 
	 * @param briscaGameFactory
	 * @param parameters
	 * @return
	 */
	public static Game instantiante(String briscaGameFactory,
			Object[] parameters) {
		if (briscaGameFactory.contains(VANILLA_BRISCA_GAME)) {
			return new BriscaGame();
		} else if (briscaGameFactory.contains(TOURNAMENT_GAME)) {
			return new TournamentBrisca((BriscaGame) parameters[0]);
		} // else
		return null;
	}

}