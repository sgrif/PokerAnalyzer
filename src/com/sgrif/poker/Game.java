package com.sgrif.poker;

import java.math.BigInteger;
import java.util.Arrays;

import static com.sgrif.poker.Factorial.*;

public class Game {
	private int hand_size;
	private int hand_cards_playable;
	private int board_size;
	private int cards_playable;
	private int deck[] = Deck.getDeck();
	
	private boolean royal_beats_5 = false;
	private boolean five_of_a_kind_allowed = false;
	
	private int wins = 0;
	private int played = 0;
	private int test = 0;
	private boolean debug = false;
	
	private static int[] nonUniqueCache = new int[27];
	private static int[][] comRepetitionCache = new int[14][27];
	private static int[][] comUniqueCache = new int[14][27];
	private static int[] pairedStraightCache = new int[27];
	
	private long[] products5;
	private int[] rankings5;
	private int[] unique5;
	private int[] flushes5;
	
	private long[] products;
	private int[] rankings;
	private int[] unique;
	private int[] flushes;
	private int[][] permutations = {{0, 1, 2, 3, 4}};
	
	public static final int MAX_UNIQUE_INDEX = 0x1FFF + 1;
	public static final int MAX_UNIQUE_INDEX5 = 0x1F00 + 1;
	
	public Game(int h, int cp, int b) {
		
		hand_size = h;
		hand_cards_playable = cp;
		board_size = b;
		cards_playable = cp + b;
		
		unique5 = new int[MAX_UNIQUE_INDEX5];
		flushes5 = new int[MAX_UNIQUE_INDEX5];
		products5 = new long[possibleNonUnique(5)];
		rankings5 = new int[possibleNonUnique(5)];
		
		generateRankings5();
		
		if(cards_playable == 5) {
			unique = unique5;
			flushes = flushes5;
			products = products5;
			rankings = rankings5;
		} else {
			int perm_count = possiblePermutationsOfHandSize(cards_playable);
			int max_non_unique = possibleNonUnique(cards_playable);
			
			unique = new int[MAX_UNIQUE_INDEX];
			flushes = new int[MAX_UNIQUE_INDEX];
			products = new long[max_non_unique];
			rankings = new int[max_non_unique];
			permutations = new int[perm_count][5];
			generatePermutations();
			generateRankings();
		}
	}
	
	private void generateRankings5() {
		int i, j, k, l, m;
		int n=1;
		int o=0;
		
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
							products5[o] = Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[j] * Deck.PRIMES[k] * Deck.PRIMES[l];
							rankings5[o] = n;
							n++; o++;
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
						products5[o] = Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[j] * Deck.PRIMES[j] * Deck.PRIMES[k];
						rankings5[o] = n;
						n++; o++;
					}
				}
			}
		}
		
		//Trips
		for(i=0; i<13; i++) { //Trips
			for(j=1; j<13; j++) { //Can't have kicker lower than 3
				for(k=0; k<j; k++) {
					if(i!=j && i!=k) { //No quads
						products5[o] = Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[j] * Deck.PRIMES[k];
						rankings5[o] = n;
						n++; o++;
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
						for(m=0; m<l && !(i-m==4 || (i==12 && j==3 && k==2 && l==1 && m==0)); m++) { // No straight flushes5
							flushes5[((1 << i) | (1 << j) | (1 << k) | (1 << l) | (1 << m))] = n;
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
					products5[o] = Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[j] * Deck.PRIMES[j];
					rankings5[o] = n;
					n++; o++;
				}
			}
		}
		
		//Four of a kind
		for(i=0; i<13; i++) { //Four
			for(j=0; j<13; j++) { //Kicker
				if(i!=j) { //No 5 of a kind
					products5[o] = Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[j];
					rankings5[o] = n;
					n++; o++;
				}
			}
		}
		
		//Small straight flush
		flushes5[((1 << 12) | (1 << 0) | (1 << 1) | (1 << 2) | (1 << 3))] = n;
		n++;
		
		//Normal straight flush
		for(i=0; i<8; i++) { // Exclude royal in case rules say it beats 5 of a kind
			flushes5[((0x1F << i))] = n;
			n++;
		}
		
		if(!royal_beats_5) {
			flushes5[(0x1F<<8)] = n;
			n++;
		}
		
		/*
		//Five of a kind
		for(i=0; i<13; i++) {
			products5[o] = Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i] * Deck.PRIMES[i];
			rankings5[o] = n;
			n++; o++;
		}
		*/
		
		if(royal_beats_5) {
			flushes5[(0x1F<<8)] = n;
			n++;
		}
		
		associativeSort(products5, rankings5);
	}
	
	public int getRanking5(int c0, int c1, int c2, int c3, int c4) {
		int q = (c0 >> 16) | (c1 >> 16) | (c2 >> 16) | (c3 >> 16) | (c4 >> 16);
		if((c0 & c1 & c2 & c3 & c4 & 0xF000) > 0) return flushes5[q];
		if(unique5[q] > 0) return unique5[q];
		int x = Arrays.binarySearch(products5, ((c0 & 0xFF) * (c1 & 0xFF) * (c2 & 0xFF) * (c3 & 0xFF) * (c4 & 0xFF)));
		return rankings5[x];
	}
	
	public int getRank(int c0, int c1, int c2, int c3, int c4, int c5, int ... cards) {
		int q = (c0 >> 16) | (c1 >> 16) | (c2 >> 16) | (c3 >> 16) | (c4 >> 16) | (c5 >> 16);
		long product = (c0 & 0xFF) * (c1 & 0xFF) * (c2 & 0xFF) * (c3 & 0xFF) * (c4 & 0xFF) * (long)(c5 & 0xFF);
		for(int c : cards) {
			q |= (c >> 16);
			product *= (c & 0xFF);
		}
		if(flushes[q] > 0) return flushes[q];
		if(unique[q] > 0) return unique[q];
		int x = Arrays.binarySearch(products, product);
		return rankings[x];
	}
	
	public int getBestOf(int ... cards) {
		int best = 0;
		int current;
		for(int[] p : permutations) {
			current = getRanking5(cards[p[0]], cards[p[1]], cards[p[2]], cards[p[3]], cards[p[4]]);
			if(current > best) best=current;
		}
		int bf=0;
		for(int c : cards) {
			bf |= c;
		}
		bf>>=16;
		if(best > 5853 && best < 5864 && Integer.bitCount(bf) < cards_playable) test++;
		return best;
	}
	
	private void generateRankings() {
		if(cards_playable == 5) {
			return;
		}
		generateNonFlushRankings();
		associativeSort(products, rankings);
		//testAll(0, new int[cards_playable]);
		System.out.println("Number of excess indexes: " + (-1 - Arrays.binarySearch(products, 1)));
		System.out.println("Number of straights with pairs: " + test);
		System.out.println("Number of combinations for straights: " + possiblePairedStraights(cards_playable));
		System.out.println("Number of illegal straights included: " + ((-1 - Arrays.binarySearch(products, 1) - possiblePairedStraights(cards_playable))));
		System.out.println("Number of boat/quad straights included: " + (possiblePairedStraights(cards_playable) - test));
		test(cards_playable);
	}
	
	private int test(int from, int current, int counter) {
		if(from == cards_playable-5) {
			counter++;
		} else {
			for(int x=current; x<13; x++) {
				counter = test(from+1, x, counter);
			}
		}
		return counter;
	}
	
	private void testAll(int from, int[] cards) {
		if(from == cards_playable) {
			int[] other_cards = new int[cards.length-6];
			for(int x=6; x<other_cards.length+6; x++) {
				other_cards[x-6] = deck[cards[x]];
			}
			int i = getRank(deck[cards[0]], deck[cards[1]], deck[cards[2]], deck[cards[3]], deck[cards[4]], deck[cards[5]], other_cards);
		} else {
			for(int x=0; x<13; x++) {
				if(from >= 4) {
					if(x != cards[from-4]) {
						cards[from] = x;
						testAll(from+1, cards);
					}
				} else {
					cards[from] = x;
					testAll(from+1, cards);
				}
			}
		}
	}
	
	public int generatePermutations(int from, int count, int[] cards, int index) {
		if(count == 5) {
			permutations[index] = Arrays.copyOf(cards, cards.length);
			index++;
		} else {
			for(int x=from; x<cards_playable; x++) {
				cards[count] = x;
				index = generatePermutations(x+1, count+1, cards, index);
			}
		}
		return index;
	}
	
	private void generatePermutations() {
		generatePermutations(0, 0, new int[5], 0);
	}
	
	private int generateNonFlushRankings(int from, int current, int[] cards, int counter, int match) {
		if(from == cards_playable) {
			int bf = 0;
			long product = 1;
			for(int c : cards) {
				bf |= c;
				product *= (c & 0xFF);
			}
			bf >>= 16;
			int rank = getBestOf(cards);
			if((Integer.bitCount(bf) == cards_playable
					||(bf & 0x100F) == 0x100F
					|| (bf & 0x001F) == 0x001F
					|| (bf & 0x003E) == 0x003E
					|| (bf & 0x007C) == 0x007C
					|| (bf & 0x00F8) == 0x00F8
					|| (bf & 0x01F0) == 0x01F0
					|| (bf & 0x03E0) == 0x03E0
					|| (bf & 0x07C0) == 0x07C0
					|| (bf & 0x0F80) == 0x0F80
					|| (bf & 0x1F00) == 0x1F00)
					/*&& rank < 5864*/) {
				unique[bf] = rank;
				return counter;
			}
			products[counter] = product;
			rankings[counter] = rank;
			counter++;
		} else {
			int next, next_match;
			if(match > 0) {
				next = current;
				next_match = match-1;
			} else {
				next = current+1;
				next_match = 3;
			}
			
			for(int x=next; x<13; x++, next_match=3) {
				cards[from] = deck[x + (13*(from%4))];
				counter = generateNonFlushRankings(from+1, x, cards, counter, next_match);
			}
		}
		return counter;
	}
	
	private void generateNonFlushRankings() {
		generateNonFlushRankings(0, 0, new int[cards_playable], 0, 4);
	}
	
	/*
	 * Formula for all non-flush hands is calculated by determining all possible combinations with
	 * repetition, subtracting all combinations without repetition, and subtracting combinations
	 * more than 4 of a card.
	 * All combinations with repetition:
	 * (13+num_cards-1)!/(num_cards!*(13-1)!)
	 * 
	 * All combinations without repetition:
	 * 13!/(num_cards!*(13-num_cards)!)
	 * 
	 * Combinations with more than 4 of a card:
	 * ((13+num_cards-5-1)!/(num_cards-5)!*(13-1)!)*13
	 */
	private static int possibleNonUnique(int i) {
		if(nonUniqueCache[i] > 0) return nonUniqueCache[i];		
		int rv = comRepetition(13, i) - comRepetition(13, i-5)*13 - comUnique(13, i);
		nonUniqueCache[i] = rv;
		return rv;
	}
	
	/**
	 * Calculates the number of unique combinations with repetition of count cards within max values
	 * Formula:
	 * (max+count-1)!/(count!*(max-1)!)
	 * 
	 * @param max Number of possible values for the card between 1 and 13
	 * @param count Number of cards to calculate for
	 * @return Result of formula
	 */
	private static int comRepetition(int max, int count) {
		if(count < 0) return 0;
		if(comRepetitionCache[max][count] > 0) return comRepetitionCache[max][count];
		int ret = factorial(max+count-1).divide(factorial(count).multiply(factorial(max-1))).intValue();
		comRepetitionCache[max][count] = ret;
		return ret;
	}
	
	/**
	 * Calculates the number of unique combinations without repetition of count cards within max
	 * values. 
	 * Formula:
	 * max!/(count!*(max-count)!)
	 * 
	 * @param max Number of possible values for the card between 1 and 13
	 * @param count Number of cards to calculate for
	 * @return Result of formula
	 */
	private static int comUnique(int max, int count) {
		if(count < 0) return 0;
		if(comUniqueCache[max][count] > 0) return comUniqueCache[max][count];
		int ret = factorial(max).divide(factorial(count).multiply(factorial(max - count))).intValue();
		comUniqueCache[max][count] = ret;
		return ret;
	}
	
	/**
	 * Returns the number of ways to have a straight and also a pair, two pair, or 
	 * a set. Formula takes all combinations with repetition that wouldn't give a higher
	 * straight, and removes all combinations without repetition that wouldn't give a higher
	 * straight, and wouldn't be paired with a card in the straight. 
	 * 
	 * Still need to exclude full houses, four of a kind, and illegal hands
	 * 
	 * @param i The number of playable cards
	 * @return The number of ways to make a straight with a pair, two pair, or set
	 */
	private static int possiblePairedStraights(int i) {
		if(pairedStraightCache[i] > 0) return pairedStraightCache[i];
		int ret = (comRepetition(12, i-5) - comUnique(7, i-5)) * 9; //All non ace straights
		ret += comRepetition(13, i-5) - comUnique(8, i-5); //Ace high straight
		pairedStraightCache[i] = ret;
		return ret;
	}
	
	private static void test(int i) {
		int x = (comRepetition(5, 3) - comUnique(5, 3)) * comRepetition(12, i-8);
		x += 5 * (comRepetition(12, i-7) - comUnique(12, i-7));
		x += comRepetition(12, i-9);
		
		System.out.println(x);
		
		int y = (comRepetition(12, i-8) - comUnique(12, i-8)) * 5;
		
		/*
		int counter=0;
		for(int j=0; j<5; j++) {
			for(int k=0; k<5; k++) {
				if(j != k) {
					for(int l=0; l<12; l++) {
						counter++;
					}
				}
			}
		}*/
	}
	
	private static int possiblePermutationsOfHandSize(int i) {
		return factorial(i).divide(factorial(5).multiply(
				factorial(i - 5))).intValue();
	}
	
	/**
	 * Sorts arr0 using quick sort, and maintains the same order for arr1
	 * @param arr0 The array to be sorted
	 * @param arr1 The associated array
	 * @param left Starting index
	 * @param right Ending index
	 */
	private static void associativeSort(long[] arr0, int[] arr1, int left, int right) {
		int index = partition(arr0, arr1, left, right);
		if(left < index - 1)
			associativeSort(arr0, arr1, left, index-1);
		if(index < right)
			associativeSort(arr0, arr1, index, right);
	}
	
	private static int partition(long[] arr0, int[] arr1, int left, int right) {
		int i=left,j=right;
		long tmp;
		int tmp1;
		long pivot = arr0[(left+right)>>1];
		
		while(i <= j) {
			while(arr0[i] < pivot) i++;
			while(arr0[j] > pivot) j--;
			if(i<=j) {
				tmp = arr0[i]; tmp1 = arr1[i];
				arr0[i] = arr0[j]; arr1[i] = arr1[j];
				arr0[j] = tmp; arr1[j] = tmp1;
				i++;
				j--;
			}
		}
		
		return i;
	}
	
	private static void associativeSort(long[] arr0, int[] arr1) {
		associativeSort(arr0, arr1, 0, arr0.length-1);
	}
	
	public void enumerateAll(int rank1, int rank2, int suit1, int suit2) {
		
	}
}