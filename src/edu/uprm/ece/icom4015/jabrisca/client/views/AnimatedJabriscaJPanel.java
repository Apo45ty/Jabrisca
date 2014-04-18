package edu.uprm.ece.icom4015.jabrisca.client.views;

public interface AnimatedJabriscaJPanel {
	
	/**
	 * This method allows all calling the gameboard to animate any object via a series of predefined strings each associated to specific animation
	 * @param animation
	 * @param target
	 * @param destination
	 * @return true if the animation was played
	 */
	boolean animate(String animation,String target, String destination);
	
	/**
	 * This method allows all calling the gameboard to animate any object via a series of predefined strings each associated to specific animation
	 * @param animation
	 * @param target
	 * @param destination
	 * @return true if the animation was played
	 */
	boolean animateAsync(String animation,String target, String destination);
	
	/**
	 * Check if the animation exists
	 * @param animation
	 * @return true if the gameboard has the desired animation
	 */
	boolean hasAnimation(String animation);
	
	/**
	 * Check if such the gameboard hasthe target
	 * @param target
	 * @return true if the gameboard could associate one of its elements with this given target
	 */
	boolean hasTarget(String target);
	
	/**
	 * Check if the target has the specified animation
	 * @param target
	 * @return true if the gameboard could associate one of its elements with this given target
	 */
	boolean targetHasAnimation(String target,String Animation);
	
	/**
	 *  verify if the destination exists
	 * @param destination
	 * @return
	 */
	boolean hasDestination(String target);
}
