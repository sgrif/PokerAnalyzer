package com.sgrif.poker;

import java.util.Arrays;

public class Game {
	private int hand_size;
	private int hand_cards_playable;
	private int board_size;
	private int deck[] = Deck.getDeck();
	
	private boolean royal_beats_5 = false;
	
	private int wins = 0;
	private int played = 0;
	
	//private boolean[] board_flush_possible;
	//private boolean[] board_straight_possble;
	
	private int[] rankings;
	private int[] unique5;
	private int[] flushes;
	
	public Game(int h, int cp, int b) {
		
		hand_size = h;
		hand_cards_playable = cp;
		board_size = b;
		//board_flush_possible = new boolean[(int)Math.pow(7, board_size) + 1];
		
		int n = ((1 << 12) | (1 << 11) | (1 << 10) | (1 << 9) | (1 << 8)+1);
		unique5 = new int[n];
		flushes = new int[n];
		rankings = new int[7475];
		
		generateRankings(0);
	}
	
	private void generateRankings(int from) {
		int i, j, k, l, m;
		int n=0;
		
		//High card
		for(i=5; i<13; i++) { // Can't have a non-straight hand lower than 75432
			for(j=3; j<i; j++) {
				for(k=2; k<j; k++) {
					for(l=1; l<k; l++) {
						for(m=0; m<l && !(i-m==4 || (i==12 && j==3 && k==2 && l==1 && m==0)); m++) { // No straights
							unique5[((1 << i) | (1 << j) | (1 << k) | (1 << l) | (1 << m))] = n;
							n++;
						}
					}
				}
			}
		}
		
		//Single pair
		for(i=0; i<13; i++) { // The Pair
			for(j=2; j<13; j++) { //Impossible to have any kicker lower than 4
				for(k=1; k<j; k++) { //Don't want to pair our kickers
					for(l=0; l<k; l++) {
						if(i!=j && i!=k && i!=l) { // No trips
							rankings[n] = Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[j] * Deck.PRIMES[k] * Deck.PRIMES[l];
							n++;
						}
					}
				}
			}
		}
		
		//Two pair
		for(i=1; i<13; i++) { //First pair
			for(j=0; j<i; j++) { //Second pair can't be higher than first pair
				for(k=0; k<13; k++) { //Kicker
					if(k!=i && k!=j) { //No boats
						rankings[n] = Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[j] * Deck.PRIMES[j] * Deck.PRIMES[k];
						n++;
					}
				}
			}
		}
		
		//Trips
		for(i=0; i<13; i++) { //Trips
			for(j=1; j<13; j++) { //Can't have kicker lower than 3
				for(k=0; k<j; k++) {
					if(i!=j && i!=k) { //No quads
						rankings[n] = Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[j] * Deck.PRIMES[k];
						n++;
					}
				}
			}
		}
		
		//Small straight
		unique5[((1 << 12) | (1 << 0) | (1 << 1) | (1 << 2) | (1 << 3))] = n;
		n++;
		
		//Normal straight
		for(i=0; i<9; i++) {
			unique5[((0x1F << i))] = n;
			n++;
		}
				
		//Flush
		for(i=5; i<13; i++) { // Can't have a non-straight hand lower than 75432
			for(j=3; j<i; j++) {
				for(k=2; k<j; k++) {
					for(l=1; l<k; l++) {
						for(m=0; m<l && !(i-m==4 || (i==12 && j==3 && k==2 && l==1 && m==0)); m++) { // No straight flushes
							flushes[((1 << i) | (1 << j) | (1 << k) | (1 << l) | (1 << m))] = n;
							n++;
						}
					}
				}
			}
		}
		
		//Full house
		for(i=0; i<13; i++) { //Trips
			for(j=0; j<13; j++) { //Pair
				if(i!=j) { //No 5 of a kind
					rankings[n] = Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[j] * Deck.PRIMES[j];
					n++;
				}
			}
		}
		
		//Quads
		for(i=0; i<13; i++) { //Quad
			for(j=0; j<13; j++) { //Kicker
				if(i!=j) { //No 5 of a kind
					rankings[n] = Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[j];
					n++;
				}
			}
		}
		
		//Small straight flush
		flushes[((1 << 12) | (1 << 0) | (1 << 1) | (1 << 2) | (1 << 3))] = n;
		n++;
		
		//Normal straight flush
		for(i=0; i<8; i++) { // Exclude royal in case rules say it beats 5 of a kind
			flushes[((0x1F << i))] = n;
			n++;
		}
		
		if(!royal_beats_5) {
			flushes[(0x1F<<8)] = n;
			n++;
		}
		
		//Five of a kind
		for(i=0; i<13; i++) {
			rankings[n] = Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i];
			n++;
		}
		
		if(royal_beats_5) {
			flushes[(0x1F<<8)] = n;
			n++;
		}
	}
	
	public int getRanking(int c0, int c1, int c2, int c3, int c4) {
		int q = (c0 >> 16) | (c1 >> 16) | (c2 >> 16) | (c3 >> 16) | (c4 >> 16);
		if((c0 & c1 & c2 & c3 & c4 & 0xF000) > 0) return flushes[q];
		if(unique5[q] > 0) return unique5[q];
		return Arrays.binarySearch(rankings, 0, 7475, ((c0 & 0xFF) * (c1 & 0xFF) * (c2 & 0xFF) * (c3 & 0xFF) * (c4 & 0xFF)));
	}
	
	/*
	private void generateBoardFlushChecks(int from, int[] board) {
		if(from == board_size) {
			int product = 1;
			for(int c : board) {
				product *= (c>>8 & 0xF);
			}
			if(!board_flush_possible[product]) {
				board_flush_possible[product] = boardCheckFlushPossible(0, 0, board, new int[board_size-hand_cards_playable]);
				String s = "";
				for(int c : board) {
					s += Integer.valueOf(c>>8 & 0xF).toString();
				}
				System.out.println(s + " flush possible: " + Boolean.valueOf(board_flush_possible[product]).toString());
			}
		} else {
			for(int x=0;x<4;x++) {
				board[from] = deck[13*x+1];
				generateBoardFlushChecks(from+1, board);
			}
		}
	}
	
	private boolean boardCheckFlushPossible(int from, int count, int[] board, int[] using) {
		if(count == board_size - hand_cards_playable) {
			int r = 0xF000;
			for(int c : using) {
				r &= c;
			}
			if(r > 0) return true;
		} else {
			for(int x=from;x<board_size;x++) {
				using[count] = board[x];
				boolean v = boardCheckFlushPossible(x+1, count+1, board, using);
				if(v == true) return true;
			}
		}
		return false;
	}
	*/
	public void enumerateAll(int rank1, int rank2, int suit1, int suit2) {
		
	}
}