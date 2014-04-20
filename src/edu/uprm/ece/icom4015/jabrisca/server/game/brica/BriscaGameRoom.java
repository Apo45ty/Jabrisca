/**
 * 
 */
package edu.uprm.ece.icom4015.jabrisca.server.game.brica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.uprm.ece.icom4015.jabrisca.server.BriscaGameFactory;
import edu.uprm.ece.icom4015.jabrisca.server.GameSocketServer;
import edu.uprm.ece.icom4015.jabrisca.server.ManagerSocketServer;

/**
 * @author EltonJohn
 * 
 */
public class BriscaGameRoom implements GameRoom, GameLawEnforcer {
	private int id=0;
	private static final int MAX_NUMBER_OF_THREADS = 3;
	private Player[] players = new Player[GameSocketServer.MAX_NUMBER_OF_USERS_PER_GAME];
	private final int MAX_NUMBER_OF_ENFORCERS = 1;
	private final int MAX_NUMBER_OF_GAMELISTENERS = 10;
	private int gameEnforcerCount = 0;
	private GameLawEnforcer[] gameEnforcers = new GameLawEnforcer[MAX_NUMBER_OF_ENFORCERS];
	private Game game;
	private int playerCount = 0;
	// private Thread enforcerThread;//TODO make thread array for each enforcer
	private boolean done = false;
	private boolean isWaitingForUsers = false;
	private String name;
	private int listenerCount;
	private GameListener[] gameListeners = new GameListener[MAX_NUMBER_OF_GAMELISTENERS];
	private boolean isTimed;
	private boolean playersHaveNotConnected;
	public static ExecutorService service;
	private static final long THREAD_SLEEP_TIME = 1000;
	// Verbs
	public static final String A_GAME_HAS_ENDED = "gameEnded";
	public static final String A_GAME_HAS_STARTED = "-" + "gameStarted";
	public static final String PLAYER_HAS_JOINED = "playerJoined";
	public static final String NUMBER_OF_PLAYERS_KEY = "numeroplayers=";

	private BriscaGameRoom(int roomSize) {
		this.players = new Player[roomSize];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uprm.ece.icom4015.jabrisca.server.brica.GameRoom#getInstance(int)
	 */
	public synchronized static GameRoom getInstance(int roomSize) {
		return new BriscaGameRoom(roomSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uprm.ece.icom4015.jabrisca.server.brica.GameRoom#getNumberOfPlayers()
	 */
	public synchronized int getNumberOfPlayers() {
		return playerCount;
	}

	public void run() {
		while (!done) {
			if (game == null) {
				game = BriscaGameFactory.instantiante(
						BriscaGameFactory.VANILLA_BRISCA_GAME, null);
			}

			while (!game.initialParametersSet()) {
				try {
					Thread.currentThread().sleep(THREAD_SLEEP_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			;

			isWaitingForUsers = true;
			while (playerCount < players.length) {
				try {
					Thread.currentThread().sleep(THREAD_SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// Start game after the initial parameters are set
			playersHaveNotConnected = true;
			while (playersHaveNotConnected) {
				for (Player player : players) {
					if (player.getUser().getGameSocket() == null) {
						playersHaveNotConnected = true;
					} else {
						playersHaveNotConnected = false;
					}
				}// if reach end then all are connected
			}
			isWaitingForUsers = false;
			this.game.startNewGame();

			// Tell everyone the game started
			gameStartEvent(new GameEvent(game));

			// Wait for game to end
			while (!game.isOver()) {
				if (game.isRoundOver()) {
					for (Player player : players) {
						// TODO send the users the results of the turn
						player.getUser().getGameSocket()
								.sendMessage(GameSocketServer.TURN_IS_OVER);
					}
				}
				try {
					Thread.currentThread().sleep(THREAD_SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// TODO tell every game listener game has ended
			if (game instanceof TournamentBrisca) {
				// if the game is a tournament brisca game tell it so that it
				// does all the cleaning up
				((TournamentBrisca) game).gameEnded();
				gameOverEvent(new GameEvent(game));
				if (((TournamentBrisca) game).isTournamentOver()) {
					game = null;
				}
			} else {
				gameOverEvent(new GameEvent(game));
				cleanUp();
				game = null;
			}
		}
	}

	private void cleanUp() {
		// TODO get player winner and create a game results object
		// TODO check if player wants to continue if so check if game is
		// tournament
	}

	/**
	 * 
	 * @param gameEvent
	 */
	private void gameOverEvent(GameEvent gameEvent) {
		gameEvent.sourceGame = this.name;
		for (GameListener listener : gameListeners) {
			if (listener == null)
				continue;
			listener.onGameEnd(gameEvent);
		}
	}

	/**
	 * 
	 * @param gameEvent
	 */
	private void gameStartEvent(GameEvent gameEvent) {
		gameEvent.sourceGame = this.name;
		for (GameListener listener : gameListeners) {
			if (listener == null)
				continue;
			listener.onGameStart(gameEvent);
		}
	}

	/**
	 * @return if player can't be added returns -1, otherwise it return the
	 *         player number
	 */
	public synchronized int addPlayer(Player player) {
		if (game != null && playerCount < players.length) {
			player.setSeatNumber(game.addPlayer(player));
			players[playerCount++] = player;
			addGameListener((GameListener) player.getUser().getGameSocket());
			if (playerCount == 1) {
				for (GameLawEnforcer game : gameEnforcers) {
					game.start();
				}
			}
			broadcast(PLAYER_HAS_JOINED + "@username="
					+ player.getUser().getUsername() + ","
					+ NUMBER_OF_PLAYERS_KEY + playerCount, player);
			return playerCount;
		}
		return -1;
	}

	/**
	 * 
	 */
	public synchronized int getSize() {
		return players.length;
	}

	/**
	 * 
	 */
	public synchronized boolean isFull() {
		return players.length == playerCount;
	}

	/**
	 * 
	 */
	public synchronized Player[] getPlayers() {
		return players;
	}

	/**
	 * fetch the player number
	 */
	public synchronized String playerMadeMove(Player player, String parameters) {
		String message = game.play(player, parameters);
		broadcast(message + "@seat=" + player.getSeatNumber() + ",name="
				+ player.getUser().getUsername()
				+ ManagerSocketServer.END_MESSAGE_DELIMETER, player);
		return message;
	}

	/**
	 * Send the message to the rest of the players
	 * 
	 * @param message
	 * @param player
	 */
	public synchronized void broadcast(String message, Player player) {
		for (Player client : players) {
			if (client == null)
				continue;
			if (player == client)
				continue;
			client.getUser().getGameSocket().sendMessage(message);
		}
	}

	/**
	 * 
	 */
	public synchronized String setParameters(String KeyValuePairs) {
		if (game == null) {
			game = BriscaGameFactory.instantiante(
					BriscaGameFactory.VANILLA_BRISCA_GAME, null);
		}

		if (!game.initialParametersSet())
			isWaitingForUsers = true;

		if (KeyValuePairs.contains(BriscaGameFactory.TOURNAMENT_GAME + "=true")) {
			Object[] parameters = { game };
			game = BriscaGameFactory.instantiante(
					BriscaGameFactory.TOURNAMENT_GAME, parameters);
		}
		if (KeyValuePairs.contains(GameSocketServer.TIMED_KEY)) {
			// TODO make time out on turns
			isTimed = Boolean.parseBoolean(((KeyValuePairs
					.split(GameSocketServer.TIMED_KEY)[1]).split(",")[0]));
		}
		return game.addParameters(KeyValuePairs);
	}

	/**
	 * @return
	 */
	public synchronized boolean addGameLawEnforcer(
			GameLawEnforcer gameLawEnforcer) {
		if (gameEnforcerCount < gameEnforcers.length) {
			gameEnforcers[gameEnforcerCount++] = gameLawEnforcer;
			gameLawEnforcer.setGameRoom(this);
			// gameLawEnforcer.start();
			return true;
		} else
			return false;
	}

	/**
	 * 
	 */
	public synchronized String getHand(Player player) {
		// TODO Auto-generated method stub
		return game.showHand(player);
	}

	/**
	 * ignored because this class is the game room also
	 * 
	 * @ignore
	 */
	public synchronized void setGameRoom(BriscaGameRoom briscaGameRoom) {
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 */
	public synchronized void start() {
		ExecutorService service = getExecutor();
		service.execute(this);
	}

	private synchronized ExecutorService getExecutor() {
		if (service == null) {
			service = Executors.newFixedThreadPool(MAX_NUMBER_OF_ENFORCERS);
		}
		return service;
	}

	/**
	 * 
	 */
	public synchronized Game getGame() {
		return game;
	}

	/**
	 * @return true if game is been played
	 */
	public synchronized boolean isBeenPlayed() {
		boolean result = !(game == null) && !game.hasStarted()
				|| isWaitingForUsers;
		if (playerCount == 0) {
			result = false;
			game = null;
		}
		return result;
	}

	/**
	 * 
	 */
	public synchronized boolean setGame(Game game) {
		this.game = game;
		return true;
	}

	/**
	 * 
	 */
	public synchronized String getName() {
		return name;
	}

	/**
	 * @param name
	 *            of room
	 */
	public synchronized void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 */
	public synchronized String getGameParameters() {
		return game.getParameters() + "," + GameSocketServer.TIMED_KEY
				+ isTimed;
	}

	/**
	 * add a listener object
	 */
	public synchronized void addGameListener(GameListener listener)
			throws IllegalArgumentException {
		if (listenerCount > gameListeners.length)
			throw new IllegalArgumentException("Too many listeners");
		this.gameListeners[listenerCount++] = listener;
	}

	/**
	 * 
	 */
	public synchronized String getGameScore() {
		if (game == null)
			throw new IllegalStateException();
		String result = "score=\"" + "Round " + game.getCurrentRound()
				+ ":\nPlayer : Score";
		boolean teams = (Boolean) (game.getParameter(GameSocketServer.TEAMS));

		for (int i = 0; i < playerCount; i++) {
			result += "\n" + players[i].getUser().getUsername() + ":"
					+ players[i].getScore();
		}
		if (teams) {
			result += "\nTeam Scores:";
			for (int i = 0; i < game.getNumberOfTeams(); i++) {
				result += "\nteam " + i + " has a score of"
						+ game.gameTeamScore(i);
			}
		}
		return result + "\"";
	}

	/**
	 * 
	 */
	public synchronized int getCurrentPlayersSeat() {
		return game.getCurrentPlayersSeat();
	}

	/**
	 * 
	 */
	public String getCurrentPlayersName() {
		return players[game.getCurrentPlayersSeat() - 1].getUser()
				.getUsername();
	}

	@Override
	public String toString() {
		// TODO {key1:value;key2:value...}
		if (game == null)
			return null;
		String result = "{roomName=" + this.name + ","
				+ GameSocketServer.TIMED_KEY + isTimed + ",";
		String tResult = game.getParameters();
		if (tResult != null)
			result = (result + tResult);
		return result.replace("=", ":").replace(",", ";") + "} ";
	}

	/**
	 * @return the id
	 */
	public synchronized int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public synchronized void setId(int id) {
		this.id = id;
	}

}
