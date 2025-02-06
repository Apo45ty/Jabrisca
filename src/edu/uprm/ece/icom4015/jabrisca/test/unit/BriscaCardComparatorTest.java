package edu.uprm.ece.icom4015.jabrisca.test.unit;

import edu.uprm.ece.icom4015.jabrisca.server.game.brica.BriscaCardComparator;
import edu.uprm.ece.icom4015.jabrisca.server.game.brica.ItalianDeckCard;
import org.junit.jupiter.api.Test;

public class BriscaCardComparatorTest {

    @Test
    public void test_compare_HappyPathCardOneIsSuit(){
        //Prepare
        ItalianDeckCard cardOne = new ItalianDeckCard(ItalianDeckCard.ItalianDeckSuit.cup,
                ItalianDeckCard.ItalianDeckRank.five);
        ItalianDeckCard cardTwo = new ItalianDeckCard(ItalianDeckCard.ItalianDeckSuit.bastion,
                ItalianDeckCard.ItalianDeckRank.five);
        ItalianDeckCard.ItalianDeckSuit suit = ItalianDeckCard.ItalianDeckSuit.cup;

        //Execute
        int result = BriscaCardComparator.compare(cardOne,cardTwo,suit);
        assert (result==1);
    }
    @Test
    public void test_compare_HappyPathCardTwoIsSuit(){
        //Prepare
        ItalianDeckCard cardOne = new ItalianDeckCard(ItalianDeckCard.ItalianDeckSuit.cup,
                ItalianDeckCard.ItalianDeckRank.five);
        ItalianDeckCard cardTwo = new ItalianDeckCard(ItalianDeckCard.ItalianDeckSuit.bastion,
                ItalianDeckCard.ItalianDeckRank.five);
        ItalianDeckCard.ItalianDeckSuit suit = ItalianDeckCard.ItalianDeckSuit.bastion;

        //Execute
        int result = BriscaCardComparator.compare(cardOne,cardTwo,suit);
        assert (result==-1);
    }
    @Test
    public void test_compare_HappyPathNoCardIsSuitRankIsMatch(){
        //Prepare
        ItalianDeckCard cardOne = new ItalianDeckCard(ItalianDeckCard.ItalianDeckSuit.cup,
                ItalianDeckCard.ItalianDeckRank.five);
        ItalianDeckCard cardTwo = new ItalianDeckCard(ItalianDeckCard.ItalianDeckSuit.cup,
                ItalianDeckCard.ItalianDeckRank.five);
        ItalianDeckCard.ItalianDeckSuit suit = ItalianDeckCard.ItalianDeckSuit.bastion;

        //Execute
        int result = BriscaCardComparator.compare(cardOne,cardTwo,suit);
        assert (result==0);
    }
    @Test
    public void test_compare_HappyPathNoCardIsSuitRankIsNotMatch(){
        //Prepare
        ItalianDeckCard cardOne = new ItalianDeckCard(ItalianDeckCard.ItalianDeckSuit.cup,
                ItalianDeckCard.ItalianDeckRank.five);
        ItalianDeckCard cardTwo = new ItalianDeckCard(ItalianDeckCard.ItalianDeckSuit.cup,
                ItalianDeckCard.ItalianDeckRank.tre);
        ItalianDeckCard.ItalianDeckSuit suit = ItalianDeckCard.ItalianDeckSuit.bastion;

        //Execute
        int result = BriscaCardComparator.compare(cardOne,cardTwo,suit);
        assert (result==-8);
    }
    @Test
    public void test_compare_HappyPathBothCardsIsSuitRankIsMatch(){
        //Prepare
        ItalianDeckCard cardOne = new ItalianDeckCard(ItalianDeckCard.ItalianDeckSuit.cup,
                ItalianDeckCard.ItalianDeckRank.five);
        ItalianDeckCard cardTwo = new ItalianDeckCard(ItalianDeckCard.ItalianDeckSuit.cup,
                ItalianDeckCard.ItalianDeckRank.five);
        ItalianDeckCard.ItalianDeckSuit suit = ItalianDeckCard.ItalianDeckSuit.cup;

        //Execute
        int result = BriscaCardComparator.compare(cardOne,cardTwo,suit);
        assert (result==0);
    }
}
