package edu.uprm.ece.icom4015.jabrisca.server.game.brica;

public class TournamentBrisca implements Game {
	BriscaGame game;

	public TournamentBrisca(BriscaGame game) {
		this.game = game;
	}
	
	public void startNewGame() {
		// TODO Auto-generated method stub

	}

	public boolean initialParametersSet() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOver() {
		// TODO Auto-generated method stub
		return false;
	}

	public void gameEnded() {
		// TODO Auto-generated method stub
		
	}

	public int addPlayer(Player player) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String play(Player player, String parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	public String addParameters(String keyValuePairs) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isTournamentOver() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRoundOver() {
		// TODO Auto-generated method stub
		return false;
	}

	public String showHand(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCurrentPlayersSeat() {
		// TODO Auto-generated method stub
		return 0;
	}

}
