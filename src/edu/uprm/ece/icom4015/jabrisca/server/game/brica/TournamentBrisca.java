package edu.uprm.ece.icom4015.jabrisca.server.game.brica;

import java.util.Date;

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

	public Object getParameter(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNumberOfTeams() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int gameTeamScore(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getCurrentRound() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String drawCard(Player player, int seat) {
		// TODO Auto-generated method stub
		return null;
	}

	public void endRound() {
		// TODO Auto-generated method stub
		
	}

	public Date getCreateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public Player getWinner() {
		// TODO Auto-generated method stub
		return null;
	}

}
