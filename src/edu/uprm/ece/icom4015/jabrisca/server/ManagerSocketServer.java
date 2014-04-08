package edu.uprm.ece.icom4015.jabrisca.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ManagerSocketServer extends VanillaSocketServer {
	private static ManagerSocketServer instance;
	private static boolean listening = false;
	public static final int MAX_CLIENTS = 60;
	public static int gameSocketServerPort = 5050;
	public static int chatSocketServerPort = 5757;
	public static int managerSocketServerPort = 6767;
	public static GameSocketServer gameServer;
	public static ChatSocketServer chatServer;
	private String[] badWords = { "cabron","pendejo", "popo", "puta" };
	public static BlockingDeque bannedWords = new LinkedBlockingDeque();
	public static int currentUsers=0;
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
		if (ports != null && ports.length > 0) {
			// TODO set ports
		}
		if (!listening){
			(new Thread(this)).start();
			listening = true;
		}
	}

	/**
	 * 
	 */
	public void run() {
		listening = true;
		chatServer = ChatSocketServer.getServerSingleton();
		chatServer.start(chatSocketServerPort);
		gameServer =  GameSocketServer.getServerSingleton();
		gameServer.start(gameSocketServerPort);
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(managerSocketServerPort);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("ManagerServer listening on " + managerSocketServerPort
				+ "...");
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
	

	class MainSocketThread extends VanillaSocketThread {

		private User user;

		public MainSocketThread(Socket socket) {
			super(socket);
		}

		@Override
		public void main(String pushedMessages) {
			// TODO implement socket on the server
			try {
				System.out.println(pushedMessages);
				if (pushedMessages.contains(SIGNUP_USER)) {
					// TODO check if username is registed already
					String parameters = pushedMessages.split("@")[1];
					String username = ((parameters.split("username=")[1]).split(",")[0]);
					String password = ((parameters.split("password=")[1]).split(",")[0]);
					this.user = User.getInstance(username, password, 0);
					if(allowcateUser(user)){
						out.println(SIGNUP_SUCCESS + END_MESSAGE_DELIMETER);
						System.out.println("Registered User" + username);
					} else {
						out.println(SIGNUP_FAILED + END_MESSAGE_DELIMETER);
					}
				} else if (pushedMessages.contains(LOGIN_USER)) {
					// TODO check if user name is valid and create new user
					String parameters = pushedMessages.split("@")[1];
					String username = ((parameters.split("username=")[1]).split(",")[0]);
					username = sanitizeWord(username);
					String password = ((parameters.split("password=")[1]).split(",")[0]);
					boolean userExists = false;
					for(User user:users){
						if(users ==null)continue;
						if(user.getUsername().equals(username)&&user.getPassword().equals(password)){
							userExists = true;
							this.user = user;
							user.setLoggedIn(userExists);
							break;
						}
					}
					if(userExists){
						out.println(LOGIN_SUCCESS + END_MESSAGE_DELIMETER);
					} else {
						out.println(LOGIN_FAIL + END_MESSAGE_DELIMETER);
					}
				} else if (pushedMessages.contains(GET_CHAT_PORT)) {
					// TODO check if user name is valid and create new user
					out.println(chatSocketServerPort + END_MESSAGE_DELIMETER);
				} else if (pushedMessages.contains(GET_GAME_PORT)) {
					// TODO check if user name is valid and create new user
					out.println(gameSocketServerPort + END_MESSAGE_DELIMETER);
				} else if (pushedMessages.contains(CREATE_GAME)) {
					// TODO do something special: verify game can be created,
					// switch the user to the proper chat room, create game ...
					
				} else if (pushedMessages.contains(JOIN_GAME)) {
					// TODO do something special: verify game can be Joined,
					// switch the user to the proper chat room, create game ...
				}
			} catch (Exception e) {
				System.out.println("Gracefully dealt with error in "
						+ getClass().getTypeName() + ",Exception"
						+ e.getClass().getSimpleName());
			}
		}

		
	}

	/**
	 * This method is placed here because it I don't want to recreate it on every socket instance
	 * @param instance
	 * @return
	 */
	public static synchronized boolean allowcateUser(User instance) {
		for(int i=0;i<users.length;i++){
			if(users[i] == null){
				users[i] = instance;
				instance.setUserNumber(i);
				instance.setLoggedIn(true);
				currentUsers++;
				return true;
			}
		}
		//could not allocate space for user
		return false;
	}
	/**
	 * Very BadAlgorithm switch for dictionary
	 * @param word
	 * @return the input word without all the bannedwords
	 */
	public static String sanitizeWord(String word) {
		String result = word;
		for(Object obj:bannedWords){
			String badWord = (String)obj;
			if(word.contains(badWord)){
				result = "";
				for(String part: word.split(word))
				{
					result+=part;
				}
			}
		}
		return result;
	}
	
}
