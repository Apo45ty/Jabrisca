package edu.uprm.ece.icom4015.jabrisca.client;

import java.awt.Point;
import java.util.concurrent.BlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import edu.uprm.ece.icom4015.jabrisca.client.views.AnimatedJabriscaJPanel;
import edu.uprm.ece.icom4015.jabrisca.client.views.ImageDatabase;
import edu.uprm.ece.icom4015.jabrisca.client.views.JabriscaImageDatabase;
import edu.uprm.ece.icom4015.jabrisca.client.views.JabriscaJPanel;
import edu.uprm.ece.icom4015.jabrisca.server.ChatSocketServer;
import edu.uprm.ece.icom4015.jabrisca.server.GameSocketServer;
import edu.uprm.ece.icom4015.jabrisca.server.ManagerSocketServer;
import edu.uprm.ece.icom4015.jabrisca.server.VanillaSocketThread;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.BriscaGameRoom;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.ItalianDeckCard;

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
	private ImageDatabase imageDB = new JabriscaImageDatabase();
	private String currentGameFetched = "";
	private boolean guiIsBusy;
	public static final String WAIT_TIME_OUT = "messageTimedOut";
	public static final String LOG_FILTER = MANAGER_SOCKET;
	private static final int MAX_GAMES_TO_LOAD = 15;

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
			Thread.currentThread().setName("JabriscaModel");
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

					if (logginEnabled
							&& (LOG_FILTER == null || instruction
									.contains(LOG_FILTER))) {
						System.out.println("Status:\n" + state + "\n"
								+ instruction);
					}
					// Interpret state specific instructions
					switch (state) {
					case loginsingup:
						if (instruction.equals("login")) {
							// Get values and try to login user, await server
							// verification
							transitionToState(state, null);
							String result = sendMessageToServer(ManagerSocketServer.LOGIN_USER);
							if (result
									.contains(ManagerSocketServer.LOGIN_SUCCESS))

								transitionToState(ModelStates.lobby, null);
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
							transitionToState(state, null);
							String result = sendMessageToServer(ManagerSocketServer.SIGNUP_USER);
							if (result
									.contains(ManagerSocketServer.SIGNUP_SUCCESS))
								transitionToState(ModelStates.lobby, null);
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
							transitionToState(ModelStates.newgame, null);
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
								transitionToState(ModelStates.loginsingup, null);
							else {
								// TODO handle exception
								JOptionPane.showMessageDialog(currentWindow,
										"Error Login out. Error:" + result);
							}
							transitionToState(ModelStates.loginsingup, null);
						} else if (instruction
								.contains("mouseClicked-games_table")) {
							// TODO parse the message from the table
							if (instruction.contains(":")) {
								String parameters = instruction.split(":")[1];
								String[] parametersKeyValuePair = parameters
										.split(",");
								int startGame = JOptionPane.showConfirmDialog(
										currentWindow,
										"Are you sure you want to join\n"
												+ parameters);
								// // Check if user wants to transition state
								if (startGame == JOptionPane.YES_OPTION) {
									currentWindow.setStatus("Joining room...");
									String roomName = parameters
											.split(GameSocketServer.ROOMNAMEKEY)[1]
											.split(",")[0];
									long oldtimeout = mainSocket.TIME_OUT_WAIT;
									mainSocket.TIME_OUT_WAIT = oldtimeout * 2;
									String temp0 = sendMessageToServer(
											ManagerSocketServer.JOIN_GAME,
											"," + GameSocketServer.ROOMNAMEKEY
													+ roomName);
									mainSocket.TIME_OUT_WAIT = oldtimeout;
									String temp1 = sendMessageToSomeSocket(
											GameSocketServer.LOGIN_USER, "",
											gameSocket);

									if (/*
										 * temp0 .contains(GameSocketServer.
										 * PLAYER_JOINED_ROOM) &&
										 */temp1
											.contains(GameSocketServer.LOGIN_SUCCESS))
										transitionToState(
												ModelStates.gameboard,
												parametersKeyValuePair);
									else {
										JOptionPane.showMessageDialog(
												currentWindow,
												"Cant join game!" + temp1
														+ temp0);
										currentWindow
												.setStatus("Failed to join room "
														+ roomName + ".");
									}
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
							String[] results = getGameParameters();
							transitionToState(ModelStates.newgame, results);
							String result = "";
							for (String s : results) {
								result += s + ",";
							}
							result = result.substring(0, result.length() - 1);
							String result2 = sendMessageToServer(
									ManagerSocketServer.CREATE_GAME, ","
											+ result);
							String result1 = sendMessageToSomeSocket(
									GameSocketServer.LOGIN_USER,
									","
											+ GameSocketServer.ROOMNAMEKEY
											+ state.getStateParameterValue("roomName"),
									gameSocket);
							if (result1
									.contains(GameSocketServer.LOGIN_SUCCESS)
									&& result2
											.contains(GameSocketServer.GAME_CREATED))
								transitionToState(ModelStates.gameboard, null);
							else
								JOptionPane.showMessageDialog(currentWindow,
										"Cant load game!" + result1 + result2);
						} else if (instruction.contains("windowClosed")) {// windowClosed
							transitionToState(ModelStates.lobby, null);
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
							// String result = "";
							// while (!result.contains("roomready=true")) {
							// result = sendMessageToSomeSocket(
							// GameSocketServer.IS_GAME_ROOM_READY,
							// "roomname="
							// + state.getStateParameterValue("roomName"),
							// gameSocket);
							// }
							currentWindow
									.setStatus("Waiting for other players...");
						} else if (instruction.contains(GAME_SOCKET)) {
							if (instruction
									.contains(GameSocketServer.SHOW_PLAYERS_HAND)) {
								String hand = instruction.split("cards=")[1]
										.split(GameSocketServer.END_MESSAGE_DELIMETER)[0]
										.split(",")[0];
								String[] cards = hand.split("/");
								for (int i = 1; i <= 3; i++) {
									String label = "boardGame_myCard" + i;
									state.setStateParameterValue(label,
											new ItalianDeckCard(cards[i - 1]));
								}

								for (int i = 1; i <= 3; i++) {
									String label = "boardGame_myCard" + i;
									// Add Animation
									ItalianDeckCard card = (ItalianDeckCard) state
											.getStateParameterValue(label);
									ImageIcon image = imageDB.getImage(card);
									if (image != null)
										gameboard.setImageIcon(label, image);
								}

								String seat = ((instruction
										.split(BriscaGameRoom.SEAT_KEY)[1])
										.split(GameSocketServer.END_MESSAGE_DELIMETER)[0])
										.split(",")[0];
								state.setStateParameterValue(
										BriscaGameRoom.SEAT_KEY
												.replace("=", ""), seat);
								String imageName = ((instruction
										.split(GameSocketServer.LIFECARD_KEY)[1])
										.split(GameSocketServer.END_MESSAGE_DELIMETER)[0])
										.split(",")[0];
								ImageIcon image = imageDB
										.getImage(new ItalianDeckCard(imageName));
								if (image != null)
									gameboard.setImageIcon("boardGame_life",
											image);
							} else if (instruction
									.contains(GameSocketServer.GAME_SCORE_IS)) {
								gameboard.fetchComponentAndAddValueJTextArea(
										null, "boardScore_display",
										instruction.split("\nscore=")[1]
												.split(",")[0], false);
							} else if (instruction
									.contains(BriscaGameRoom.PLAYER_HAS_JOINED)) {
								gameboard
										.fetchComponentAndAddValueJTextArea(
												null, "boardScore_display","\n"+
												instruction.split("@")[1]
														.split(",")[0], false);
							} else if (instruction
									.contains(GameSocketServer.MOVE_NEW_CARD)) {
								// TODO add card to hand
								String card = ((instruction
										.split(GameSocketServer.MOVE_NEW_CARD)[1])
										.split(GameSocketServer.END_MESSAGE_DELIMETER)[0])
										.split(",")[0];
								ItalianDeckCard italCard = new ItalianDeckCard(
										card);
								for (int i = 1; i <= 3; i++) {
									String label = "boardGame_myCard" + i;
									if (state.getStateParameterValue(label) == null) {
										state.setStateParameterValue(label,
												italCard);
										ImageIcon image = imageDB
												.getImage(italCard);
										if (image != null)
											gameboard
													.setImageIcon(label, image);
									}
								}
							} else if (instruction
									.contains(GameSocketServer.MOVE_SUCCESSFUL)) {
								// TODO Anther player has made a success full
								// player move
								String card = instruction
										.split(GameSocketServer.MOVE_SUCCESSFUL)[1]
										.split("@")[0];
								ItalianDeckCard italCard = new ItalianDeckCard(
										card);
								String parameters = instruction.split("@")[1]
										.split(GameSocketServer.END_MESSAGE_DELIMETER)[0];
								String seat = parameters.split("seat=")[1]
										.split(",")[0];
								String turn = parameters.split("turn=")[1]
										.split(",")[0];
								String name = parameters.split("name=")[1]
										.split(",")[0];
								String destination = "boardGame_player"
										+ (seat) + "Card";
								state.setStateParameterValue(destination,
										italCard);
								// Could animate
								ImageIcon image = imageDB.getImage(italCard);
								if (image != null)
									gameboard.setImageIcon(destination, image);
							} else if (instruction
									.contains(GameSocketServer.TURN_IS_OVER)) {
								// TODO clean up stack
								for (int i = 1; i <= 4; i++) {
									String label = "boardGame_player" + i
											+ "Card";
									state.setStateParameterValue(label, null);
									gameboard.setImageIcon(label, null);

								}
							}
						} else if (instruction.equals("options_surrender")) {
							// TODO Tell the server user has surrendered
							sendMessageToSomeSocket(
									GameSocketServer.PLAYER_SURRENDER, "",
									gameSocket);
							transitionToState(ModelStates.endgame, null);
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
							int cardValue = JOptionPane.showOptionDialog(
									currentWindow, "TradeCard",
									"Select a card to trade.",
									JOptionPane.DEFAULT_OPTION,
									JOptionPane.PLAIN_MESSAGE, null, options,
									options[0]);
							if (cardValue == 3) {
								continue;// Continue Loop
							}
							String result = sendMessageToSomeSocket(
									GameSocketServer.PLAYER_TRADEDCARD,
									"cardNumber=" + cardValue, gameSocket);
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
							String result = sendMessageToSomeSocket(
									GameSocketServer.MOVE_CARD,
									",move="
											+ GameSocketServer.MOVE_CARD
											+ state.getStateParameterValue(instruction
													.split("mouseClicked-")[1]),
									gameSocket);
							if (result
									.contains(GameSocketServer.MOVE_SUCCESSFUL)) {
								String target = instruction
										.split("mouseClicked-")[1];
								// get the seat assigned to the player by
								// server
								String seatString = (String) state
										.getStateParameterValue(BriscaGameRoom.SEAT_KEY
												.replace("=", ""));
								if (seatString == null) {
									seatString = "0";
								}
								int playerSeat = Integer.parseInt(seatString);
								String destination = "boardGame_player"
										+ (playerSeat) + "Card";
								// Remove the card from the state variables
								state.setStateParameterValue(target, null);
								/*
								 * Animate the action
								 */
								if (gameboard instanceof AnimatedJabriscaJPanel) {
									String animationName = (String) state
											.getStateParameterValue("playCardAnimationName");
									String animationParameters = (String) state
											.getStateParameterValue("playCardAnimationParameters");
									if (animationName == null) {
										animationName = "MoveCardAnimation";
									}
									if (animationParameters != null) {
										animationName += ":"
												+ animationParameters;
									}

									AnimatedJabriscaJPanel boardanimator = (AnimatedJabriscaJPanel) gameboard;
									// remove extras string and get the name of
									// the
									// card fix to sync
									boardanimator.animate(animationName,
											target, destination);
								}
							} else {
								JOptionPane.showMessageDialog(currentWindow,
										result);
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
							// TODO fetch game state
						} else if (instruction.contains("surrender")) {
							// TODO tell the server
							String result = sendMessageToSomeSocket(
									GameSocketServer.PLAYER_SURRENDER, "",
									gameSocket);
							if (result
									.contains(GameSocketServer.PLAYER_CANT_SURRENDER)) {
								// TODO handle player cant surrender
							}
							transitionToState(ModelStates.lobby, null);
						} else if (instruction
								.contains("windowClosed-EndGameWindow")) {
							// windowClosed
							transitionToState(ModelStates.lobby, null);
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
								transitionToState(ModelStates.lobby, null);
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
						if (mainSocket.isConnected()) {
							sendMessageToServer(ManagerSocketServer.END_CONNECTION);
							mainSocket.dispose();
						}
						if (gameSocket.isConnected()) {
							sendMessageToSomeSocket(
									GameSocketServer.END_CONNECTION, "",
									gameSocket);
							gameSocket.dispose();
						}
						if (chatSocket.isConnected()) {
							sendMessageToSomeSocket(
									ChatSocketServer.END_CONNECTION, "",
									chatSocket);
							chatSocket.dispose();
						}
						attempConnection();
					} else if (instruction.contains(WAIT_TIME_OUT)) {
						// TODO
					}
				} catch (Exception e) {
					String output = "Unexpected minor error, gracefully dealing with it in "
							+ getClass().getSimpleName()
							+ ".  Error: "
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

	private String[] getGameParameters() {
		// TODO Get parameters in key value pairs ready for next state
		String[] radioName = { "playInTeams", "blackHand", "swapCard",
				"surrender", "timed", "tournamentMode", };
		String[] key = { "teamGame=", "blackhand=", "cardSwap=", "surrender=",
				"timeLimit=", "tournament=" };
		String[] parameters = new String[radioName.length + 1];
		if (key.length == radioName.length) {
			for (int i = 0; i < radioName.length; i++) {
				boolean value = newgame.isCheckedIcon(radioName[i]);
				parameters[i] = key[i] + value;
			}
			// TODO
			parameters[radioName.length] = "roomName="
					+ newgame.fetchJTextValue("roomName");
		}
		return parameters;
	}

	/**
	 * 
	 */
	private void loadRooms() {
		// TODO Auto-generated method stub
		// rooms=room1{key1:value;key2:value...},room1{key1:value;key2:value...}
		lobby.setStatus("Loading Rooms");
		String[] results = new String[MAX_GAMES_TO_LOAD];
		String response = sendMessageToServer(GameSocketServer.GET_ALL_GAMES,
				",load=" + MAX_GAMES_TO_LOAD);
		// for (int i = 0; i < MAX_GAMES_TO_LOAD; i++) {
		// results[i] = sendMessageToServer(GameSocketServer.GET_GAME
		// + i);
		// currentGameFetched = results[i];
		// if (results[i].contains(GameSocketServer.COULD_LOAD_GAMES)) {
		// results[i] = results[i].split("@")[1];
		// } else
		// break;
		// }
		if (response.contains(GameSocketServer.GET_ALL_GAMES_SUCCESS)
				&& response.contains(ManagerSocketServer.END_LINE_DELIMETER)) {
			results = (response.split("@")[1]
					.split(ManagerSocketServer.END_MESSAGE_DELIMETER)[0])
					.split("\\" + ManagerSocketServer.END_LINE_DELIMETER);
			lobby.setJTableRow(results, "games_table");
			lobby.setStatus("Done");
		} else
			lobby.setStatus("Error loading Rooms." + response);
	}

	/**
	 * @param parameters
	 */
	private void updateLeaderboard(String parameters) {
		// TODO Auto-generated method stub
		String users = (parameters
				.split(ManagerSocketServer.END_MESSAGE_DELIMETER)[0]);
		users = users.replace(ManagerSocketServer.END_LINE_DELIMETER, "\n");
		lobby.fetchComponentAndAddValueJTextArea(null, "leaderBoards_display",
				users, true);
	}

	/**
	 * @param parameters
	 */
	private void updatePlayers(String parameters) {
		// TODO Auto-generated method stub
		String users = (parameters
				.split(ManagerSocketServer.END_MESSAGE_DELIMETER)[0]);
		users = users.replace(ManagerSocketServer.END_LINE_DELIMETER, "\n");
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

	/**
	 * @param username
	 * @param message
	 */
	private void updateChat_GameBoard(String username, String message) {
		gameboard.fetchComponentAndAddValueJTextArea(null, "lobbyChat_display",
				"\n" + username + ":" + message);
	}

	/**
	 */
	private void sendMessageToChat_Lobby() {
		// TODO Auto-generated meth od stub
		String message = lobby.fetchJTextValue("lobbyChat_message");
		String result = sendMessageToSomeSocket(ChatSocketServer.MESSAGE,
				",message=" + message, chatSocket);
		if (result.contains(ChatSocketServer.MESSAGE_RECEIVED)) {
			updateChat_Lobby("Me", message);
		}
	}

	/**
	 * @param currentPlayersPosition
	 * @param quantity
	 */
	private void loadLeaderboard(int currentPlayersPosition, int quantity) {
		// TODO Auto-generated method stub
		lobby.setStatus("Loading Leaderboards");
		this.currentLeaderPosition = quantity;
		String result = sendMessageToServer(GameSocketServer.GET_TOP_PLAYERS,
				",startAt=" + currentPlayersPosition + ",quantity=" + quantity);
		if (result.contains(GameSocketServer.GET_TOP_PLAYERS_SUCCESS)) {
			lobby.setStatus("Updating Leaderboards");
			String parameters = result.split("@")[1];
			updateLeaderboard(parameters);
			lobby.setStatus("Done");
		} else
			lobby.setStatus("Error loading leaderboards." + result);
	}

	/**
	 * @param currentPlayersPosition
	 * @param quantity
	 */
	private void loadPlayers(int currentPlayersPosition, int quantity) {
		// TODO Auto-generated method stub
		lobby.setStatus("Loading Players");
		this.currentPlayersPosition = quantity;
		String result = sendMessageToServer(
				GameSocketServer.GET_PLAYERS_ONLINE, ",startAt="
						+ currentPlayersPosition + ",quantity=" + quantity);
		if (result.contains(GameSocketServer.GET_PLAYERS_ONLINE_SUCCESS)) {
			lobby.setStatus("Updating Players");
			String parameters = result.split("@")[1];
			updatePlayers(parameters);
			lobby.setStatus("Done");
		} else
			lobby.setStatus("Error loading players." + result);
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
		extraParameters = extraParameters == null ? "" : extraParameters;
		String result = null;
		try {
			result = socket.sendMessageWaitResponse(message + "@" + "username="
					+ state.getStateParameterValue("username") + ","
					+ "password=" + state.getStateParameterValue("password")
					+ extraParameters);
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
	private void transitionToState(ModelStates nextState,
			String[] parametersKeyValuePair) throws IllegalStateException {
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
			case loginsingup:
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

		if (parametersKeyValuePair != null) {
			for (String keyvalue : parametersKeyValuePair) {
				for (String key : state.getParameterKeys()) {
					if (keyvalue.contains(key + "=")) {
						state.setStateParameterValue(key,
								keyvalue.split("=")[1]);
					}
				}
			}
		}
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

	/**
	 * You deserver better then I must be better
	 */
	private void attempConnection() {

		int i = 0;
		boolean connecting = true;
		if (!(currentWindow == null))
			currentWindow.setStatus("Attempting to connect...");
		while (connecting) {
			try {
				setupSocketConnection();
				connecting = !(mainSocket.isConnected()
						&& chatSocket.isConnected() && gameSocket.isConnected());
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
			if (mainSocket.isConnected() && chatSocket.isConnected()
					&& gameSocket.isConnected())
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
		gameSocket = new SocketClient(hostURL, gamePort, instructions,
				GAME_SOCKET);
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

}
