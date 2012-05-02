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
		
		if(cards_playable == 5) {
			unique = unique5;
			flushes = flushes5;
			products = products5;
			rankings = rankings5;
		} else {
			int perm_count = possiblePermutationsOfHandSize(cards_playable);
			int max_unique_five_or_more = maxUniqueIndex(cards_playable);
			int max_non_unique = possibleNonUnique(cards_playable) + 2;
			
			unique = new int[max_unique_five_or_more];
			flushes = new int[max_unique_five_or_more];
			products = new long[max_non_unique];
			rankings = new int[max_non_unique];
			permutations = new int[perm_count][5];
			generatePermutations();
		}
		
		generateRankings5();
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
	
	public int testRank(int ... c) {
		int q = 0, product = 1;
		q |= c[0];
		q |= c[1];
		q |= c[2];
		q |= c[3];
		q |= c[4];
		q |= c[5];
		q |= c[6];
		q >>= 16;
		product *= (c[0] & 0xFF);
		product *= (c[1] & 0xFF);
		product *= (c[2] & 0xFF);
		product *= (c[3] & 0xFF);
		product *= (c[4] & 0xFF);
		product *= (c[5] & 0xFF);
		product *= (c[6] & 0xFF);
		if(flushes[q] > 0) return flushes[q];
		if(unique[q] > 0) return unique[q];
		int x = Arrays.binarySearch(products, product);
		return rankings[x];
	}
	
	public int getRank(int c0, int c1, int c2, int c3, int c4, int c5, int ... cards) {
		int q = (c0 >> 16) | (c1 >> 16) | (c2 >> 16) | (c3 >> 16) | (c4 >> 16) | (c5 >> 16);
		long product = (c0 & 0xFF) * (c1 & 0xFF) * (c2 & 0xFF) * (c3 & 0xFF) * (c4 & 0xFF) * (c5 & 0xFF);
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
		return best;
	}
	
	private void generateRankings() {
		if(cards_playable == 5) {
			return;
		}
		
		int n=0;
		generateHighCardRankings();
		generateStraightRankings();
		n = generateSinglePairRankings(n);
		n = generateTwoPairRankings(n);
		n = generateTripRankings(n);
		n = generateFullHouseRankings(n);
		n = generateQuadRankings(n);
		associativeSort(products, rankings);
		//System.out.println(Arrays.toString(products));
		//System.out.println(Arrays.toString(rankings));
		//System.out.println(n+test);
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
	
	private int generateNonUniqueRankOf(int[] cards, int i) {
		int bf = 0;
		long product = 1;
		for(int c : cards) {
			bf |= c;
			product *= (c & 0xFF);
		}
		bf >>= 16;
		if((bf & 0x100F) == 0x100F
				|| (bf & 0x001F) == 0x001F
				|| (bf & 0x003E) == 0x003E
				|| (bf & 0x007C) == 0x007C
				|| (bf & 0x00F8) == 0x00F8
				|| (bf & 0x01F0) == 0x01F0
				|| (bf & 0x03E0) == 0x03E0
				|| (bf & 0x07C0) == 0x07C0
				|| (bf & 0x0F80) == 0x0F80
				|| (bf & 0x1F00) == 0x1F00)
			return 0;
		try {
			products[i] = product;
		} catch (ArrayIndexOutOfBoundsException e) {
			associativeSort(products, rankings);
			System.out.println(Arrays.toString(products));
		}
		rankings[i] = getBestOf(cards);
		return 1;
	}
	
	private void generateHighCardRankings() {
		generateHighCardRankings(0, 13, new int[cards_playable]);
	}
	
	private void generateHighCardRankings(int from, int current, int[] cards) {
		if(from == cards_playable) {
			int bf = 0;
			for(int b : cards) {
				bf |= b;
			}
			bf >>= 16;
			if((bf & 0x100F) == 0x100F
					|| (bf & 0x001F) == 0x001F
					|| (bf & 0x003E) == 0x003E
					|| (bf & 0x007C) == 0x007C
					|| (bf & 0x00F8) == 0x00F8
					|| (bf & 0x01F0) == 0x01F0
					|| (bf & 0x03E0) == 0x03E0
					|| (bf & 0x07C0) == 0x07C0
					|| (bf & 0x0F80) == 0x0F80
					|| (bf & 0x1F00) == 0x1F00)
				return;
			int rank = getBestOf(cards);
			unique[bf] = rank;
			test++;
		} else {
			int next = cards_playable - ((from+(4-cards_playable%4))>>2) - from + ((cards_playable-4)>>2);
			for(int x=next; x<current; x++) {
				cards[from] = deck[x + (13*(from>>2))];
				generateHighCardRankings(from+1, x, cards);
			}
		}
	}
	
	private void generateStraightRankings() {
		int[] cards = new int[cards_playable];
		for(int x=4; x<13; x++) { 
			cards[0] = deck[x];
			cards[1] = deck[x-1+13];
			cards[2] = deck[x-2+26];
			cards[3] = deck[x-3+39];
			cards[4] = deck[x-4];
			generateStraightKicker(5, cards);
		}
		cards[0] = deck[12];
		cards[1] = deck[3+13];
		cards[2] = deck[2+26];
		cards[3] = deck[1+39];
		cards[4] = deck[0];
		generateStraightKicker(5, cards);
	}
	

	private void generateStraightKicker(int from, int[] cards) {
		if(from == cards_playable) {
			int rank = getBestOf(cards);
			if(rank < 5854 || rank > 5863) return;
			int bf = 0;
			for(int b : cards) {
				bf |= b;
			}
			bf >>= 16;
			unique[bf] = rank;
			test++;
		} else {
			for(int x=0; x<13; x++) {
				cards[from] = deck[x + (13*(from>>2))];
				generateStraightKicker(from+1, cards);
			}
		}
	}
	
	private int generateUniqueKicker(int from, int current, int[] cards, int counter, int test_to) {
		if(from == cards_playable) {
			counter += generateNonUniqueRankOf(cards, counter);
		} else {
			int next = cards_playable - ((from+(4-cards_playable%4))>>2)-from + ((cards_playable-4)>>2);
			for(int x=next; x<current; x++) {
				boolean matches = false;
				for(int y=0; y<test_to; y++) {
					if(deck[x] == cards[y]) matches = true;
				}
				if(!matches) {
					cards[from] = deck[x + (13*(from>>2))];
					counter = generateUniqueKicker(from+1, x, cards, counter, test_to);
				}
			}
		}
		return counter;
	}
	
	private int generateNonUniqueKicker(int from, int current, int[] cards, int counter, int exclude) {
		if(from == cards_playable) {
			if(debug) {
				for(int c : cards) {
					System.out.print(Integer.toBinaryString(c) + " ");
				}
				System.exit(0);
			}
			counter += generateNonUniqueRankOf(cards, counter);
		} else {
			for(int x=0; x<13; x++) {
				if(x!=exclude) {
					cards[from] = deck[x + (13*(from%4))];
					counter = generateNonUniqueKicker(from+1, x, cards, counter, exclude);
				}
			}
		}
		return counter;
	}
	
	private int generateSinglePairRankings(int i) {
		int[] cards = new int[cards_playable];
		int x;
		int counter = i;
		
		for(x=0; x<13; x++) {
			cards[0] = deck[x]; cards[1] = deck[x+13];
			counter = generateUniqueKicker(2, 13, cards, counter, 1);
		}
		return counter;
	}
	
	private int generateTwoPairRankings(int i) {
		int[] cards = new int[cards_playable];
		int x, y;
		int counter = i;
		
		for(x=1; x<13; x++) {
			cards[0] = deck[x]; cards[2] = deck[x+13];
			for(y=0; y<x; y++) {
				cards[1] = deck[y]; cards[3] = deck[y+13];
				counter = generateUniqueKicker(4, 13, cards, counter, 2);
			}
		}
		return counter;
	}
	
	private int generateTripRankings(int i) {
		int[] cards = new int[cards_playable];
		int x;
		int counter = i;
		
		for(x=0; x<13; x++) {
			cards[0] = deck[x];
			cards[1] = deck[x+13];
			cards[2] = deck[x+26];
			counter = generateUniqueKicker(3, 13, cards, counter, 1);
		}
		
		return counter;
	}
	
	private int generateFullHouseRankings(int i) {
		int[] cards = new int[cards_playable];
		int x, y;
		int counter = i;
		
		for(x=1; x<13; x++) {
			cards[0] = deck[x]; cards[2] = deck[x+13]; cards[3] = deck[x+26];
			for(y=0; y<x; y++) {
				cards[1] = deck[y]; cards[4] = deck[y+13];
				counter = generateUniqueKicker(5, 13, cards, counter, 2);
			}
		}
		
		return counter;
	}
	
	private int generateQuadRankings(int i) {
		int[] cards = new int[cards_playable];
		int x;
		int counter = i;
		
		for(x=0; x<13; x++) {
			cards[0] = deck[x];
			cards[1] = deck[x+13];
			cards[2] = deck[x+26];
			cards[3] = deck[x+39];
			counter = generateNonUniqueKicker(4, 13, cards, counter, x);
		}
		
		return counter;
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