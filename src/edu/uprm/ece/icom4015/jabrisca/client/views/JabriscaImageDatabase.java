package edu.uprm.ece.icom4015.jabrisca.client.views;

import javax.swing.ImageIcon;

import edu.uprm.ece.icom4015.jabrisca.server.game.brica.ItalianDeckCard;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.ItalianDeckCard.ItalianDeckRank;

public class JabriscaImageDatabase implements ImageDatabase {

	public ImageIcon getImage(Object key) {
		if(key instanceof ItalianDeckCard)
		{
			ItalianDeckCard card = ((ItalianDeckCard) key);
			String iconName = "";
			iconName = card.getSuit().toString();
			iconName += "_" +(
			card.getRank().getCardNumber()>=2 ||card.getRank().getCardNumber()<=7 ?card.getRank().getCardNumber() :
			card.getRank() == ItalianDeckRank.asso?"ace":
			card.getRank() == ItalianDeckRank.fade?"jack":
			card.getRank() == ItalianDeckRank.re?"king":
			card.getRank() == ItalianDeckRank.fade?"knight":
				"ERROR");
			iconName += ".png";
			return new ImageIcon(getClass().getResource("/Images/Deck/"+iconName));
		}
		return null;
	}

}
