package com.sgrif.poker;

import java.util.*;

public class Hand implements Comparable<Hand> {
	private ArrayList<Card> cards = new ArrayList<Card>();
	private Integer ranking_value = 0;
	private String description = "";
	private String name = "";
	
	public Hand(String n) {
		name = n;
	}
	
	public void reset() {
		cards.clear();
		ranking_value = 0;
		description = "";
	}
	
	public void addCard(Card c) {
		cards.add(c);
	}
	
	public void discardCard(String s) {
		Card r = null;
		for(Card c : cards) {
			if(c.toString().equals(s)) r = c;
		}
		if(r != null) cards.remove(r);
	}
	
	/*
	public ArrayList<Card> getCards() {
		ArrayList<Card> c = new ArrayList<Card>();
		c.addAll(cards);
		return c;
	}*/
	
	public Integer size() {
		return cards.size();
	}
	
	public String toString() {
		String s = "";
		s += name + ": " + description + " - ";
		for(Card card : cards) {
			s += card.toString();
		}
		return s;
	}
	
	public Integer getValue() {
		return ranking_value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Integer determineValue(ArrayList<Card> board) {
		ranking_value = 0;
		description = "";
		ArrayList<Card> working = new ArrayList<Card>();
		working.addAll(cards);
		working.addAll(board);
		Collections.sort(working);
		Card[] c = new Card[5];
		BitSet b = new BitSet();
		enumerateHands(c, working, 0, b);
		return ranking_value;
	}
	
	private void enumerateHands(Card[] hand, ArrayList<Card> working, int from, BitSet cards_used) {
		if (from == 5) {
			int x = getValue(hand);
			if(x > ranking_value) {
				ranking_value = x;
				description = determineDescription(hand);
			}
		} else {
			for (int i = 0; i < working.size(); i++) {
				if (!cards_used.get(i)) {
					hand[from] = working.get(i);
					cards_used.set(i);
					enumerateHands(hand, working, from + 1, cards_used);
					cards_used.clear(i);
				}
			}
		}
	}
	
	private Integer getValue(Card[] hand) {
		Card[] sorted = hand.clone();
		Arrays.sort(sorted);
		Card c = null;
		Card[] ca = null;
		
		if((c = hasRoyalFlush(sorted)) != null) {
			return parseHexValue(c, sorted, "a");
		} else if((c = hasFiveOfAKind(sorted)) != null) {
			return parseHexValue(c, sorted, "9");
		} else if((c = hasStraightFlush(sorted)) != null) {
			return parseHexValue(c, sorted, "8");
		} else if((c = hasFourOfAKind(sorted)) != null) {
			return parseHexValue(c, sorted, "7");
		} else if((ca = hasFullHouse(sorted)) != null) {
			return parseHexValue(ca, sorted, "6");
		} else if((c = hasFlush(sorted)) != null) {
			return parseHexValue(c, sorted, "5");
		} else if((c = hasStraight(sorted)) != null) {
			return parseHexValue(c, sorted, "4");
		} else if((c = hasThreeOfAKind(sorted)) != null) {
			return parseHexValue(c, sorted, "3");
		} else if((ca = hasTwoPair(sorted)) != null) {
			return parseHexValue(ca, sorted, "2");
		} else if((c = hasPair(sorted)) != null) {
			return parseHexValue(c, sorted, "1");
		} else {
			return parseHexValue(sorted, "0");
		}
	}
	
	private Integer parseHexValue(Card[] sorted, String multiplier) {
		String hex = ""; 
		hex += multiplier;
		hex += getHexCards(sorted);
		return Integer.parseInt(hex, 16);
	}
	
	private Integer parseHexValue(Card c, Card[] sorted, String multiplier) {
		String hex = ""; 
		hex += multiplier;
		hex += Integer.toHexString(c.getValue());
		hex += getHexCards(sorted);
		return Integer.parseInt(hex, 16);
	}
	
	private Integer parseHexValue(Card[] ca, Card[] sorted, String multiplier) {
		String hex = ""; 
		hex += multiplier;
		for(Card card : ca) hex += Integer.toHexString(card.getValue());
		hex += getHexCards(sorted);
		return Integer.parseInt(hex.substring(0, 7), 16);
	}
	
	private Card hasFiveOfAKind(Card[] sorted) {
		for(int x=0; x < sorted.length; x++) {
			if(sorted[x].compareTo(sorted[0]) != 0) return null;
		}
		return sorted[0];
	}
	
	private Card hasRoyalFlush(Card[] sorted) {
		Card c;
		if((c = hasStraightFlush(sorted)) != null && c.getValue() == 14) return c;
		return null;
	}
	
	private Card hasStraightFlush(Card[] sorted) {
		Card c;
		if((c = hasStraight(sorted)) != null && hasFlush(sorted) != null) return c;
		return null;
	}
	
	private Card hasFourOfAKind(Card[] sorted) {
		for(int x=0; x < sorted.length-3; x++) {
			if(sorted[x].compareTo(sorted[x+1]) == 0 && sorted[x].compareTo(sorted[x+2]) == 0 && sorted[x].compareTo(sorted[x+3]) == 0) return sorted[x];
		}
		return null;
	}
	
	private Card[] hasFullHouse(Card[] sorted) {
		Card[] c = new Card[2];
		if((c[0] = hasThreeOfAKind(sorted)) != null && (c[1] = hasPair(sorted, c[0])) != null) {
			return c;
		} else {
			return null;
		}
	}
	
	private Card hasFlush(Card[] sorted) {
		for(int x=0; x < 4; x++) {
			if(!sorted[x].sameSuit(sorted[x+1])) return null;
		}
		return sorted[0];
	}
	
	private Card hasStraight(Card[] sorted) {
		if(sorted[4].getValue() == 2) {
			Card.setLow(true);
			Arrays.sort(sorted);
		}
		for(int x=0; x < 4; x++) {
			if(sorted[x].compareTo(sorted[x+1]) != -1) {
				Card.setLow(false);
				Arrays.sort(sorted);
				return null;
			}
		}
		Card.setLow(false);
		return sorted[0];
	}
	
	private Card hasThreeOfAKind(Card[] sorted) {
		for(int x=0; x < sorted.length-2; x++) {
			if(sorted[x].compareTo(sorted[x+1]) == 0 && sorted[x].compareTo(sorted[x+2]) == 0) return sorted[x];
		}
		return null;
	}
	
	private Card[] hasTwoPair(Card[] sorted) {
		Card[] c = new Card[2];
		if((c[0] = hasPair(sorted)) != null) {
			if((c[1] = hasPair(sorted, c[0])) != null) return c;
		}
		return null;
	}
	
	private Card hasPair(Card[] sorted) {
		for(int x=0; x < sorted.length-1; x++) {
			if(sorted[x].compareTo(sorted[x+1]) == 0) return sorted[x];
		}
		return null;
	}
	
	private Card hasPair(Card[] sorted, Card exclude) {
		for(int x=0; x < sorted.length-1; x++) {
			if(sorted[x].compareTo(exclude) != 0 && sorted[x].compareTo(sorted[x+1]) == 0) {
				return sorted[x];
			}
		}
		return null;
	}
	
	private String getHexCards(Card[] sorted) {
		String hex = "";
		for(Card card : sorted) {
			hex += Integer.toHexString(card.getValue());
		}
		return hex;
	}
	
	private String determineDescription(Card[] hand) {
		Card[] sorted = hand.clone();
		Arrays.sort(sorted);
		Card c;
		Card[] cs;
		
		if(hasRoyalFlush(sorted) != null) return "Royal Flush";
		if((c = hasFiveOfAKind(sorted)) != null) return "Five " + c.getName() + "s";
		if((c = hasStraightFlush(sorted)) != null) return c.getName() + " high straight-flush";
		if((c = hasFourOfAKind(sorted)) != null) return "Four " + c.getName() + "s";
		if((cs = hasFullHouse(sorted)) != null) return cs[0].getName() + "s full of " + cs[1].getName() + "s";
		if((c = hasFlush(sorted)) != null) return c.getName() + " high flush";
		if((c = hasStraight(sorted)) != null) return c.getName() + " high straight";
		if((c = hasThreeOfAKind(sorted)) != null) return "Set of " + c.getName() + "s, " + getKicker(sorted, c).getName() + " kicker";
		if((cs = hasTwoPair(sorted)) != null) return cs[0].getName() + "s and " + cs[1].getName() + "s, " + getKicker(sorted, cs).getName() + " kicker";
		if((c = hasPair(sorted)) != null) return "Pair of " + c.getName() + "s, " + getKicker(sorted, c).getName() + " kicker";
		return sorted[0].getName() + " high, " + getKicker(sorted, sorted[0]).getName() + " kicker";
	}
	
	private Card getKicker(Card[] sorted, Card exclude) {
		for(Card card : sorted) {
			if(card.compareTo(exclude) != 0) return card;
		}
		return null;
	}
	
	private Card getKicker(Card[] sorted, Card[] exclude) {
		boolean match;
		for(Card card : sorted) {
			match = false;
			for(Card c : exclude) {
				if(card.compareTo(c) == 0) match = true;
			}
			if(match != true) return card;
		}
		return null;
	}

	@Override
	public int compareTo(Hand arg0) {
		return arg0.getValue() - getValue();
	}
	
	public Hand clone() {
		Hand h = new Hand(name);
		h.cards.addAll(cards);
		return h;
	}
}
