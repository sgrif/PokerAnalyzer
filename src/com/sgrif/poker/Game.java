package com.sgrif.poker;

import java.util.*;

public class Game {
	private Deck deck = Deck.getTheDeck();
	private Hand starting_hero = new Hand("Hero");
	private Hand hero;
	private ArrayList<Card> starting_board = new ArrayList<Card>();
	private ArrayList<Card> board = new ArrayList<Card>();
	private ArrayList<Hand> opponents = new ArrayList<Hand>();
	
	public Game(Integer num_opponents, String h, String b) {		
		for(int x = 0; x < 1; x++) { // Create an empty hand for each opponent
			opponents.add(new Hand("Opponent " + (x+1)));
		}
		
		for(int x = 0; x < h.length(); x+=2) {
			starting_hero.addCard(deck.reserveCard(h.substring(x, x+2)));
		}
		
		if(b.length() >= 2) {
			for(int x = 0; x < b.length(); x+=2) {
				starting_board.add(deck.reserveCard(b.substring(x, x+2)));
			}
		}
	}
	
	public float enumerateAll() {
		int wins = 0;
		int losses = 0;
		Card[] deck = Deck.getTheDeck().getCards();
		Hand hero = (Hand) starting_hero.clone();
		Hand opponent = new Hand("Opponent");
		ArrayList<Card> board = (ArrayList<Card>) starting_board.clone();
		
		return (float) wins/(wins+losses);
	}
	
	private void reset(Hand h, Hand o, ArrayList<Card> b) {
		deck.shuffle();
		board = b;
		hero = h;
		for(Hand opponent : opponents) {
			opponent = o;
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean run() {
		for(int x = hero.getCards().size(); x < 2; x++) {
			hero.addCard(deck.drawCard());
		}
		
		for(Hand opponent : opponents) {
			for(int x = opponent.getCards().size(); x < 2; x++) {
				opponent.addCard(deck.drawCard());
			}
		}
		
		for(int x = board.size(); x < 5; x++) {
			board.add(deck.drawCard());
		}
		
		ArrayList<Hand> players = (ArrayList<Hand>) opponents.clone();
		players.add(hero);
		
		for(Hand player : players) player.determineValue(board);
		Collections.sort(players);
				
		return players.get(0).equals(hero);
	}
	
	public String toString() {
		String s = "Hero: " + hero.toString() + "\n";
		s += "Board: " + starting_board.toString() + "\n";
		return s;
	}
}
