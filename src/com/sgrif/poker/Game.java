package com.sgrif.poker;

import java.util.*;

public class Game {
	private Deck deck = Deck.getTheDeck();
	private Hand starting_hero = new Hand("Hero");
	private Hand hero;
	private ArrayList<Card> starting_board = new ArrayList<Card>();
	private ArrayList<Card> board = new ArrayList<Card>();
	private ArrayList<Hand> opponents = new ArrayList<Hand>();
	private int wins = 0;
	private int losses = 0;
	
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
	
	public Game(Integer num_oppponents, String h) {
		this(num_oppponents, h, "");
	}
	
	@SuppressWarnings("unchecked")
	public float enumerateAll() {
		hero = (Hand) starting_hero.clone();
		board = (ArrayList<Card>) starting_board.clone();
		String[] useableCards = deck.listCards();
		BitSet cardsUsed = new BitSet();
		
		enumerateHero(hero.size(), useableCards, cardsUsed);
		
		return (float) wins/(wins+losses);
	}
	
	private void enumerateHero(int from, String[] useableCards, BitSet cardsUsed) {		
		if (from == 2) {
			enumerateOpponents(opponents.get(0).size(), 0, useableCards, cardsUsed);
		} else {
			for (int i = 0; i < useableCards.length; i++) {
				if (!cardsUsed.get(i)) {
					hero.addCard(deck.reserveCard(useableCards[i]));
					cardsUsed.set(i);
					enumerateHero(from+1, useableCards, cardsUsed);
					hero.discardCard(useableCards[i]);
					deck.unreserveCard(useableCards[i]);
					cardsUsed.clear(i);
				}
			}
		}
	}
	
	private void enumerateOpponents(int from, int oppid, String[] useableCards, BitSet cardsUsed) {
		if(oppid == opponents.size()) {
			enumerateBoard(board.size(), useableCards, cardsUsed);
		} else {
			if (from == 2) {
				enumerateOpponents(from, oppid+1, useableCards, cardsUsed);
			} else {
				for (int i = 0; i < useableCards.length; i++) {
					if (!cardsUsed.get(i)) {
						opponents.get(oppid).addCard(deck.reserveCard(useableCards[i]));
						cardsUsed.set(i);
						enumerateOpponents(from+1, oppid, useableCards, cardsUsed);
						opponents.get(oppid).discardCard(useableCards[i]);
						deck.unreserveCard(useableCards[i]);
						cardsUsed.clear(i);
					}
				}
			}
		}
	}
	
	private void enumerateBoard(int from, String[] useableCards, BitSet cardsUsed) {		
		if (from == 5) {
			Hand winner = determineWinner();
			if(winner.equals(hero)) {
				wins++;
			} else {
				losses++;
			}
		} else {
			for (int i = 0; i < useableCards.length; i++) {
				if (!cardsUsed.get(i)) {
					board.add(deck.reserveCard(useableCards[i]));
					cardsUsed.set(i);
					enumerateBoard(from+1, useableCards, cardsUsed);
					Card r = null;
					for(Card c : board) {
						if(c.toString().equals(useableCards[i]));
						r = c;
					}
					board.remove(r);
					deck.unreserveCard(useableCards[i]);
					cardsUsed.clear(i);
				}
			}
		}
	}
	
	private Hand determineWinner() {
		ArrayList<Hand> players = new ArrayList<Hand>();
		players.add(hero);
		players.addAll(opponents);
		for(Hand player : players) player.determineValue(board);
		Collections.sort(players);
		return players.get(0);
	}
	
	@SuppressWarnings("unchecked")
	private boolean run() {
		for(int x = hero.size(); x < 2; x++) {
			hero.addCard(deck.drawCard());
		}
		
		for(Hand opponent : opponents) {
			for(int x = opponent.size(); x < 2; x++) {
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
