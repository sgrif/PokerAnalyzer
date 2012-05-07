package com.sgrif.poker;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Locale;
import static com.sgrif.poker.Deck.PRIMES;

public class Main {
	private static Game g = new Game(0, 10, 0);
	private static int[] deck = Deck.getDeck();

	public static void main(String[] args) {
		/*
		int counter = 0;
		BitSet bs = new BitSet();
		HashMap<Long, Boolean> hm = new HashMap<Long, Boolean>();
		for(int x=0; x<5; x++) {
			for(int y=0; y<13; y++) {
				for(int z=0; z<13; z++) {
					for(int a=0; a<13; a++) {
						int product = PRIMES[x] * PRIMES[y] * PRIMES[z] * PRIMES[a];
						if(!bs.get(product) && a!=x) {
							counter++;
						}
						bs.set(product);
					}
				}
			}
		}
		System.out.println(counter);
		counter=0;
		for(int x=0; x<13; x++) {
			for(int y=0; y<=x; y++) {
				for(int z=0; z<=y; z++) {
					for(int a=0; a<=z; a++) {
						for(int b=0; b<=a; b++) {
							for(int c=0; c<=b; c++) {
								for(int d=0; d<=c; d++) {
									for(int e=0; e<=d; e++) {
										for(int f=0; f<=e; f++) {
											if(x!=b && y!=c && z!=d && a!=e && b!=f) {
												long product = (long)PRIMES[x] * (long)PRIMES[y] * (long)PRIMES[z] * (long)PRIMES[a] * (long)PRIMES[b] * (long)PRIMES[c] * (long)PRIMES[d] * (long)PRIMES[e] * (long)PRIMES[f];
												int bf = (1<<x) | (1<<y) | (1<<z) | (1<<a) | (1<<b) | (1<<c) | (1<<d) | (1<<e) | (1<<f);
												if(null == hm.get(Long.valueOf(product)) && Integer.bitCount(bf) < 9) {
													counter++;
													hm.put(Long.valueOf(product), Boolean.TRUE);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println(counter);
		int counter = 0;
		HashMap<Long, Boolean> cache = new HashMap<Long, Boolean>();
		for(int x=0; x<13; x++) {
			for(int y=0; y<=x; y++) {
				for(int z=0; z<=y; z++) {
					for(int a=0; a<=z; a++) {
						for(int b=0; b<=a; b++) {
							for(int c=0; c<=b; c++) {
								for(int d=0; d<=c; d++) {
									for(int e=0; e<=d; e++) {
										if(e!=a && d!=z && c!=y && b!=x) {
											Long product = (long)PRIMES[e] * (long)PRIMES[d] * (long)PRIMES[c] * (long)PRIMES[b] * (long)PRIMES[a] * (long)PRIMES[z] * (long)PRIMES[y] * (long)PRIMES[x];
											int bf = (1<<e) | (1<<d) | (1<<c) | (1<<b) | (1<<a) | (1<<z) | (1<<y) | (1<<x); 
											if(null == cache.get(product) && Integer.bitCount(bf) < 8) {
												if((bf & 0x1F) == 0x1F00
												&& (bf & 0x003E) != 0x003E
												) {
													counter++;
												}
												cache.put(product, Boolean.TRUE);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println(counter);*/
	}
	
	private static void test1() {
		g.getRank(deck[12], deck[11], deck[10], deck[9], deck[7], deck[6]);
	}
	
	private static void test2() {
		g.getRank(deck[12], deck[11], deck[10], deck[9], deck[7], deck[6]);
	}
	
	private static void bench(boolean comp, int times) {
		long best1 = 0;
		long best2 = 0;
		
		for(int x=0;x<100;x++) {
			long time_before = System.nanoTime();
			for(int y=0;y<times;y++) {
				test1();
			}
			long time_after = System.nanoTime();
			long t = time_after - time_before;
			if(t < best1 || best1 == 0) {
				best1 = t;
			}
		}
		
		best1 /= times;
		int c1 = 0;
		String[] s = {"nanoseconds", "microseconds", "milliseconds", "seconds"};
		while(best1 >= 1000) {
			best1 /= 1000;
			c1 += 1;
		}
		System.out.printf(Locale.US, "Case 1 took %1$,d %2$s %n", best1, s[c1]);
		
		if(comp) {
			for(int x=0;x<100;x++) {
				long time_before = System.nanoTime();
				for(int y=0;y<times;y++) {
					test2();
				}
				long time_after = System.nanoTime();
				long t = time_after - time_before;
				if(t < best2 || best2 == 0) {
					best2 = t;
				}
			}
			
			best2 /= times;
			int c2 = 0;
			while(best2 >= 1000) {
				best2 /= 1000;
				c2 += 1;
			}
			System.out.printf(Locale.US, "Case 2 took %1$,d %2$s %n", best2, s[c2]);
		}
	}
	private static void memBench() {
		Object o = new Game(2, 2, 5);
		long mem_before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long mem_after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		o = null;
		
		System.gc(); System.gc(); System.gc(); System.gc(); 
		System.gc(); System.gc(); System.gc(); System.gc(); 
		System.gc(); System.gc(); System.gc(); System.gc(); 
		System.gc(); System.gc(); System.gc(); System.gc(); 
		
		mem_before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		o = new Game(2, 2, 5);
		
		System.gc(); System.gc(); System.gc(); System.gc(); 
		System.gc(); System.gc(); System.gc(); System.gc(); 
		System.gc(); System.gc(); System.gc(); System.gc(); 
		System.gc(); System.gc(); System.gc(); System.gc(); 
		
		mem_after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long m = mem_after - mem_before;
		String[] s = {"B", "KB", "MB", "GB"};
		int c = 0;
		
		while(m >= 1024) {
			m >>= 10;
			c += 1;
		}
		
		System.out.printf(Locale.US, "Object cost %1$,d%2$s of memory %n", m, s[c]);
	}
}
