package edu.uprm.ece.icom4015.jabrisca.client;

import java.awt.Point;
import java.util.concurrent.BlockingQueue;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import edu.uprm.ece.icom4015.jabrisca.client.views.AnimatedJabriscaJPanel;
import edu.uprm.ece.icom4015.jabrisca.client.views.JabriscaJPanel;
import edu.uprm.ece.icom4015.jabrisca.server.ChatSocketServer;
import edu.uprm.ece.icom4015.jabrisca.server.GameSocketServer;
import edu.uprm.ece.icom4015.jabrisca.server.ManagerSocketServer;
import edu.uprm.ece.icom4015.jabrisca.server.VanillaSocketThread;

public class JabriscaModel implements Runnable {
	private static final String GAME_SOCKET = "GameSocket";
	private static final String MANAGER_SOCKET = "ManagerSocket";
	private static final String CHAT_SOCKET = "ChatSocket";
	private static final int ATTEMPTS_TO_CONNECT = 3;
	private JabriscaJPanel lobby;
	private JabriscaJPanel loginsingup;
	private JabriscaJPanel newgame;
	private JabriscaJPanel gameboard;
	private JabriscaJPanel endgamewindow;
	private JabriscaJPanel currentWindow;
	private BlockingQueue instructions;
	private boolean done = false;
	private ModelStates state;
	public boolean logginEnabled = true;
	private final String hostURL = "localhost";
	private final int mainPort = ManagerSocketServer.managerSocketServerPort;
	private int chatPort = ManagerSocketServer.chatSocketServerPort;
	private int gamePort = ManagerSocketServer.gameSocketServerPort;
	private SocketClient mainSocket;
	private SocketClient chatSocket;
	private SocketClient gameSocket;
	private final int MAX_PLAYERS_RESULTS = 10;
	private int currentPlayersPosition = 0;
	private int currentLeaderPosition;
	public final int MAX_LEADER_RESULTS = MAX_PLAYERS_RESULTS;
	public static final String WAIT_TIME_OUT = "messageTimedOut";
	public static final String LOG_FILTER = MANAGER_SOCKET;
	public JabriscaModel(BlockingQueue instructions) {
		this(null, null, null, null, null, instructions);
	}

	public JabriscaModel(JabriscaJPanel lobby, JabriscaJPanel loginsingup,
			JabriscaJPanel newgame, JabriscaJPanel gameboard,
			JabriscaJPanel endgamewindow, BlockingQueue instructions) {
		super();
		this.lobby = lobby;
		this.loginsingup = loginsingup;
		this.newgame = newgame;
		this.gameboard = gameboard;
		this.endgamewindow = endgamewindow;
		this.instructions = instructions;
		this.state = ModelStates.loginsingup;
	}

	public void run() {
		try {
			while (!done) {
				try {
					String instruction = "";
					// Force the model to try and dequeue and wait for the next
					// instruction
					try {
						instruction = (String) instructions.take();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (logginEnabled&&instructions.contains(LOG_FILTER)) {
						System.out.println("Status:\n" + state + "\n"
								+ instruction);
					}
					// Interpret state specific instructions
					switch (state) {
					case loginsingup:
						if (instruction.equals("login")) {
							// Get values and try to login user, await server
							// verification
							transitionToState(state);
							String result = sendMessageToServer(ManagerSocketServer.LOGIN_USER);
							if (result
									.contains(ManagerSocketServer.LOGIN_SUCCESS))
								transitionToState(ModelStates.lobby);
							else {
								// TODO handle exception
								JOptionPane.showMessageDialog(currentWindow,
										"Error Login in. Error:" + result);
							}
						} else if (instruction.equals("mouseClicked-jabrisca")) {//
							if (loginsingup instanceof AnimatedJabriscaJPanel) {
								// TODO animate login screen

							}//
						} else if (instruction.equals("singup")) {//
							// TODO use socket client to sign user in the server
							// and await server confirmation
							transitionToState(state);
							String result = sendMessageToServer(ManagerSocketServer.SIGNUP_USER);
							if (result
									.contains(ManagerSocketServer.SIGNUP_SUCCESS))
								transitionToState(ModelStates.lobby);
							else {
								// TODO handle exception
								JOptionPane.showMessageDialog(currentWindow,
										"Error Signingup. Error:" + result);
							}
						}
						break;
					case lobby:
						if (instruction.contains("windowOpened")) {
							// TODO Populate lobby with data
							String result = sendMessageToSomeSocket(
									ChatSocketServer.LOGIN_USER, "", chatSocket);
							System.out.println(result);
							if (result.contains(ChatSocketServer.LOGIN_SUCCESS)) {
								updateChat_Lobby("Jabrisca", "Welcome!");
							} else if (result
									.contains(ChatSocketServer.LOGIN_FAIL)) {
								updateChat_Lobby("Jabrisca",
										"Error login into chat!");
								JOptionPane.showMessageDialog(currentWindow,
										result);
							}

							loadPlayers(currentPlayersPosition,
									MAX_PLAYERS_RESULTS);
							loadLeaderboard(currentLeaderPosition,
									MAX_LEADER_RESULTS);
							loadRooms();
						} else if (instruction.equals("games_create")) {
							// Show the user the create game window
							transitionToState(ModelStates.newgame);
						} else if (instruction.equals("players_load")) {
							// TODO load more player data
							loadPlayers(currentPlayersPosition,
									MAX_PLAYERS_RESULTS);
						} else if (instruction.equals("leaderBoards_load")) {
							// TODO load more leaderBoards data
							loadLeaderboard(currentLeaderPosition,
									MAX_LEADER_RESULTS);
						} else if (instruction.equals("lobbyChat_send")) {
							// TODO Fetch message and send it to the chat server
							sendMessageToChat_Lobby();
						} else if (instruction.equals("reloadGames")) {
							// TODO Fetch the playable games rooms and display
							// them on the table
							loadRooms();
						} else if (instruction.equals("options_myScores")) {
							// TODO fetch all the scores from the user and
							// present
							// them
							// in a JOptionPane DisplayDialog
						} else if (instruction.equals("options_logout")) {
							// TODO send the server a logout request and await
							// for server confirmation
							String result = sendMessageToServer(ManagerSocketServer.LOGOUT_USER);
							// SERVER should log out the user from the other
							// servers
							if (result
									.contains(ManagerSocketServer.LOGOUT_SUCCESS))
								transitionToState(ModelStates.loginsingup);
							else {
								// TODO handle exception
								JOptionPane.showMessageDialog(currentWindow,
										"Error Loginin. Error:" + result);
							}

							transitionToState(ModelStates.loginsingup);

						} else if (instruction.contains("games_table")) {
							// TODO parse the message from the table
							if (instruction.contains(":")) {
								String parameters = instruction.split(":")[1];
								Object[] parametersKeyValuePair = parameters
										.split(",");
								int startGame = JOptionPane.showConfirmDialog(
										currentWindow,
										"Are you sure you want to join\n"
												+ parameters);
								// System.out.println("Option=" + startGame
								// + ",constant ="
								// + JOptionPane.YES_OPTION);
								// // Check if user wants to transition state
								if (startGame == JOptionPane.YES_OPTION) {
									transitionToState(ModelStates.gameboard,
											parametersKeyValuePair);
								} else {
									// TODO user did not click yes
								}
							}
						} else if (instruction.contains(CHAT_SOCKET)) {
							if (instruction.contains(ChatSocketServer.MESSAGE)) {
								String parameters = instruction.split("@")[1];
								String username = ((parameters
										.split("username=")[1]).split(",")[0]);
								String message = ((parameters.split("message=")[1])
										.split(",")[0]);
								updateChat_Lobby(username, message);
							}
						} else if (instruction.contains(MANAGER_SOCKET)) {
							// TODO
							if (instruction
									.contains(GameSocketServer.GET_PLAYERS_ONLINE_SUCCESS)) {
								String parameters = instruction.split("@")[1];
								updatePlayers(parameters);
							} else if (instruction
									.contains(GameSocketServer.GET_TOP_PLAYERS_SUCCESS)) {
								String parameters = instruction.split("@")[1];
								updateLeaderboard(parameters);
							}
						}
						break;
					case newgame:
						if (instruction.equals("createRoom")) {
							// TODO Create a new game in the server
							// "GameSocket-"
							createGameRoom();
							transitionToState(ModelStates.gameboard);
						} else if (instruction.contains("windowClosed")) {// windowClosed
							transitionToState(ModelStates.lobby);
						}
						break;
					case gameboard:
						if (instruction.contains("windowOpened")) {
							// TODO Create game socket and try to join game
							// fetch the current cards and display proper
							// image and the usernumber
							// state.getStateParametersValueKeyPair();
							// sendMessageToServer(ManagerSocketServer.JOIN_GAME,
							// sendMessageToServer(GameSocketServer.LOGIN_USER,
							// parameters);
						} else if (instruction.equals("options_surrender")) {
							// TODO Tell the server user has surrendered
							sendMessageToSomeSocket(
									GameSocketServer.PLAYER_SURRENDER, "",
									gameSocket);
							transitionToState(ModelStates.endgame);
						} else if (instruction.equals("boardChat_send")) {
							// TODO Tell the user the number of cards remaining
							if (instruction.contains(ChatSocketServer.MESSAGE)) {
								String parameters = instruction.split("@")[1];
								String username = ((parameters
										.split("username=")[1]).split(",")[0]);
								String message = ((parameters.split("message=")[1])
										.split(",")[0]);
								updateChat_GameBoard(username, message);
							}
						} else if (instruction
								.equals("mouseClicked-boardGame_deck")) {
							// TODO Tell the user the number of cards remaining
						} else if (instruction.equals("options_tradeCard")) {
							// TODO pomp the user through a JOption pane to
							// select a card use an image to help the user
							// later inform the server
							// Get Card Name
							String[] options = new String[] { "Card 1",
									"Card 2", "Card 3", "Cancel" };
							int cardValue = JOptionPane.showOptionDialog(currentWindow,
									"TradeCard", "Select a card to trade.",
									JOptionPane.DEFAULT_OPTION,
									JOptionPane.PLAIN_MESSAGE, null, options,
									options[0]);
							if(cardValue==3){
								continue;//Continue Loop
							}
							String result = sendMessageToSomeSocket(
									GameSocketServer.PLAYER_TRADEDCARD, "cardNumber="+cardValue,
									gameSocket);
							if (result
									.contains(GameSocketServer.PLAYER_CAN_TRADE_CARD)) {
								// TODO tradecard
							} else if (result
									.contains(GameSocketServer.PLAYER_CANT_TRADE_CARD)) {
								// TODO tell the user he cant tradecard;
							}
						} else if (instruction.equals("options_blackHand")) {
							// TODO try to tell the server the user has the
							// black hand and has won
							int temp = JOptionPane
									.showConfirmDialog(currentWindow,
											"Are you sure you want to play the blackhand?");
							if (temp == JOptionPane.YES_OPTION) {
								String result = sendMessageToSomeSocket(
										GameSocketServer.PLAYER_PLAYED_BLACKHAND,
										"", gameSocket);
								if (result
										.contains(GameSocketServer.PLAYER_CAN_BLACKHAND)) {
									// TODO playerWon
								} else if (result
										.contains(GameSocketServer.PLAYER_CANT_BLACKHAND)) {
									// TODO tell the user he cant play black
									// card
								}
							}

						} else if (instruction
								.contains("mouseClicked-boardGame_myCard")) {
							// TODO getsocket and Game logic stuff
							// Then Animate the board if possible
							// sendMessageToSomeSocket(GameSocketServer.PLAYER_PLAYED_CARD,
							// instruction.spli("mouseClicked-boardGame_myCard")[0],gameSocket);
							if (gameboard instanceof AnimatedJabriscaJPanel) {
								String animationName = (String) state
										.getStateParameterValue("playCardAnimationName");
								String animationParameters = (String) state
										.getStateParameterValue("playCardAnimationParameters");

								if (animationParameters != null) {
									animationName += ":" + animationParameters;
								}

								AnimatedJabriscaJPanel boardanimator = (AnimatedJabriscaJPanel) gameboard;
								// remove extras string and get the name of the
								// card
								String target = instruction
										.split("mouseClicked-")[1];
								// get the seat assigned to the player by server
								int playerSeat = (Integer) state
										.getStateParameterValue("playerSeat");
									
								String destination = "boardGame_player"
										+ (playerSeat + 1) + "Card";

								if (boardanimator.animate(animationName,
										target, destination)) {
									// Animation was played
								} else {
									// Animation could not be played
								}
							}
						} else if (instruction.contains(CHAT_SOCKET)) {
							if (instruction.contains(ChatSocketServer.MESSAGE)) {
								String parameters = instruction.split("@")[1];
								String username = ((parameters
										.split("username=")[1]).split(",")[0]);
								String message = ((parameters.split("message=")[1])
										.split(",")[0]);
								updateChat_GameBoard(username, message);
							}
						}
						break;
					case endgame:
						if (instruction.contains("windowOpened")) {
							//TODO fetch game state
						} else if (instruction.contains("surrender")) {
							// TODO tell the server
							String result = sendMessageToSomeSocket(
									GameSocketServer.PLAYER_SURRENDER, "",
									gameSocket);
							if (result
									.contains(GameSocketServer.PLAYER_CANT_SURRENDER)) {
								// TODO handle player cant surrender
							}
							transitionToState(ModelStates.lobby);
						} else if (instruction
								.contains("windowClosed-EndGameWindow")) {
							// windowClosed
							transitionToState(ModelStates.lobby);
						} else if (instruction.contains("continue")) {
							// TODO player continues
							String result = sendMessageToSomeSocket(
									GameSocketServer.PLAYER_CAN_CONTINUE, "",
									gameSocket);
							if (result
									.contains(GameSocketServer.PLAYER_CAN_CONTINUE_TRUE)) {
								// Fetch the game parameters for the next game
								// in the tournament
								sendMessageToSomeSocket(
										GameSocketServer.PLAYER_CONTINUED, "",
										gameSocket);
							} else if (result
									.contains(GameSocketServer.PLAYER_CANT_CONTINUE_TRUE)) {
								// TODO if game is not in tournament mode
								// continue to the lobby
								transitionToState(ModelStates.lobby);
							}
						}
						break;
					default:
						break;
					}

					// Interpret non state specific instructions
					if (instruction.equals("howToPlay")) {
						JOptionPane.showMessageDialog(currentWindow,
								"You play brisca as follows");
					} else if (instruction.equals("reconnect")) {
						setState(ModelStates.loginsingup);
						attempConnection();
					} else if (instruction.contains(WAIT_TIME_OUT)) {
						// TODO
					}
				} catch (Exception e) {
					String output = "Unexpected minor error, gracefully dealing with it in "
							+ getClass().getSimpleName()
							+ ".Error: "
							+ e.getClass().getSimpleName();
					System.out.println(output);
					JOptionPane.showMessageDialog(currentWindow, output);
				}
			}
		} catch (Exception e) {
			String output = "O CRAP!! YOU BROKE IT ! But I'm gracefully dealing with it in "
					+ getClass().getSimpleName()
					+ ".Error: "
					+ e.getClass().getSimpleName();
			System.out.println(output);
			JOptionPane.showMessageDialog(currentWindow, output);
		}

	}

	private void loadRooms() {
		// TODO Auto-generated method stub
		// rooms=room1{key1:value;key2:value...},room1{key1:value;key2:value...}

	}

	private void updateLeaderboard(String parameters) {
		// TODO Auto-generated method stub
		String users = ((parameters.split("users=")[1]).split(",")[0]);
		lobby.fetchComponentAndAddValueJTextArea(null, "leaderBoards_display",
				users, true);
	}

	private void updatePlayers(String parameters) {
		// TODO Auto-generated method stub
		String users = ((parameters.split("users=")[1]).split(",")[0]);
		lobby.fetchComponentAndAddValueJTextArea(null, "players_display",
				users, true);
	}

	/**
	 * @param username
	 * @param message
	 */
	private void updateChat_Lobby(String username, String message) {
		lobby.fetchComponentAndAddValueJTextArea(null, "lobbyChat_display",
				"\n" + username + ":" + message);
	}

	private void updateChat_GameBoard(String username, String message) {
		gameboard.fetchComponentAndAddValueJTextArea(null, "lobbyChat_display",
				"\n" + username + ":" + message);
	}

	private void createGameRoom() {
		// TODO Auto-generated method stub
		// sendMessageToSomeSocket(ManagerSocketServer.CREATE_GAME, parameters);
		gameSocket = gameSocket == null ? new SocketClient(hostURL, chatPort,
				instructions, GAME_SOCKET) : gameSocket;
		sendMessageToServer(ManagerSocketServer.CREATE_GAME);
		sendMessageToSomeSocket(GameSocketServer.LOGIN_USER, "", gameSocket);
	}

	private void sendMessageToChat_Lobby() {
		// TODO Auto-generated method stub
		String message = lobby.fetchJTextValue("lobbyChat_message");
		sendMessageToSomeSocket(ChatSocketServer.MESSAGE, "message=" + message,
				chatSocket);
	}

	private void loadLeaderboard(int currentPlayersPosition, int quantity) {
		// TODO Auto-generated method stub
		lobby.setStatus("Loading Leaderboards");
		String result = sendMessageToServer(GameSocketServer.GET_TOP_PLAYERS,
				"startAt=" + currentPlayersPosition + ",quantity=" + quantity);
		if (result.contains(GameSocketServer.GET_TOP_PLAYERS_SUCCESS)) {
			String parameters = result.split("@")[1];
			updateLeaderboard(parameters);
		}
	}

	private void loadPlayers(int currentPlayersPosition, int quantity) {
		// TODO Auto-generated method stub
		lobby.setStatus("Loading Players");
		String result = sendMessageToServer(
				GameSocketServer.GET_PLAYERS_ONLINE, "startAt="
						+ currentPlayersPosition + ",quantity=" + quantity);
		if (result.contains(GameSocketServer.GET_PLAYERS_ONLINE_SUCCESS)) {
			String parameters = result.split("@")[1];
			updatePlayers(parameters);
		}
	}

	/**
	 * Same as sendMessageToServer(String message,String extraParameters)
	 * 
	 * @param message
	 *            to be send
	 * @return
	 */
	private String sendMessageToServer(String message) {
		return sendMessageToServer(message, null);
	}

	/**
	 * Deals with the mainSocket in sending messages to the server
	 * 
	 * @param message
	 * @param extraParameters
	 * @return
	 */
	private String sendMessageToServer(String message, String extraParameters) {
		return sendMessageToSomeSocket(message, extraParameters, mainSocket);
	}

	/**
	 * Deals with the sending a properly formated message to any one socket
	 * 
	 * @param message
	 * @param extraParameters
	 * @param socket
	 * @return the server response, fails gracefully if an error occurs
	 */
	private String sendMessageToSomeSocket(String message,
			String extraParameters, VanillaSocketThread socket) {
		String result = null;
		try {
			result = socket.sendMessageWaitResponse(message + "@" + "username="
					+ state.getStateParameterValue("username") + ","
					+ "password=" + state.getStateParameterValue("password"))
					+ extraParameters;
		} catch (Exception e) {
			String output = "Unexpected error, gracefully dealing with it in "
					+ getClass().getSimpleName() + ". Error: "
					+ e.getClass().getSimpleName();
			System.out.println(output);
			JOptionPane.showMessageDialog(currentWindow, output);
		}
		return result;
	}

	/**
	 * This method verifies if the transition can be made and then atomically
	 * executes the transition.
	 * 
	 * @param nextState
	 */
	private void transitionToState(ModelStates nextState)
			throws IllegalStateException {
		// TODO Actually implements this method fetch the value of the
		// parameters for the next state
		Object[] stateParameters = state.getStateParametersValues();
		// Map all posible transitions
		switch (state) {
		case loginsingup:
			switch (nextState) {
			case loginsingup:
				Object[] temp = { loginsingup.fetchJTextValue("username"),
						loginsingup.fetchJTextValue("password") };
				stateParameters = temp;
			case lobby:
				break;
			default:
				throw new IllegalStateException();
			}
			break;
		case lobby:
			switch (nextState) {
			case lobby:
				break;
			case newgame:
				break;
			case gameboard:
				break;
			default:
				throw new IllegalStateException();
			}
			break;
		case endgame:
			switch (nextState) {
			case endgame:
				break;
			case lobby:
				break;
			case gameboard:
				break;
			default:
				throw new IllegalStateException();
			}
			break;
		case newgame:
			switch (nextState) {
			case newgame:
				break;
			case lobby:
				break;
			case gameboard:
				break;
			default:
				throw new IllegalStateException();
			}
			break;
		case gameboard:
			switch (nextState) {
			case gameboard:
				break;
			case endgame:
				break;
			default:
				throw new IllegalStateException();
			}
			break;
		}
		setState(nextState);
		if (stateParameters != null)
			state.setStateParameterValues(stateParameters);
	}

	/**
	 * Transition to new state and set the parameters for the next state
	 * 
	 * @param nextState
	 *            to transition to
	 * @param parametersKeyValuePair
	 *            a string array of parameters joined as key value pairs
	 */
	private void transitionToState(ModelStates nextState,
			Object[] parametersKeyValuePair) {
		// TODO Actually implement this method
		transitionToState(nextState);
	}

	/**
	 * This method handles all parameter transitions
	 * 
	 * @param startState
	 *            state
	 * @param toState
	 */

	/**
	 * Start the model thread and setting preparing all variables
	 */
	public void start() throws IllegalStateException {
		if (instructions == null || endgamewindow == null || gameboard == null
				|| lobby == null || loginsingup == null || newgame == null)
			throw new IllegalStateException();

		done = false;
		// Start Socket connection
		attempConnection();
		setState(ModelStates.loginsingup);
		newgame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		endgamewindow
				.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

		Thread myThread = new Thread(this);
		myThread.start();
	}

	private void attempConnection() {
		int i = 0;
		boolean connecting = true;
		if (!(currentWindow == null))
			currentWindow.setStatus("Attempting to connect...");
		while (connecting) {
			try {
				setupSocketConnection();
				connecting = false;
			} catch (Exception e) {
				if (i < ATTEMPTS_TO_CONNECT) {
					i++;
					if (!(currentWindow == null))
						currentWindow
								.setProgressBar((100 / ATTEMPTS_TO_CONNECT) * i);
				} else {
					connecting = false;// stop trying to connect
					// TODO handle no connection
				}
				System.out
						.println("Unexpected error, gracefully dealing with it in "
								+ getClass().getSimpleName()
								+ "."
								+ e.getClass().getSimpleName());
			}
		}
		if (!(currentWindow == null))
			if (mainSocket.isConnected())
				currentWindow.setStatus("Connected.");
			else
				currentWindow.setStatus("Can't connect.");
	}

	/**
	 * Connect to the chat server and function as it is so desired
	 */
	private void setupSocketConnection() throws Exception {
		// TODO Auto-generated method stub
		mainSocket = new SocketClient(hostURL, mainPort, instructions,
				MANAGER_SOCKET);
		chatSocket = new SocketClient(hostURL, chatPort, instructions,
				CHAT_SOCKET);
		/**
		 * chatPort = Integer.parseInt(mainSocket.sendMessageWaitResponse(
		 * ManagerSocketServer.GET_CHAT_PORT, null, null).split(
		 * ManagerSocketServer.END_MESSAGE_DELIMETER)[0]); gamePort =
		 * Integer.parseInt(mainSocket.sendMessageWaitResponse(
		 * ManagerSocketServer.GET_GAME_PORT, null, null).split(
		 * ManagerSocketServer.END_MESSAGE_DELIMETER)[0]);
		 */

	}

	/**
	 * Switch everything to the specified states
	 * 
	 * @param state
	 *            to set
	 */
	private void setState(ModelStates state) {
		Point windowPoint = currentWindow == null ? new Point(50, 50)
				: currentWindow.getLocation();
		switch (state) {
		case loginsingup:
			currentWindow = loginsingup;
			loginsingup.setLocation(windowPoint);// set window is a custom
													// location
			endgamewindow.setVisible(false);
			gameboard.setVisible(false);
			lobby.setVisible(false);
			newgame.setVisible(false);
			break;
		case lobby:
			currentWindow = lobby;
			lobby.setLocation(windowPoint); // set window in a custom
											// location
			endgamewindow.setVisible(false);
			gameboard.setVisible(false);
			loginsingup.setVisible(false);
			newgame.setVisible(false);
			break;
		case newgame:
			currentWindow = newgame;
			newgame.setLocation(windowPoint); // set window in a custom
												// location
			endgamewindow.setVisible(false);
			gameboard.setVisible(false);
			loginsingup.setVisible(false);
			lobby.setVisible(false);
			break;
		case gameboard:
			currentWindow = gameboard;
			gameboard.setLocation(windowPoint);
			endgamewindow.setVisible(false);
			lobby.setVisible(false);
			loginsingup.setVisible(false);
			newgame.setVisible(false);
			break;
		case endgame:
			currentWindow = endgamewindow;
			endgamewindow.setLocation(windowPoint);
			lobby.setVisible(false);
			gameboard.setVisible(false);
			loginsingup.setVisible(false);
			newgame.setVisible(false);
			break;
		default:
			break;
		}

		// set the state of the machine
		this.state = state;
		currentWindow.setVisible(true);
	}

	/**
	 * Stop the model thread by notifying the thread that it is done
	 */
	public void stop() {
		done = true;
	}

	// / This model operates in two ways one is a state machine and below are
	// its states
	enum ModelStates {
		loginsingup() {
			@Override
			void setStateParametersValues() {
				String[] stateParameters = { "username", "password" };
				parameterKeys = stateParameters;
			}
		},
		lobby() {
			@Override
			void setStateParametersValues() {
				String[] stateParameters = { "username", "password" };
				parameterKeys = stateParameters;
			}
		},
		newgame() {
			@Override
			void setStateParametersValues() {
				String[] stateParameters = { "username", "password" };
				parameterKeys = stateParameters;
			}
		},
		gameboard() {
			@Override
			void setStateParametersValues() {
				String[] stateParameters = { "username", "password",
						"roomName", "teamGame", "blackHandEnabled",
						"onGameStartCardSwap", "surrenderEnabled",
						"timeLimitOnTurns", "inTournamentMode",
						"playCardAnimationName", "playCardAnimationParameters",
						"playerSeat"
				// number given to the player by the server to that is the
				// players original position in the first play or his "seat" in
				// the game
				};
				parameterKeys = stateParameters;
			}
		},
		endgame() {
			@Override
			void setStateParametersValues() {
				String[] stateParameters = { "roomName", "teamGame",
						"blackHandEnabled", "onGameStartCardSwap",
						"surrenderEnabled", "timeLimitOnTurns",
						"inTournamentMode" };
				parameterKeys = stateParameters;
			}
		};

		Object[] parameterValues = {

		};
		String[] parameterKeys;

		/**
		 * Constructor
		 */
		private ModelStates() {
			setStateParametersValues();
		}

		/**
		 * Conver the state number and state parameter along with the state
		 * values to string
		 */
		public String toString() {
			String parameters = ":";
			parameters += getStateParametersValueKeyPair();
			int stateNumber = Integer.parseInt(this.getClass().getTypeName()
					.split("\\$ModelStates\\$")[1]);
			return stateNumber + parameters;
		}

		/**
		 * Manually set all the values for the parameterValues
		 * 
		 * @param parameterValues
		 */
		public void setStateParameterValues(Object[] parameterValues) {
			this.parameterValues = parameterValues;
		}

		/**
		 * This method is ran when each instance of the enum is instantiated-
		 * its purpose is to allow each state to specify its state parameters
		 */
		abstract void setStateParametersValues();

		public Object getStateParameterValue(int index) {
			return parameterValues[index];
		}

		/**
		 * @param index
		 *            an integer that represents a position
		 * @return specific key associated with the index
		 */
		public Object getStateParameterKey(int index) {
			return parameterKeys[index];
		}

		/**
		 * get a specific parameter
		 * 
		 * @param key
		 * @return
		 */
		public Object getStateParameterValue(String key) {
			int index = -1;
			for (int i = 0; i < parameterKeys.length; i++) {
				if (key.equals(parameterKeys[i])) {
					index = i;
					break; // exit loop
				}
			}

			// Check if card could be found
			if (index < 0) {
				return null;
			} // else return the found card if so
			return parameterValues[index];
		}
		/**
		 * get a specific parameter
		 * 
		 * @param key
		 * @return
		 */
		public Object setStateParameterValue(String key,Object value) {
			int index = -1;
			for (int i = 0; i < parameterKeys.length; i++) {
				if (key.equals(parameterKeys[i])) {
					index = i;
					break; // exit loop
				}
			}

			// Check if card could be found
			if (index < 0) {
				return null;
			} // else return the found card if so
			Object result =parameterValues[index];
			parameterValues[index] = value;
			return result;
		}

		/**
		 * return all the values for each parameter
		 * 
		 * @return
		 */
		public Object[] getStateParametersValues() {
			return parameterValues;
		}

		public String getStateParametersValueKeyPair() {
			String parameters = "";
			for (int i = 0; i < parameterKeys.length; i++) {
				// Add the parameter key
				parameters += parameterKeys[i] + "=";
				// add the parameter values if you can null otherwise
				if (parameterValues.length > i) {
					parameters += parameterValues[i];
				} else {
					parameters += "null";
				}
				// add trailing comma to all but the last element
				if (i < parameterKeys.length - 1)
					parameters += ",";
			}
			return parameters;
		}
		// \"static ModelStates values()\" returns all values of the enum
	}

}
