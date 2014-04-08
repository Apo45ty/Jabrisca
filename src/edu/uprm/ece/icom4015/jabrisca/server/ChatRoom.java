/**
 * 
 */
package edu.uprm.ece.icom4015.jabrisca.server;

/**
 * @author EltonJohn
 *
 */
public class ChatRoom {
	private User[] users;
	
	/**
	 * @param userCount
	 */
	private ChatRoom(int userCount) {
		users= new User[userCount];
		// TODO Setup other stuff 
	}
	
	/**
	 * Only one ChatRoom may be created at a time
	 * @param userCount
	 * @return
	 */
	public static synchronized ChatRoom getInstance(int userCount){
		return new ChatRoom(userCount);
	}
	
	/**
	 * Send a message to all the chat users
	 * @param message
	 * @param user
	 */
	public synchronized void broadCast(String message,User user){
		for(User client:users){
			if(client != user)
				client.getChatSocket().sendMessage(message);
		}
	}
}
