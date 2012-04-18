package com.sgrif.poker;

import java.util.*;

public class Deck {
	private static Deck the_deck;
	private Random random = new Random();
	private LinkedHashMap<String, Card> cards = new LinkedHashMap<String, Card>();
	private LinkedHashMap<String, Card> unusedCards;
	
	private Deck() {
		String[] suits = {"s", "c", "h", "d"};
		String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A" };
		
		for(String suit : suits) {
			for(String value : values) {
				cards.put(value + suit, new Card(value, suit));
			}
		}
		
		shuffle();
	}
	
	public static Deck getTheDeck() {
		if(the_deck == null) {
			the_deck = new Deck();
		}
		return the_deck;
	}
	
	@SuppressWarnings("unchecked")
	public void shuffle() {
		unusedCards = (LinkedHashMap<String, Card>) cards.clone();
	}
	
	public Card drawCard() {
		Object[] keys = unusedCards.keySet().toArray();
		return drawCard(keys[random.nextInt(keys.length)].toString());
	}
	
	public Card drawCard(String s) {
		return unusedCards.remove(s);
	}
	
	public Card reserveCard(String s) {
		cards.remove(s);
		return drawCard(s);
	}
	
	public Card[] getCards() {
		return (Card[]) unusedCards.values().toArray();
	}
}
