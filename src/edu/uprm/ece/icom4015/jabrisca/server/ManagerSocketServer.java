package edu.uprm.ece.icom4015.jabrisca.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.Timer;

import edu.uprm.ece.icom4015.jabrisca.server.ChatSocketServer.ChatSocketThread;
import edu.uprm.ece.icom4015.jabrisca.server.GameSocketServer.GameSocketThread;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.BriscaGameRoom;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.GameEvent;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.GameListener;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.GameRoom;

public class ManagerSocketServer extends VanillaSocketServer {
	private static ManagerSocketServer instance;
	private static boolean listening = false;
	public static final int MAX_CLIENTS = 60;
	public static int gameSocketServerPort = 5050;
	public static int chatSocketServerPort = 5757;
	public static int managerSocketServerPort = 6767;
	public static GameSocketServer gameServer;
	public static ChatSocketServer chatServer;
	private String[] badWords = { "cabron", "pendejo", "popo", "puta", "cago",
			"jodienda", "joder" };
	private static final boolean debug =true;
	public static BlockingDeque bannedWords = new LinkedBlockingDeque();
	public static int currentUsers = 0;
	// Verbs
	public static final String LOGIN_USER = "loginUser";
	public static final String LOGIN_SUCCESS = "loginSuccess";
	public static final String SIGNUP_USER = "signUpUser";
	public static final String SIGNUP_SUCCESS = "signUpSuccess";
	public static final String END_MESSAGE_DELIMETER = "-END-OF-MESSAGE-";
	public static final String GET_CHAT_PORT = "getChatSocketPort";
	public static final String GET_GAME_PORT = "getGameSocketPort";
	public static final String CREATE_GAME = "createGame";
	public static final String JOIN_GAME = "joinGame";
	public static final String SIGNUP_FAILED = "signUpFailed";
	public static final String LOGIN_FAIL = "loginFailed";
	public static final String LOGOUT_SUCCESS = "logoutSuccess";
	public static final String LOGOUT_USER = "logoutUser";
	public static final String SIGNUP_FAILED_USERNAME_TAKEN = SIGNUP_FAILED
			+ "-" + "UsenameTaken";
	public static final String SHOW_USERS = "listUsers";
	public static final String END_CONNECTION = "endConnection";
	public static final String END_LINE_DELIMETER = "$";

	/**
	 * The constructor is an introvert and thus
	 */
	private ManagerSocketServer() {
		super();
		for (String word : badWords) {
			bannedWords.add(word);
		}
		badWords = null; // Dispose of string to save memory
	}

	/**
	 * Create the specific amount of users for this Server
	 */
	@Override
	void initializeUsers() {
		users = new User[MAX_CLIENTS];
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ManagerSocketServer server = ManagerSocketServer.getServerSingleton();
		server.start(null);
	}

	/**
	 * 
	 * @return
	 */
	public synchronized static ManagerSocketServer getServerSingleton() {
		if (instance == null) {
			instance = new ManagerSocketServer();
		}
		return instance;
	}

	/**
	 * 
	 * @param ports
	 */
	public synchronized void start(int... ports) {
		if (ports != null && ports.length > 3) {
			// TODO set ports
			gameSocketServerPort = ports[0];
			chatSocketServerPort = ports[1];
			managerSocketServerPort = ports[2];
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
		Thread.currentThread().setName("ManagerSocketServer");
		chatServer = ChatSocketServer.getServerSingleton();
		chatServer.start(chatSocketServerPort);
		gameServer = GameSocketServer.getServerSingleton();
		gameServer.start(gameSocketServerPort);
		if (debug) {
			User amir = User.getInstance("Amir", "securepassword", 0);
			String keyvalues = GameSocketServer.ROOMNAMEKEY + "Game01,"
					+ GameSocketServer.BLACKHAND_KEY + "true,"
					+ GameSocketServer.SURRENDER_KEY + "true,"
					+ GameSocketServer.TEAMS + "true,"
					+ GameSocketServer.CARD_SWAP + "true,"
					+ GameSocketServer.TIMED_KEY + "false";
			gameServer.createGame(keyvalues, amir);
			gameServer.addUser("Game01", User.getInstance("Maria", "lol", 0));
			gameServer.addUser("Game01", User.getInstance("Juan", "lol", 0));
		}
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(managerSocketServerPort);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("ManagerServer listening on "
				+ managerSocketServerPort + "...");
		while (listening) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println("Socket created at " + socket.getPort());
				VanillaSocketThread myThread = new MainSocketThread(socket);
				(new Thread(myThread)).start();
			} catch (Exception e) {
				System.out.println("Gracefully dealt with error in "
						+ getClass().getTypeName() + ",Exception"
						+ e.getMessage());
			}
		}
	}

	class MainSocketThread extends VanillaSocketThread implements GameListener {

		private User user;
		private boolean clearingBuffer = false;
		private Timer timeout2;

		public MainSocketThread(Socket socket) {
			super(socket);
		}

		@Override
		public void main(String pushedMessages) {
			System.out.println(pushedMessages);
			if (pushedMessages.contains(SIGNUP_USER)) {
				// TODO check if username is registed already
				String parameters = pushedMessages.split("@")[1];
				String username = ((parameters.split("username=")[1])
						.split(",")[0]);
				username = sanitizeWord(username);
				String password = ((parameters.split("password=")[1])
						.split(",")[0]);
				Thread.currentThread()
						.setName("ManagerSocketClient" + username);
				if (!userExists(username, password)) {
					this.user = User.getInstance(username, password, 0);
					if (allowcateUser(user, users)) {
						out.println(SIGNUP_SUCCESS + END_MESSAGE_DELIMETER);
						chatServer.addUser(user);
						user.setUserNumber(currentUsers);
						user.setLoggedIn(true);
						user.setMainSocket(this);
						currentUsers++;
						System.out.println("Registered User: " + username);
					} else {
						out.println(SIGNUP_FAILED + END_MESSAGE_DELIMETER);
					}
				} else {
					out.println(SIGNUP_FAILED_USERNAME_TAKEN
							+ END_MESSAGE_DELIMETER);
				}
			} else if (pushedMessages.contains(LOGIN_USER)) {
				// TODO check if user name is valid and create new user
				String parameters = pushedMessages.split("@")[1];
				String username = ((parameters.split("username=")[1])
						.split(",")[0]);
				String password = ((parameters.split("password=")[1])
						.split(",")[0]);
				Thread.currentThread()
						.setName("ManagerSocketClient" + username);
				boolean userExists = false;
				for (User user : users) {
					if (user == null)
						continue;
					if (user.getUsername().equals(username)
							&& user.getPassword().equals(password)) {
						userExists = true;
						this.user = user;
						break;
					}
				}
				if (userExists) {
					out.println(LOGIN_SUCCESS + END_MESSAGE_DELIMETER);
					user.setLoggedIn(userExists);
					user.setMainSocket(this);
				} else {
					out.println(LOGIN_FAIL + END_MESSAGE_DELIMETER);
				}
			} else if (pushedMessages.contains(LOGOUT_USER)) {
				// TODO check if user name is valid and create00 new user
				user.setLoggedIn(false);
				out.println(ManagerSocketServer.LOGOUT_SUCCESS
						+ ManagerSocketServer.END_MESSAGE_DELIMETER);
			} else if (pushedMessages.contains(GET_CHAT_PORT)) {
				out.println(chatSocketServerPort + END_MESSAGE_DELIMETER);
			} else if (pushedMessages.contains(END_CONNECTION)) {
				done = true;
			} else if (pushedMessages.contains(SHOW_USERS) && user != null) {
				// TODO check if user is loggin
				String result = "";
				for (User user : users) {
					if (user == null)
						continue;
					result += user + ",";
				}
				result = result.substring(0, result.length() - 1);
				out.println(result + END_MESSAGE_DELIMETER);
			} else if (pushedMessages.contains(GET_GAME_PORT)) {
				out.println(gameSocketServerPort + END_MESSAGE_DELIMETER);
			} else if (pushedMessages.contains(LOGOUT_USER)) {
				user.setLoggedIn(false);
				out.println(LOGOUT_SUCCESS + END_MESSAGE_DELIMETER);
			} else if (pushedMessages.contains(CREATE_GAME) && !clearingBuffer) {
				// TODO do something special: verify game can be created,
				// switch the user to the proper chat room, create game ...
				String parameters = pushedMessages.split("@")[1];
				out.println(GameSocketServer.GAME_CREATED + "@"
						+ GameSocketServer.ROOMNAMEKEY
						+ GameSocketServer.createGame(parameters, user)
						+ END_MESSAGE_DELIMETER);
				clearingBuffer = true;
				timeout2 = new Timer(1500, new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						clearingBuffer = false;
						timeout2.stop();
					}
				});
				timeout2.start();
			} else if (pushedMessages.contains(JOIN_GAME)) {
				// TODO do something special: verify game can be Joined,
				// switch the user to the proper chat room, create game ...
				String parameters = pushedMessages.split("@")[1];
				String roomName = ((parameters.split("roomName=")[1])
						.split(",")[0]);
				int result = GameSocketServer.addUser(roomName, user);
				if (result >= 0) {
					out.println(GameSocketServer.PLAYER_JOINED_ROOM + "@"
							+ BriscaGameRoom.NUMBER_OF_PLAYERS_KEY + result);
				} else {
					out.println(GameSocketServer.PLAYER_CANT_JOINED_ROOM);
				}
				clearingBuffer = true;
				timeout2 = new Timer(1500, new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						clearingBuffer = false;
						timeout2.stop();
					}
				});
				timeout2.start();
			} else if (pushedMessages.contains(GameSocketServer.GET_ALL_GAMES)) {
				String result = "";
				int num = Integer.parseInt(((pushedMessages.split("load=")[1])
						.split(",")[0]));
				GameRoom[] rooms = gameServer.getRooms();
				for (int i = 0; i < num; i++) {
					if (rooms[i] == null || rooms[i].toString() == null)
						continue;
					result += i + "game=" + rooms[i].toString()
							+ END_LINE_DELIMETER;
				}

				out.println(GameSocketServer.GET_ALL_GAMES_SUCCESS + "@"
						+ result + END_MESSAGE_DELIMETER);
			} else if (pushedMessages
					.contains(GameSocketServer.GET_PLAYERS_ONLINE)) {
				// TODO Get all players online
				String result = "";
				for (User user : users) {
					if (user == null)
						continue;
					// else if
					if (user.isLoggedIn())
						result += user.getUsername() + "  is logged in."
								+ END_LINE_DELIMETER;
				}
				result = result.substring(0, result.length() - 1);
				out.println(GameSocketServer.GET_PLAYERS_ONLINE_SUCCESS + "@"
						+ result + END_MESSAGE_DELIMETER);
			} else if (pushedMessages
					.contains(GameSocketServer.GET_TOP_PLAYERS)) {
				// TODO Get all the top players
				String result = "";
				for (User user : users) {
					if (user == null)
						continue;
					// else if
					// Sort
					result += user.getUsername() + "  has a score of "
							+ user.getScore() + "." + END_LINE_DELIMETER;
				}
				result = result.substring(0, result.length() - 1);
				out.println(GameSocketServer.GET_TOP_PLAYERS_SUCCESS + "@"
						+ result + END_MESSAGE_DELIMETER);
			} // TODO rest of methods
		}

		private boolean userExists(String username, String password) {
			boolean userExists = false;
			for (User user : users) {
				if (user == null)
					continue;
				if (user.getUsername().equals(username)) {
					userExists = true;
					break;
				}
			}
			return userExists;
		}

		/**
		 * 
		 */
		public void onGameEnd(GameEvent e) {
			main(e.sourceGame + GameSocketServer.GAME_ENDED);
		}

		/**
		 * 
		 */
		public void onGameStart(GameEvent e) {
			main(e.sourceGame + GameSocketServer.GAME_STARTED);
		}

	}

	/**
	 * This method is placed here because it I don't want to recreate it on
	 * every socket instance
	 * 
	 * @param instance
	 * @return
	 */
	public static synchronized boolean allowcateUser(Object instance,
			Object[] users) {
		for (int i = 0; i < users.length; i++) {
			if (users[i] == null) {
				users[i] = instance;
				return true;
			}
		}
		// could not allocate space for user
		return false;
	}

	/**
	 * Very BadAlgorithm; switch for dictionary related algorithm
	 * 
	 * @param word
	 * @return the input word without all the bannedwords
	 */
	public static String sanitizeWord(String word) {
		String result = word;
		for (Object obj : bannedWords) {
			String badWord = (String) obj;
			if (word.contains(badWord)) {
				result = "";
				// RemoveInstanceOfWord and add all other parts
				for (String part : word.split(badWord)) {
					result += part + "@#$%&!";
				}
			}
		}
		return result;
	}

}
