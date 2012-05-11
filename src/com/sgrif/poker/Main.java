package com.sgrif.poker;

import java.util.Locale;
import static com.sgrif.poker.Game.*;

public class Main {
	private static int[] deck = Deck.getDeck();

	public static void main(String[] args) {
		bench(false, 1);
	}
	
	private static void test1() {
		new Game(0, 8, 0);
	}
	
	private static void test2() {
		g.getRanking5(deck[0], deck[1], deck[2], deck[3], deck[13]);
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
