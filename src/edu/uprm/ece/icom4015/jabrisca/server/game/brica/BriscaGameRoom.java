/**
 * 
 */
package edu.uprm.ece.icom4015.jabrisca.server.game.brica;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.uprm.ece.icom4015.jabrisca.server.GameSocketServer;

/**
 * @author EltonJohn
 * 
 */
public class BriscaGameRoom implements GameRoom, GameLawEnforcer {

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
	public static ExecutorService service;
	private static final long THREAD_SLEEP_TIME = 1000;
	// Verbs
	private static final String A_GAME_HAS_ENDED = "gameEnded";
	private static final String A_GAME_HAS_STARTED = "-" + "gameStarted";

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

			isWaitingForUsers = false;

			// Start game after the initial parameters are set
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
		// TODO check if player wants to continue if so check if game is tournament
	}

	/**
	 * 
	 * @param gameEvent
	 */
	private void gameOverEvent(GameEvent gameEvent) {
		gameEvent.sourceGame = this.name;
		for (GameListener listener : gameListeners) {
			if(listener == null)continue;
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
			if(listener == null)continue;
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
			if (playerCount == players.length) {
				for (GameLawEnforcer game : gameEnforcers) {
					game.start();
				}
			}
			return playerCount;
		}
		return -1;
	}

	/**
	 * 
	 */
	public int getSize() {
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
	public String playerMadeMove(Player player, String parameters) {
		return game.play(player, parameters);

	}

	/**
	 * Send the message to the rest of the players
	 * 
	 * @param message
	 * @param player
	 */
	public synchronized void broadcast(String message, Player player) {
		for (Player clients : players) {
			if (player == null)
				continue;
			if (player == clients)
				continue;
			clients.getUser().getGameSocket().sendMessage(message);
		}
	}

	/**
	 * 
	 */
	public String setParameters(String KeyValuePairs) {
		if(game == null){
			game = BriscaGameFactory.instantiante(BriscaGameFactory.VANILLA_BRISCA_GAME, null);
		}
		
		if (!game.initialParametersSet())
			isWaitingForUsers = true;

		if (KeyValuePairs.contains(BriscaGameFactory.TOURNAMENT_GAME)) {
			Object[] parameters = { game };
			game = BriscaGameFactory.instantiante(
					BriscaGameFactory.TOURNAMENT_GAME, parameters);
		}
		return game.addParameters(KeyValuePairs);
	}

	/**
	 * @return
	 */
	public boolean addGameLawEnforcer(GameLawEnforcer gameLawEnforcer) {
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
	public String getHand(Player player) {
		// TODO Auto-generated method stub
		return game.showHand(player);
	}
	/**
	 * ignored because this class is the game room also
	 * 
	 * @ignore
	 */
	public void setGameRoom(BriscaGameRoom briscaGameRoom) {
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 */
	public void start() {
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
	public Game getGame() {
		return game;
	}

	/**
	 * @return true if game is been played
	 */
	public boolean isBeenPlayed() {
		return !(game==null)&&!game.hasStarted()||isWaitingForUsers;
	}

	/**
	 * 
	 */
	public boolean setGame(Game game) {
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
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * add a listener object
	 */
	public void addGameListener(GameListener listener)
			throws IllegalArgumentException {
		if (listenerCount > gameListeners.length)
			throw new IllegalArgumentException("Too many listeners");
		this.gameListeners[listenerCount++] = listener;
	}

	/**
	 * @author EltonJohn
	 */
	static class BriscaGameFactory {

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
}
