package com.sgrif.poker;

public class Card implements Comparable<Card> {
	private String suit;
	private Integer value;
	private String[] names = {"Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King", "Ace"};
	private static boolean low;
	
	public Card(String v, String s) {
		suit = s;
		value = valueFromString(v);
	}

	public Integer getValue() {
		if(low == true && value == 14) return 1;
		return value;
	}
	
	public String getName() {
		return names[value - 2];
	}
	
	public String toString() {
		String s = value.toString();
		if(s.equals("10")) s = "T";
		if(s.equals("11")) s = "J";
		if(s.equals("12")) s = "Q";
		if(s.equals("13")) s = "K";
		if(s.equals("14")) s = "A";
		return s + suit;
	}
	
	public static void setLow(boolean b) {
		low = b;
	}
	
	private Integer valueFromString(String s) {
		if(s.equals("T")) s = "10";
		if(s.equals("J")) s = "11";
		if(s.equals("Q")) s = "12";
		if(s.equals("K")) s = "13";
		if(s.equals("A")) s = "14";
		return Integer.parseInt(s);
	}

	@Override
	public int compareTo(Card o) {
		return o.getValue() - getValue();
	}
	
	public boolean sameSuit(Card o) {
		return o.suit.equals(suit);
	}
}
