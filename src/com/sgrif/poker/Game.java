package com.sgrif.poker;

import java.util.Arrays;

public class Game {
	private int hand_size;
	private int hand_cards_playable;
	private int board_size;
	private int deck[] = Deck.getDeck();
	
	private int wins = 0;
	private int played = 0;
	
	private boolean[] board_flush_possible;
	private boolean[] board_straight_possble;
	
	public Game(int h, int cp, int b) {
		
		hand_size = h;
		hand_cards_playable = cp;
		board_size = b;
		board_flush_possible = new boolean[(int)Math.pow(7, board_size) + 1];
		
		generateRankings(0);
	}
	
	private void generateRankings(int from) {
		generateBoardFlushChecks(0, new int[board_size]);
		generateBoardStraightChecks(0, new int[board_size]);
		
		if(from == hand_size) {
			
		} else {
			
		}
	}
	
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
	
	public void enumerateAll(int rank1, int rank2, int suit1, int suit2) {
		
	}
}