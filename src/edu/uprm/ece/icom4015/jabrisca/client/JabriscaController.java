package edu.uprm.ece.icom4015.jabrisca.client;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.JTable;

public class JabriscaController implements ActionListener, KeyListener,
		MouseListener, WindowListener {
	// Use to feed the consumer, in our case the model.
	BlockingQueue queue;

	/**
	 * Set the Blocking Queue we will populate
	 * 
	 * @param queue
	 */
	public JabriscaController(BlockingQueue queue) {
		setQueue(queue);
	}

	/**
	 * Construct an Empty controller
	 */
	public JabriscaController() {
	}

	/**
	 * Set the Blocking Queue we will populate, we synchronized the thread so
	 * that we dontget replica queues
	 * 
	 * @param queue
	 */
	public synchronized void setQueue(BlockingQueue queue) {
		this.queue = queue;
	}

	/**
	 * 
	 * @param command
	 *            to enqueue
	 */
	private void enqueueCommands(String command) {
		// System.out.println(command);
		queue.offer(command);// use default timeout for the offering
	}

	/**
	 * Generate command containing the origin of the event along with basic
	 * event description as a command to the event
	 */
	public void mouseClicked(MouseEvent arg0) {
		String command = "mouseClicked-";
		// Because I did not want to read the JTableSpecs I have found a
		// workaround JTables limits
		if (arg0.getSource() instanceof JTable) {
			// Cast to JTable
			JTable target = (JTable) arg0.getSource();
			int row = target.getSelectedRow();
			// Check if we can fetch the room name
			String parameters = "";
			for(int i=0;i<target.getColumnCount();i++){
				String colValue = (String) target.getModel()
						.getValueAt(row, i);
				colValue = colValue == null ? "null" : colValue;
				
				String colKey = target.getColumnName(i);
				//Add key=value to the parameters string
				parameters += colKey+"="+colValue;
				
				//Add trailing coma
				if(i<target.getColumnCount()-1){
					parameters+=",";
				}
			}
			//if column has at least one column this will evaluate to true because this means loop ran at-least once
			if(parameters.contains("="))
				parameters=":"+parameters;
			command += arg0.getComponent().getName() + parameters;
		} else {
			command += arg0.getComponent().getName();
		}
		enqueueCommands(command);
		// System.out.println(arg0.getComponent().getLocationOnScreen());
	}

	/**
	 * Generate command containing the origin of the event along with basic
	 * event description as a command to the event
	 */
	public void mouseEntered(MouseEvent arg0) {
		String command = "mouseEnter-" + arg0.getComponent().getName();
		enqueueCommands(command);
	}

	/**
	 * Generate command containing the origin of the event along with basic
	 * event description as a command to the event
	 */
	public void mouseExited(MouseEvent arg0) {
		String command = "mouseExited-" + arg0.getComponent().getName();
		enqueueCommands(command);
	}

	/**
	 * Generate command containing the origin of the event along with basic
	 * event description as a command to the event
	 */
	public void mousePressed(MouseEvent arg0) {
		String command = "mousePressed-" + arg0.getComponent().getName();
		enqueueCommands(command);
	}

	/**
	 * Generate command containing the origin of the event along with basic
	 * event description as a command to the event
	 */
	public void mouseReleased(MouseEvent arg0) {
		String command = "mouseReleased-" + arg0.getComponent().getName();
		enqueueCommands(command);
	}

	/**
	 * Generate command containing the origin of the event along with basic
	 * event description as a command to the event
	 */
	public void keyPressed(KeyEvent arg0) {
		String command = "keyPressed-" + arg0.getComponent().getName() + "-"
				+ arg0.getKeyChar();
		enqueueCommands(command);
	}

	/**
	 * Generate command containing the origin of the event along with basic
	 * event description as a command to the event
	 */
	public void keyReleased(KeyEvent arg0) {
		String command = "keyReleased-" + arg0.getComponent().getName() + "-"
				+ arg0.getKeyChar();
		enqueueCommands(command);
	}

	/**
	 * Generate command containing the origin of the event along with basic
	 * event description as a command to the event
	 */
	public void keyTyped(KeyEvent arg0) {
		String command = "keyTyped-" + arg0.getComponent().getName() + "-"
				+ arg0.getKeyChar();
		enqueueCommands(command);
	}

	/**
	 * Generate command containing the origin of the event along with basic
	 * event description as a command to the event
	 */
	public void actionPerformed(ActionEvent arg0) {
		String command = ((Component) arg0.getSource()).getName();
		enqueueCommands(command);
	}

	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowClosing(WindowEvent arg0) {
		String command = "windowClosed-"
				+ arg0.getSource().getClass().getSimpleName();
		enqueueCommands(command);
	}

	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowOpened(WindowEvent arg0) {
		String command = "windowOpened-"
				+ arg0.getSource().getClass().getSimpleName();
		enqueueCommands(command);
	}

	// Study BlockingQueue and Enums
	/*
	 * GameBoard-names: boardGame_player1Card, JLabel boardGame_player2Card,
	 * JLabel boardGame_player3Card, JLabel boardGame_player4Card, JLabel
	 * boardGame_myCard1, JLabel boardGame_myCard2, JLabel boardGame_myCard3,
	 * JLabel boardGame_life, JLabel boardGame_deck, JLabel statusBar_status,
	 * JLabel statusBar_progressBar, JProgressBar boardScore_display, JTextArea
	 * boardChat_display, JTextArea boardChat_message, JTextArea
	 * boardChat_sendButton, JButton options_surrender, JMenuItem
	 * options_tradeCard, JMenuItem options_blackHand, JMenuItem howToPlay,
	 * JMenuItem
	 */

	/*
	 * NewGame-names: name, JTextField statusBar_status, JLabel
	 * statusBar_progressBar, JProgressBar playInTeams, JRadioButton
	 * playInTeams, JRadioButton blackHand, JRadioButton swapCard, JRadioButton
	 * timed, JRadioButton tournamentMode, JRadioButton createRoom, JButton
	 * howToPlay, JMenuItem
	 */

	/*
	 * Lobby-names: statusBar_status, JLabel statusBar_progressBar, JProgressBar
	 * players_display, JTextArea players_load, JButton leaderBoards_display,
	 * JTextArea leaderBoards_load, JButton lobbyChat_display, JTextArea
	 * lobbyChat_message, JTextArea lobbyChat_send, JButton games_create,
	 * JButton games_table, JTable options_logout, JMenuItem options_myScores,
	 * JMenuItem howToPlay, JMenuItem games_table, JTable
	 */

	/*
	 * LoginSingUp-names: 
	 * statusBar_status, JLabel 
	 * statusBar_progressBar,JProgressBar 
	 * jabrisca, JLabel 
	 * login, JButton 
	 * singup, JButton 
	 * howToPlay, JMenuItem 
	 * username, JTextField 
	 * password, JTextField
	 */

	/*
	 * EndGameWindow-names: statusBar_status, JLabel statusBar_progressBar,
	 * JProgressBar display, JButton continue, JButton surrender, JButton
	 * howToPlay, JMenuItem
	 */
}
