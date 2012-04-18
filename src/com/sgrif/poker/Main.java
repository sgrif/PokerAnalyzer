package com.sgrif.poker;

public class Main {

	public static void main(String[] args) {
		Game game = new Game(1, "AsAc", "KcKd3h2c6s");
		System.out.println(game.enumerateAll());
	}
}
