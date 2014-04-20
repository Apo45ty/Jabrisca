package edu.uprm.ece.icom4015.jabrisca.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.Timer;

public class ChatSocketServer extends VanillaSocketServer {
	public static final int MAX_NUMBER_OF_ROOMS = 15; // this does not include
														// the default room that
														// is always added
	public static final int DEFAULT_ROOM = MAX_NUMBER_OF_ROOMS + 1 - 1; // thus
																		// the
																		// total
																		// number
																		// rooms
																		// goes
																		// up by
																		// one
	private static boolean listening = false;
	private static ChatSocketServer instance;
	private static int currentUsers = 0;
	public static ChatRoom[] rooms = new ChatRoom[MAX_NUMBER_OF_ROOMS + 1];
	private int port = ManagerSocketServer.chatSocketServerPort;
	// Verbs
	public static final String LOGIN_USER = ManagerSocketServer.LOGIN_USER;
	public static final String LOGIN_SUCCESS = ManagerSocketServer.LOGIN_SUCCESS;
	public static final String LOGIN_FAIL = ManagerSocketServer.LOGIN_FAIL;
	public static final String MESSAGE = "userHasAMessage";
	public static final String USER_IS_TYPING = "userIsTyping";
	public static final String END_MESSAGE_DELIMETER = ManagerSocketServer.END_MESSAGE_DELIMETER;
	public static final String SHOW_USERS = ManagerSocketServer.SHOW_USERS;
	public static final String MESSAGE_RECEIVED = "messageReceived";
	public static final String END_CONNECTION = ManagerSocketServer.END_CONNECTION;

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
			listening = true;
			(new Thread(this)).start();
		}
	}

	/**
	 * 
	 */
	public void run() {
		Thread.currentThread().setName("ChatSocketServer");
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e1) {
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
				System.out
						.println("Gracefully dealt with error in "
						/* + getClass().getTypeName() */+ ",Exception"
								+ e.getMessage());
			}
		}

	}

	class ChatSocketThread extends VanillaSocketThread {

		public ChatRoom room;
		private User user;
		// Fix you are using way too many threads !!!!
		private Timer timeout2;
		private boolean handledMessage = false;

		public ChatSocketThread(Socket socket) {
			super(socket);
		}

		@Override
		public void main(String pushedMessages) {
			System.out.println(pushedMessages);
			if (pushedMessages.contains(LOGIN_USER)) {
				String parameters = pushedMessages.split("@")[1];
				String username = ((parameters.split("username=")[1])
						.split(",")[0]);
				String password = ((parameters.split("password=")[1])
						.split(",")[0]);
				Thread.currentThread().setName("ChatSocketClient" + username);
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
					setRoom(DEFAULT_ROOM);
					user.setChatSocket(this);
				} else {
					out.println(LOGIN_FAIL + END_MESSAGE_DELIMETER);
				}
			} else if (pushedMessages.contains(END_CONNECTION)) {
				done = true;
			} else if (pushedMessages.contains(MESSAGE) && user != null
					&& !handledMessage) {
				String parameters = pushedMessages.split("@")[1];
				String username = ((parameters.split("username=")[1])
						.split(",")[0]);
				String message = ((parameters.split("message=")[1]).split(",")[0]);
				message = ManagerSocketServer.sanitizeWord(message);
				out.println(MESSAGE_RECEIVED + END_MESSAGE_DELIMETER);
				room.broadCast(MESSAGE + "@username=" + username + ","
						+ "message=" + message, user);
				handledMessage = true;
				timeout2 = new Timer(700, new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						handledMessage = false;
						timeout2.stop();
					}

				});
				timeout2.start();
			} else if (pushedMessages.contains(USER_IS_TYPING) && user != null) {
				String parameters = pushedMessages.split("@")[1];
				String username = ((parameters.split("username=")[1])
						.split(",")[0]);
				room.broadCast(USER_IS_TYPING + "@username=" + username + ","
						+ USER_IS_TYPING + "=" + USER_IS_TYPING, user);
			} else if (pushedMessages.contains(SHOW_USERS) && user != null) {
				String result = "";
				for (User user : users) {
					if (user == null)
						continue;
					result += user + ",";
				}
				result = result.substring(0, result.length() - 1);
				out.println(result + END_MESSAGE_DELIMETER);
			} // TODO add the rest of the methods
		}

		public boolean setRoom(int roomNum) {
			if (room != null)
				room.removeUser(user);
			room = rooms[roomNum];
			room.addUser(user);
			return true;
		}

	}

	/**
	 * Add User to a room and to chat list
	 * 
	 * @param user
	 * @param roomName
	 * @return
	 */
	public static synchronized boolean addUser(User user) {
		boolean couldAllocate = ManagerSocketServer.allowcateUser(user,
				getServerSingleton().getUsers());
		if (couldAllocate)
			currentUsers++;
		return couldAllocate;
	}

}
