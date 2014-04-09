/**
 * 
 */
package edu.uprm.ece.icom4015.jabrisca.server.brica;


/**
 * @author EltonJohn
 *
 */
public class BriscaGameRoom implements GameRoom {
	private Player[] players;
	
	private BriscaGameRoom(int roomSize){
		//TODO
		this.players = new Player[roomSize];
	}

	/* (non-Javadoc)
	 * @see edu.uprm.ece.icom4015.jabrisca.server.brica.GameRoom#getNumberOfPlayers()
	 */
	public synchronized int getNumberOfPlayers() {
		// TODO Auto-generated method stub
		return players.length;
	}

	/* (non-Javadoc)
	 * @see edu.uprm.ece.icom4015.jabrisca.server.brica.GameRoom#addPlayer(edu.uprm.ece.icom4015.jabrisca.server.brica.Player)
	 */
	public synchronized int addPlayer(Player player) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see edu.uprm.ece.icom4015.jabrisca.server.brica.GameRoom#getSize()
	 */
	public synchronized int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see edu.uprm.ece.icom4015.jabrisca.server.brica.GameRoom#isPlaying()
	 */
	public synchronized boolean isPlaying() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.uprm.ece.icom4015.jabrisca.server.brica.GameRoom#getPlayers()
	 */
	public synchronized Player[] getPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.uprm.ece.icom4015.jabrisca.server.brica.GameRoom#getInstance(int)
	 */
	public synchronized static GameRoom getInstance(int roomSize) {
		// TODO Auto-generated method stub
		return new BriscaGameRoom(roomSize);
	}
	/**
	 * 
	 */
	public boolean playerMadeMove(Player player, Object[] parameters) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 
	 */
	public boolean setParameters(String[] KeyValuePairs) {
		// TODO Auto-generated method stub
		return false;
	}

}
