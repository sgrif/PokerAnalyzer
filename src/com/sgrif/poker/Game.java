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
	
	private static int[] nonUniqueCache = new int[26];
	private static int[] uniqueCache = new int[26];
	
	private long[] products5;
	private int[] rankings5;
	private int[] unique5;
	private int[] flushes5;
	
	private long[] products;
	private int[] rankings;
	private int[] unique;
	private int[] flushes;
	private int[][] permutations = {{0, 1, 2, 3, 4}};
	
	public Game(int h, int cp, int b) {
		
		hand_size = h;
		hand_cards_playable = cp;
		board_size = b;
		cards_playable = cp + b;
		
		unique5 = new int[maxUniqueIndex(5)];
		flushes5 = new int[maxUniqueIndex(5)];
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
			int max_unique_five_or_more = maxUniqueIndex(cards_playable);
			int max_non_unique = possibleNonUnique(cards_playable);
			
			unique = new int[max_unique_five_or_more];
			flushes = new int[max_unique_five_or_more];
			products = new long[max_non_unique];
			rankings = new int[max_non_unique];
			permutations = new int[perm_count][5];
			generatePermutations();
		}
		
		generateRankings();
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
		int counter = 0;
		for(int x=0; x<5; x++) {
			counter = test(1, x, counter);
		}
		System.out.println("Number of excess indexes: " + (-1 - Arrays.binarySearch(products, 1)));
		System.out.println("Number of straights with pairs: " + test);
		System.out.println("Number of combinations for one straight: " + counter);
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
					&& rank < 5864) {
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
		BigInteger all, five_or_more;
		
		all = factorial(13+i-1).divide(factorial(i).multiply(factorial(12)));
		five_or_more = factorial(13+i-6).divide(factorial(i-5).multiply(factorial(12))).multiply(BigInteger.valueOf(13));
		int rv = all.subtract(five_or_more).intValue() - possibleUnique(i);
		
		nonUniqueCache[i] = rv;
		return rv;
	}
	
	private static int possibleUnique(int i) {
		if(uniqueCache[i] > 0) return uniqueCache[i];
		BigInteger ret;
		ret = factorial(13).divide(factorial(i).multiply(factorial(13-i)));
		int reti = ret.intValue();
		uniqueCache[i] = reti;
		return reti;
	}
	
	public static int maxUniqueIndex(int i) {
		int r = 0;
		for(int x=12; x>12-i; x--) {
			r |= (1<<x);
		}
		return r+1;
	}
	
	public static int possiblePermutationsOfHandSize(int i) {
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