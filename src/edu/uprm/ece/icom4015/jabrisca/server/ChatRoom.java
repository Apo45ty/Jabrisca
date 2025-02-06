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
	 * 
	 * @return
	 */
	public boolean addUser(User user){
		return ManagerSocketServer.allowcateUser(user, users);
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
			if(client==null)continue;
			if(client != user)
				client.getChatSocket().sendMessage(message);
		}
	}
	public boolean removeUser(User user) {
		for(int i=0;i<users.length;i++){
			if(users[i]==user){
				users[i] = null;
				return true;
			}
		}
		return false;
	}
	public User[] getUsers() {
		return users;
	}
}
