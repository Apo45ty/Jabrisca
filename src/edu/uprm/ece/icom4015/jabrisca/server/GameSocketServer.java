package edu.uprm.ece.icom4015.jabrisca.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.uprm.ece.icom4015.jabrisca.server.game.brica.BriscaGameRoom;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.GameListener;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.GameRoom;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.Player;

public class GameSocketServer implements Runnable{
	public static final int MAX_NUMBER_OF_ROOMS = ChatSocketServer.MAX_NUMBER_OF_ROOMS;
	public static final int MAX_NUMBER_OF_USERS_PER_GAME = 4;
	private static boolean listening = false;
	private static GameSocketServer instance;
	private int port = ManagerSocketServer.gameSocketServerPort;
	private static GameRoom[] briscaGames = new GameRoom[MAX_NUMBER_OF_ROOMS];
	// Verbs
	public static final String LOGIN_USER = ManagerSocketServer.LOGIN_USER;
	public static final String LOGIN_SUCCESS = ManagerSocketServer.LOGIN_SUCCESS;
	public static final String END_MESSAGE_DELIMETER = ManagerSocketServer.END_MESSAGE_DELIMETER;
	public static final String LOGIN_FAIL = ManagerSocketServer.LOGIN_FAIL;
	public static final String PLAYER_SURRENDER = "playerHasSurrendered";
	public static final String PLAYER_TRADEDCARD = "playerHasTradedACard";
	public static final String PLAYER_CAN_TRADE_CARD = "playerCanTradeCard";
	public static final String PLAYER_CANT_TRADE_CARD = "playerCantTradeCard";
	public static final String PLAYER_PLAYED_BLACKHAND = "playerHasPlayedBlackHand";
	public static final String PLAYER_CAN_BLACKHAND = "playerCanBlackHand";
	public static final String PLAYER_CANT_BLACKHAND = "playerCantBlackHand";
	public static final String PLAYER_CAN_CONTINUE = "playerCanContinueGame";
	public static final String PLAYER_CAN_CONTINUE_TRUE = "playerCanContinue";
	public static final String PLAYER_CONTINUED = "playerCanContinued";
	public static final String PLAYER_CANT_CONTINUE_TRUE = "playerCantContinue";
	public static final String GAME_ENDED = "gameHasEnded";
	public static final String GAME_STARTED = "gameHasStarted";
	public static final String TURN_IS_OVER = "turnHasEnded";
	/**
	 * The constructor is an introvert and thus Here we add all the Game Law
	 * Enforcers
	 */
	private GameSocketServer() {
		super();
		for (int i = 0; i < briscaGames.length; i++) {
			briscaGames[i] = BriscaGameRoom
					.getInstance(MAX_NUMBER_OF_USERS_PER_GAME);
			briscaGames[i].addGameLawEnforcer((GameLawEnforcer) briscaGames[i]);
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		GameSocketServer server = GameSocketServer.getServerSingleton();
		server.start(null);
	}

	/**
	 * @return
	 */
	public synchronized static GameSocketServer getServerSingleton() {
		if (instance == null) {
			instance = new GameSocketServer();
		}
		return instance;
	}

	/**
	 * @param ports
	 */
	public synchronized void start(int... ports) {
		if (ports != null && ports.length >= 1) {
			port = ports[0];
		}
		if (!listening) {
			listening = true;
			(new Thread(this)).start();
		}
	}

	/**
	 * 
	 */
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("GameServer listening on " + port + "...");
		while (listening) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println("Socket created at " + socket.getPort());
				VanillaSocketThread myThread = new GameSocketThread(socket);
				(new Thread(myThread)).start();
			} catch (Exception e) {
				System.out.println("Gracefully dealt with error in "
						+ getClass().getTypeName() + ",Exception"
						+ e.getMessage());
			}
		}

	}

	class GameSocketThread extends VanillaSocketThread implements GameListener{
		public GameRoom room;
		private User user;

		/**
		 * @param socket
		 */
		public GameSocketThread(Socket socket) {
			super(socket);
		}

		/**
		 * 
		 */
		@Override
		public void main(String pushedMessages) {
			try {
				System.out.println(pushedMessages);
				if (pushedMessages.contains(LOGIN_USER)) {
					String parameters = pushedMessages.split("@")[1];
					String username = ((parameters.split("username=")[1])
							.split(",")[0]);
					String password = ((parameters.split("password=")[1])
							.split(",")[0]);
					boolean userExists = false;
					for (GameRoom room : briscaGames) {
						if (room == null)
							continue;
						for (Player player : room.getPlayers()) {
							if (player == null)
								continue;
							User user = player.getUser();
							if (user.getUsername().equals(username)
									&& user.getPassword().equals(password)) {
								userExists = true;
								this.user = user;
								this.room = room;
								break;
							}
						}
					}
					if (userExists) {
						out.println(LOGIN_SUCCESS + END_MESSAGE_DELIMETER);
					} else {
						out.println(LOGIN_FAIL + END_MESSAGE_DELIMETER);
					}
				}//TODO add the rest of the methods

			} catch (Exception e) {
				System.out.println("Gracefully dealt with error in "
						+ getClass().getTypeName() + ",Excetion"
						+ e.getClass().getSimpleName());
			}
		}

		public void onGameStart(GameEvent e) {
			main(e.sourceGame+GameSocketServer.GAME_ENDED);
		}

		public void onGameEnd(GameEvent e) {
			main(e.sourceGame+GameSocketServer.GAME_STARTED);
		}

	}

	/**
	 * return the room name
	 * @param rules
	 * @return null if room could not be created
	 */
	public static synchronized String createGame(String rules,User user) {
		for (GameRoom room : briscaGames) {
			//if(room==null)continue;
			if (!room.isBeenPlayed()) {
				String roomname = ((rules.split("roomname=")[1]).split(",")[0]);
				roomname = ManagerSocketServer.sanitizeWord(roomname);
				room.setName(roomname);
				room.setParameters(rules);
				room.addPlayer(Player.getInstance(0,user));
				return roomname;
			}
		}
		return null;
	}
	
	/**
	 * @return -1 if can't add user
	 */
	public static synchronized int addUser(String roomName, User user) {
		for (GameRoom room : briscaGames) {
			//if(room==null)continue;
			if (room.getName().equals(roomName)) {
				return room.addPlayer(Player.getInstance(0, user));
			}
		}
		return -1;
	}
}
