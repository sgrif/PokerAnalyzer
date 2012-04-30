package com.sgrif.poker;

public class Deck {
	public static final int DECK_SIZE	= 52;
	
	public static final int[] PRIMES	= {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41};
										// 2, 3, 4, 5, 66, 77, 88, 99, TT, JJ, QQ, KK, AA
										// d, h, c, s
	private static final int[] deck		= getDeck(); 
	/**
	 * The deck is simply an array of integers with length 52. Integers are created from 4 bytes.
	 * First two bytes are bit flags for card value. Second byte has 4 bits for suit, 4 bits
	 * for numeric rank. Final byte stores associated prime.
	 * 
	 * xxxAKQJT | 98765432 | shcdVVVV | xxPPPPPP
	 * 
	 * Using this format, cards can be quickly compared based on bitwise comparisons.
	 */
	public static int[] getDeck() {
		if(deck != null) return deck;
		int[] cards = new int[52];
		int counter = 0, suit = 0x1000;
		
		for (int i=0; i<4; i++, suit<<=1) { // Enumerate through suits
			for(int x=0; x < 13; x++, counter++) { // Enumerate through values
				cards[counter] = (1 << (16 + x) | suit | (x << 8) | PRIMES[x]); // Use bitwise OR to combine values
			}
		}
		
		return cards;
	}

}
