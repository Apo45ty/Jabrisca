package edu.uprm.ece.icom4015.jabrisca.server.game.brica;

public class ItalianDeckCard {
	ItalianDeckSuit suit;
	ItalianDeckRank rank;
	
	
	public ItalianDeckCard(String string) {
		super();
		for(ItalianDeckSuit suit :ItalianDeckSuit.values())begin:{
			if(string.contains(suit.toString())){
				String myrank = string.split(suit.toString())[1];
				for(ItalianDeckRank rank:ItalianDeckRank.values()){
					if(myrank.equals(rank.getCardNumber()+"")){
						this.rank = rank;
						this.suit = suit;
						break begin;
					}
				}
			}
		}
		if(rank==null||suit==null){
			throw new IllegalArgumentException("Malformed string parameter.");
		}
	}
	
	public ItalianDeckCard(ItalianDeckSuit suit, ItalianDeckRank rank) {
		super();
		this.suit = suit;
		this.rank = rank;
	}

	/**
	 * @return the suit
	 */
	public synchronized ItalianDeckSuit getSuit() {
		return suit;
	}

	/**
	 * @return the rank
	 */
	public synchronized ItalianDeckRank getRank() {
		return rank;
	}

	@Override
	public String toString() {
		return ""+suit+rank;
	}
	
	@Override
	public boolean equals(Object arg) {
		ItalianDeckCard card2 = (ItalianDeckCard) arg;
		return this.rank.toString().equals(card2.getRank().toString()) && this.suit.toString().equals(card2.getSuit().toString());
	}
	
	/**
	 * @author EltonJohn
	 */
	public enum ItalianDeckSuit{
		bastion("bastion"),
		cup("cup"),
		coin("coin"),
		sword("sword");
		
		String suitName;
		/**
		 * @param name
		 */
		private ItalianDeckSuit(String name) {
			this.suitName = name;
		}
		
		@Override
		public String toString(){
			return this.suitName;
		}
		
		/**
		 * @return the suitName
		 */
		public synchronized String getSuitName() {
			return suitName;
		}
		
	}
	
	public enum ItalianDeckRank{
			asso(11,14),
			two(0,2),
			tre(10,13),
			four(0,4),
			five(0,5),
			six(0,6),
			seven(0,7),
			fade(2,10),
			cavallo(3,11),
			re(4,12);
		
		private int value = 0;
		private int cardNumber;
		
		/**
		 * @param value
		 * @param number
		 */
		private ItalianDeckRank(int value,int number) {
			this.value  = value;
			this.cardNumber = number;
		}
		/**
		 * @return the value
		 */
		public synchronized int getValue() {
			return value;
		}
		
		/**
		 * @return the cardNumber
		 */
		public synchronized int getCardNumber() {
			return cardNumber;
		}
		@Override
		public String toString() {
			return this.cardNumber+"";
		}
	}
}
