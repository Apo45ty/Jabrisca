package edu.uprm.ece.icom4015.jabrisca.server.game.brica;

import edu.uprm.ece.icom4015.jabrisca.server.game.brica.ItalianDeckCard.ItalianDeckSuit;

public class BriscaCardComparator {
	/**
	 * 
	 * @param card1
	 * @param card2
	 * @param suit the life of the game
	 * @return -1 if card1 is smaller than card2
	 */
	public static int compare(ItalianDeckCard card1,ItalianDeckCard card2, ItalianDeckSuit suit){
		if(card1.getSuit().toString().equals(suit.toString())&&!card2.getSuit().toString().equals(suit.toString())){
			//TODO The player with life takes the card
			return 1;
		} else if(card2.getSuit().toString().equals(suit.toString())&&!card1.getSuit().toString().equals(suit.toString())){
			//TODO The player with life takes the card
			return -1;
		} //else
		return card1.getRank().getCardNumber() - card2.getRank().getCardNumber(); 
	}
}
