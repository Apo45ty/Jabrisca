package edu.uprm.ece.icom4015.jabrisca.server.game.brica;

import java.util.Date;

import edu.uprm.ece.icom4015.jabrisca.server.GameSocketServer;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.ItalianDeckCard.ItalianDeckRank;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.ItalianDeckCard.ItalianDeckSuit;

public class BriscaGame implements Game {

	public boolean done = false;
	public static final int ITALIAN_DECK_SIZE = 40;
	private static final int MAX_NUMBER_OF_ROUNDS = 10;
	public ItalianDeckCard[] deck = new ItalianDeckCard[ITALIAN_DECK_SIZE];
	private Date createTime;
	private boolean blackhand = false;
	private boolean surrender = false;
	private boolean teams = false;
	private boolean cardswap = false;
	private boolean initialParametersSet = false;
	private Player[] players = new Player[4];
	private int currentPlayers = 0;
	private boolean hasStarted = false;
	private ItalianDeckCard[][] playerStacks = new ItalianDeckCard[4][40];
	private ItalianDeckCard[][] playerHands = new ItalianDeckCard[4][3];
	private ItalianDeckCard[] currentStack = new ItalianDeckCard[4];
	private int cardsPlayedInRound = 0;
	private ItalianDeckCard life = null;
	private int currentCard = 40;
	private int currentTurn = 0;
	private int[] turns = new int[4];
	private boolean isWaitingForPlayers = true;
	private boolean isRoundOver = false;
	private boolean isGameOver = false;
	private int numberOfRounds = 0;

	public void startNewGame() {
		// Create Deck
		createTime = new Date();
		int count = 0;
		for (ItalianDeckSuit suit : ItalianDeckCard.ItalianDeckSuit.values()) {
			for (ItalianDeckRank rank : ItalianDeckCard.ItalianDeckRank
					.values()) {
				deck[count++] = new ItalianDeckCard(suit, rank);
			}
		}

		// Shuffle
		for (int i = 0; i < deck.length; i++) {
			ItalianDeckCard card = deck[i];
			int random = (int) (Math.random() * 40);
			deck[i] = deck[random];
			deck[random] = card;
		}

		this.life = drawCard();
		hasStarted = true;
		for (int i = 0; i < playerHands.length; i++) {
			ItalianDeckCard[] temp = new ItalianDeckCard[3];
			for (int j = 0; j < temp.length; j++) {
				temp[j] = drawCard();
			}
			playerHands[i] = temp;
		}
	}

	public String showHand(Player player) {
		String result = GameSocketServer.SHOW_PLAYERS_HAND + "@cards=";
		for (ItalianDeckCard card : playerHands[player.getSeatNumber() - 1]) {
			result += card + "/";
		}
		result = result.substring(0, result.length() - 1);
		return result;
	}

	/**
	 * Draws a card from deck
	 * 
	 * @return life at the end of the deck, null if deck is empty
	 */
	private ItalianDeckCard drawCard() {
		if (currentCard < -1)
			return null;
		// because of lazy evaluation the second isonly excuted after the first
		if (currentCard == -1 && currentCard == currentCard--)
			return life;
		return deck[--currentCard];
	}

	public boolean initialParametersSet() {
		return initialParametersSet;
	}

	public boolean isRoundOver() {
		// TODO Do the math
		return isRoundOver;
	}

	public boolean isOver() {
		return isGameOver;
	}

	/**
	 * Adds a player if possible if not then it returns -1
	 */
	public int addPlayer(Player player) {
		if (currentPlayers < players.length) {
			this.players[currentPlayers++] = player;
			player.setSeatNumber(currentPlayers);
			turns[(currentPlayers - 1)] = currentPlayers;
			if (currentPlayers == players.length) {
				isWaitingForPlayers = false;
			}
			return currentPlayers;
		} // Else
		return -1;
	}

	public String play(Player player, String parameters) {
		int seatNumber = player.getSeatNumber();
		if (!isWaitingForPlayers && this.turns[currentTurn] != seatNumber) {
			return GameSocketServer.MOVE_FAILED
					+ GameSocketServer.MOVE_OUT_OF_TURN;
		}

		String result = GameSocketServer.MOVE_NOT_FOUND;
		String move = ((parameters.split("move=")[1]).split(",")[0]);

		if (move.contains("blackhand")) {
			// TODO check players hand for blackhand
			if (blackhand) {
				result = playerWon(seatNumber);
			} else {
				result = GameSocketServer.PLAYER_CANT_BLACKHAND;
			}
		}
		if (move.contains("cardswap"))
			if (cardswap) {
				result = playerSwapCard(seatNumber, move);
			} else {
				result = GameSocketServer.PLAYER_CANT_TRADE_CARD;
			}

		if (move.contains("surrender"))
			if (surrender) {
				result = playerSurrendered(seatNumber);
			} else {
				result = GameSocketServer.PLAYER_CANT_SURRENDER;
			}

		if (move.contains(GameSocketServer.MOVE_DRAW_CARD)) {
			boolean needsCard = false;
			int i = 0;
			for (i = 0; i < playerHands[seatNumber - 1].length; i++) {
				if (playerHands[seatNumber - 1][i] == null) {// makeMove-drawingCard@move=makeMove-drawingCard
					needsCard = true;
					break;
				}
			}
			if (needsCard) {
				ItalianDeckCard card = drawCard();
				if (card != null) {
					result = GameSocketServer.MOVE_NEW_CARD + card;
					playerHands[seatNumber - 1][i] = card;
				} else
					result = GameSocketServer.DECK_OUT_OF_CARDS;
			} else {
				// TODO tell player he cant draw
				result = GameSocketServer.CANT_DRAW_CARDS;
			}
		}

		if (move.contains(GameSocketServer.MOVE_CARD))
			cardMove: {
				if (cardsPlayedInRound <= currentPlayers) {
					String card = move.split(GameSocketServer.MOVE_CARD)[1];
					ItalianDeckCard italCard = new ItalianDeckCard(card);
					boolean cardInPlayersHand = false;
					int count = 0;
					for (ItalianDeckCard tempCard : playerHands[seatNumber - 1]) {
						if (tempCard == null) {
							result = GameSocketServer.PLAYER_HAS_NOT_DRAWN_CARD;
							break cardMove;
						}
						if (tempCard.equals(italCard)) {
							cardInPlayersHand = true;
							playerHands[seatNumber - 1][count] = null;
						}
						count++;
					}
					if (cardInPlayersHand) {
						currentStack[cardsPlayedInRound++] = italCard;
						result = GameSocketServer.MOVE_SUCCESSFUL;
						if ((currentTurn + 1) == currentPlayers)
							isRoundOver = true;
						currentTurn = ((currentTurn + 1) % 4);
					} else {
						result = GameSocketServer.PLAYER_DOES_NOT_POSSES_CARD;
					}
				}
				if (isRoundOver) {
					int winnersInd = 0;
					numberOfRounds++;
					cardsPlayedInRound = 0;
					ItalianDeckCard winnersCard = currentStack[winnersInd];
					int score = 0;
					for (int i = 0; i < currentStack.length; i++) {
						int comp = BriscaCardComparator.compare(winnersCard,
								currentStack[i], this.life.getSuit());
						if (comp > 0) {
							winnersCard = currentStack[i];
							winnersInd = i;
						} else if (comp == 0) {
							// TODO get players turns
						}
						score += currentStack[i].getRank().getValue();
					}
					int count = 0;
					for (ItalianDeckCard card : playerStacks[winnersInd]) {
						if (card == null)
							break;
						count++;
					}

					for (ItalianDeckCard card : currentStack)
						playerStacks[winnersInd][count++] = card;
					players[winnersInd].addScore(score);
					// TODO END ROUND

					// Set the player turns for next round to the right of the
					// winner
					for (int i = 0; i < turns.length; i++) {
						int value = (i + winnersInd) % 4 + 1;
						turns[i] = value;
					}
					isRoundOver = false;
					if(numberOfRounds==MAX_NUMBER_OF_ROUNDS){
						isGameOver = true;
					}
				}
			}
		//
		return result;
	}

	private String playerSurrendered(int seatNumber) {
		// TODO
		return null;
	}

	private String playerSwapCard(int seatNumber, String move) {
		// TODO Make cardswap implementation
		return null;
	}

	/**
	 * Tell users the game isover and which player has won
	 * 
	 * @param seatNumber
	 * @return
	 */
	private String playerWon(int seatNumber) {
		// TODO
		return null;
	}

	/**
	 * 
	 */
	public String addParameters(String keyValuePairs) {
		blackhand = Boolean.parseBoolean(((keyValuePairs
				.split(GameSocketServer.BLACKHAND_KEY)[1]).split(",")[0]));
		surrender = Boolean.parseBoolean(((keyValuePairs
				.split(GameSocketServer.SURRENDER_KEY)[1]).split(",")[0]));
		teams = Boolean.parseBoolean(((keyValuePairs
				.split(GameSocketServer.TEAMS)[1]).split(",")[0]));
		cardswap = Boolean.parseBoolean(((keyValuePairs
				.split(GameSocketServer.CARD_SWAP)[1]).split(",")[0]));
		if (!initialParametersSet) {
			initialParametersSet = true;
		}
		return GameSocketServer.PARAMETERS_SET;
	}

	/**
	 * 
	 */
	public boolean hasStarted() {
		return hasStarted;
	}

	/**
	 * @return the isWaitingForPlayers
	 */
	public boolean isWaitingForPlayers() {
		return isWaitingForPlayers;
	}

	/**
	 * 
	 */
	public String getParameters() {
		return GameSocketServer.LIFECARD_KEY + life + ","
				+ GameSocketServer.BLACKHAND_KEY + blackhand + ","
				+ GameSocketServer.CARD_SWAP + cardswap + ","
				+ GameSocketServer.TEAMS + teams + ","
				+ GameSocketServer.SURRENDER_KEY + surrender;
	}

	/**
	 * 
	 */
	public int getCurrentPlayersSeat() {
		return turns[currentTurn];
	}

	/**
	 * 
	 */
	public Object getParameter(String key) {
		if (key.contains(GameSocketServer.BLACKHAND_KEY))
			return blackhand;
		if (key.contains(GameSocketServer.SURRENDER_KEY))
			return surrender;
		if (key.contains(GameSocketServer.TEAMS))
			return teams;
		if (key.contains(GameSocketServer.CARD_SWAP))
			return cardswap;
		return null;
	}

	/**
	 * 
	 */
	public int getNumberOfTeams() {
		if (teams)
			return 2;
		return 0;
	}

	/**
	 * All teams are hard coded to be all even number to be together
	 * 
	 * @param the
	 *            team number
	 * @return score of team the first team is always the evens
	 */
	public int gameTeamScore(int i) {
		if (i >= 2 || i < 0)
			throw new IllegalArgumentException();
		int result = 0;
		if (i == 0) {
			result += players[0].getScore();
			result += players[2].getScore();
		} else if (i == 1) {
			result += players[1].getScore();
			result += players[3].getScore();
		}
		return result;
	}

	public int getCurrentRound() {
		return numberOfRounds;
	}

}
