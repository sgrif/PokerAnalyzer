package com.sgrif.poker;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Locale;

public class Main {

	public static void main(String[] args) {
		Game g = new Game(2,2,5);
	}
	
	private static void bench() {
		long best1 = 0;
		long best2 = 0;

		for(int x=0;x<100;x++) {
			long time_before = System.nanoTime();
			Boolean[] bs = new Boolean[1000000];
			Arrays.fill(bs, true);
			for(int y=0;y<1000000;y++) {
				boolean b = bs[y];
			}
			long time_after = System.nanoTime();
			long t = time_after - time_before;
			if(t < best1 || best1 == 0) {
				best1 = t;
			}
		}
		
		for(int x=0;x<100;x++) {
			long time_before = System.nanoTime();
			boolean[] bs = new boolean[1000000];
			for(int y=0;y<1000000;y++) {
				boolean b = bs[y];
			}
			long time_after = System.nanoTime();
			long t = time_after - time_before;
			if(t < best2 || best2 == 0) {
				best2 = t;
			}
		}
		System.out.printf(Locale.US, "Case 1 took %1$,d nano %n", best1);
		System.out.printf(Locale.US, "Case 2 took %1$,d nano %n", best2);
	}
	private static void memBench() {
		long bestm1 = 0;
		long bestm2 = 0;
		for(int x=0;x<100;x++) {
			System.gc();
			long mem_before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			Boolean[] bs = new Boolean[1000000];
			Arrays.fill(bs, true);
			for(int y=0;y<1000000;y++) {
				boolean b = bs[y];
			}
			long mem_after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			long m = mem_after - mem_before;
			if(m < bestm1 || bestm1 == 0) {
				bestm1 = m;
			}
			System.gc();
		}
		
		for(int x=0;x<100;x++) {
			System.gc();
			long mem_before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			boolean[] bs = new boolean[1000000];
			Arrays.fill(bs, true);
			for(int y=0;y<1000000;y++) {
				boolean b = bs[y];
			}
			long mem_after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			long m = mem_after - mem_before;
			if(m < bestm2 || bestm2 == 0) {
				bestm2 = m;
			}
			System.gc();
		}
		
		java.lang.management.OperatingSystemMXBean mxbean = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
		com.sun.management.OperatingSystemMXBean sunmxbean = (com.sun.management.OperatingSystemMXBean) mxbean;
		long freeMemory = sunmxbean.getFreePhysicalMemorySize();
		long availableMemory = sunmxbean.getTotalPhysicalMemorySize();
		
		System.out.printf(Locale.US, "System has %2$,d free memory of %2$,d total %n", freeMemory, availableMemory);
		System.out.printf(Locale.US, "Java has %1$,d total memory and %2$,d max %n", Runtime.getRuntime().totalMemory(), Runtime.getRuntime().maxMemory());
		System.out.printf(Locale.US, "Case 1 cost %1$,d memory %n", bestm1);
		System.out.printf(Locale.US, "Case 2 cost %1$,d memory %n", bestm2);
	}
}
