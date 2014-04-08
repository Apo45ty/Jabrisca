package edu.uprm.ece.icom4015.jabrisca.client;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JOptionPane;

import edu.uprm.ece.icom4015.jabrisca.client.views.EndGameWindow;
import edu.uprm.ece.icom4015.jabrisca.client.views.GameBoard;
import edu.uprm.ece.icom4015.jabrisca.client.views.JabriscaJPanel;
import edu.uprm.ece.icom4015.jabrisca.client.views.Lobby;
import edu.uprm.ece.icom4015.jabrisca.client.views.LoginSingUp;
import edu.uprm.ece.icom4015.jabrisca.client.views.NewGame;
import edu.uprm.ece.icom4015.jabrisca.server.ManagerSocketServer;

public class JabriscaClientBuilder {
	private static boolean changeLookAndFeel = false;
	//default look and feel
	private static String lookAndFeel = "Nimbus";
	private static boolean startupServer = false;
	JabriscaJPanel lobby;
	JabriscaJPanel loginsingup;
	JabriscaJPanel newgame;
	JabriscaJPanel gameboard;
	JabriscaJPanel endgamewindow;
	BlockingQueue instructions;
	JabriscaController controller;
	JabriscaModel model;

	public JabriscaClientBuilder(JabriscaJPanel lobby,
			JabriscaJPanel loginsingup, JabriscaJPanel newgame,
			JabriscaJPanel gameboard, JabriscaJPanel endgamewindow,
			BlockingQueue instructions, JabriscaController controller,
			JabriscaModel model) {
		super();
		this.lobby = lobby;
		this.loginsingup = loginsingup;
		this.newgame = newgame;
		this.gameboard = gameboard;
		this.endgamewindow = endgamewindow;
		this.instructions = instructions;
		this.controller = controller;
		this.model = model;
		
		//Check if panels have listener if not set
		//Check if controller has blockqueue if not set
		//Check if model has blockqueue if not set
		//Check if model has Jabriscapanels if not set
	}

	public void start() {
		model.start();
	}

	public static void main(String[] args) {
		changeLookAndFeel = changeLookAndFeel || args.length > 1 ;
		//Remember to se it so we can customize the look and feel on the fly 
		
		//Set the look and feel for the program
		if(changeLookAndFeel){
			//Just in case you are running from console... was of time I know
			if(args.length<2){
				String input= JOptionPane.showInputDialog("Please Enter The Name Of the New Look and feel");
				System.out.println(input);
				if(input != null && input.length() >= 0){
					lookAndFeel = input;
				}
			} else {
				//Otherwise set the look and feel to the second parameter
				lookAndFeel = args[1];
			}
			setLookAndFeel(lookAndFeel);
		}
		
		if(startupServer)
			try {
				ManagerSocketServer server = ManagerSocketServer.getServerSingleton();
				server.start(null);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		
		BlockingQueue queue = new ArrayBlockingQueue(100);
		
		JabriscaController controller =  new JabriscaController(queue);
		JabriscaJPanel lobby = new Lobby(controller);
		JabriscaJPanel loginsingup = new LoginSingUp(controller);
		JabriscaJPanel newgame = new NewGame(controller);
		JabriscaJPanel gameboard = new GameBoard(controller);
		JabriscaJPanel endgamewindow = new EndGameWindow(controller);
		JabriscaModel model = new JabriscaModel(lobby, loginsingup, newgame, gameboard, endgamewindow, queue);
		
		JabriscaClientBuilder builder = new JabriscaClientBuilder(lobby, loginsingup, newgame, gameboard, endgamewindow, queue, controller, model);
		builder.start();
	}

	private static void setLookAndFeel(String lookAndFeel) {
		try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (lookAndFeel.equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Lobby.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Lobby.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Lobby.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Lobby.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
	}

}
