package edu.uprm.ece.icom4015.jabrisca.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatSocketServer extends VanillaSocketServer {
	public static final int MAX_NUMBER_OF_ROOMS = 15;
	public static final int DEFAULT_ROOM = MAX_NUMBER_OF_ROOMS + 1 - 1;
	private static boolean listening = false;
	private static ChatSocketServer instance;
	private static int currentUsers = 0;
	private ChatRoom[] rooms = new ChatRoom[MAX_NUMBER_OF_ROOMS + 1];
	private int port = ManagerSocketServer.chatSocketServerPort;
	// Verbs
	public static final String LOGIN_USER = ManagerSocketServer.LOGIN_USER;
	public static final String LOGIN_SUCCESS = ManagerSocketServer.LOGIN_SUCCESS;
	public static final String LOGIN_FAIL = ManagerSocketServer.LOGIN_FAIL;
	public static final String MESSAGE = "userHasAMessage";
	public static final String USER_IS_TYPING = "userIsTyping";
	public static final String END_MESSAGE_DELIMETER = ManagerSocketServer.END_MESSAGE_DELIMETER;
	public static final String SHOW_USERS = ManagerSocketServer.SHOW_USERS;

	/**
	 * The constructor is an introvert and thus
	 */
	private ChatSocketServer() {
		super();
		for (int i = 0; i < rooms.length; i++) {
			if (i == DEFAULT_ROOM)
				rooms[i] = ChatRoom
						.getInstance(ManagerSocketServer.MAX_CLIENTS);
			else
				rooms[i] = ChatRoom
						.getInstance(GameSocketServer.MAX_NUMBER_OF_USERS_PER_GAME);
		}
	}

	@Override
	void initializeUsers() {
		users = new User[ManagerSocketServer.MAX_CLIENTS];
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ChatSocketServer server = ChatSocketServer.getServerSingleton();
		server.start(null);
	}

	/**
	 * @return
	 */
	public synchronized static ChatSocketServer getServerSingleton() {
		if (instance == null) {
			instance = new ChatSocketServer();
		}
		return instance;
	}

	/**
	 * @param ports
	 */
	public synchronized void start(int... ports) {
		if (ports != null && ports.length > 0) {
			// TODO set ports
		}
		if (!listening) {
			(new Thread(this)).start();
			listening = true;
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("ChatServer listening on " + port + "...");
		while (listening) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println("Socket created at " + socket.getPort());
				VanillaSocketThread myThread = new ChatSocketThread(socket);
				(new Thread(myThread)).start();
			} catch (Exception e) {
				System.out.println("Gracefully dealt with error in "
						+ getClass().getTypeName() + ",Exception"
						+ e.getMessage());
			}
		}

	}

	class ChatSocketThread extends VanillaSocketThread {

		public ChatRoom room;
		private User user;

		public ChatSocketThread(Socket socket) {
			super(socket);
		}

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
						room = rooms[DEFAULT_ROOM];
						room.addUser(user);
						user.setChatSocket(this);
					} else {
						out.println(LOGIN_FAIL + END_MESSAGE_DELIMETER);
					}
				} else if (pushedMessages.contains(MESSAGE)) {
					String parameters = pushedMessages.split("@")[1];
					String username = ((parameters.split("username=")[1])
							.split(",")[0]);
					String message = ((parameters.split("message=")[1])
							.split(",")[0]);
					message = ManagerSocketServer.sanitizeWord(message);
					room.broadCast(MESSAGE + "@username=" + username + ","
							+"message=" + message, user);
				} else if (pushedMessages.contains(USER_IS_TYPING)) {
					String parameters = pushedMessages.split("@")[1];
					String username = ((parameters.split("username=")[1])
							.split(",")[0]);
					room.broadCast(USER_IS_TYPING + "@username=" + username
							+ "," + USER_IS_TYPING + "=" + USER_IS_TYPING, user);
				} else if (pushedMessages.contains(SHOW_USERS)&&user!=null) {
					// TODO check if user name is valid and create new user
					String result = "";
					for (User user : users) {
						if (user == null)
							continue;
						result+=user+",";
					}
					result = result.substring(0, result.length()-1);
					out.println(result + END_MESSAGE_DELIMETER);
				}
			} catch (Exception e) {
				System.out.println("Gracefully dealt with error in "
						+ getClass().getTypeName() + ",Excetion"
						+ e.getClass().getSimpleName());
			}
		}

	}

	public static synchronized boolean addUser(User user,int roomName) {
		boolean couldAllocate= ManagerSocketServer.allowcateUser(user, users);
		if(couldAllocate)
			currentUsers++;
		return couldAllocate;
	}
}
