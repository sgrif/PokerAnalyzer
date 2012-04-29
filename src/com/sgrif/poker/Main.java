package com.sgrif.poker;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Locale;

public class Main {
	static int[] d = Deck.getDeck();
	static Game g = new Game(2, 2, 5);

	public static void main(String[] args) {
		/*
		for(int x=0;x<48;x++) {
			for(int y=x+1; y<49; y++) {
				for(int z=y+1; z<50; z++) {
					for(int a=z+1; a<51; a++) {
						for(int b=a+1; b<52; b++) {
							System.out.println(g.getRanking(d[x], d[y], d[z], d[a], d[b]));
						}
					}
				}
			}
		}*/
		memBench();
	}
	
	private static void test1() {
		int i = g.getRanking(d[0], d[1], d[2], d[3], d[12]);
	}
	
	private static void test2() {
		int i = g.getRanking(d[12], d[24], d[36], d[48], d[8]);
	}
	
	private static void bench() {
		long best1 = 0;
		long best2 = 0;
		int times = 1000000;
		
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
		best1 /= times;
		best2 /= times;
		String[] s = {"nanoseconds", "milliseconds", "seconds"};
		int c1 = 0;
		int c2 = 0;
		
		System.out.printf(Locale.US, "Case 1 took %1$,d nano %n", best1);
		System.out.printf(Locale.US, "Case 2 took %1$,d nano %n", best2);
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
