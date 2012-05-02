package com.sgrif.poker;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Locale;

public class Main {
	private static Game g = new Game(2, 2, 5);
	private static int[] deck = Deck.getDeck();

	public static void main(String[] args) {
		/*
		int x, y, z, a, b, c, d, e;
		for(x=0; x<13; x++) {
			for(y=0; y<13; y++) {
				for(z=0; z<13; z++) {
					for(a=0; a<13; a++) {
						for(b=1; b<13; b++) {
							for(c=1; c<13; c++) {
								for(d=1; d<13; d++) {
									if(x!=b && y!=c && z!=d) {
										e = g.getRank(deck[x], deck[y+13], deck[z+26], deck[a+39], deck[b], deck[c+13], deck[d+26]);
									}
								}
							}
						}
					}
				}
			}
		}
		*/
		bench(false, 1000000);
	}
	
	private static void test1() {
		g.testRank(deck[12], deck[11], deck[10], deck[9], deck[7], deck[6], deck[5]);
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
