package com.sgrif.poker;

import java.util.Comparator;

public class CardCompareLow implements Comparator<Card> {

	@Override
	public int compare(Card o1, Card o2) {
		int v = o1.getValue();
		if(v == 14) v = 1;
		int v2 = o2.getValue();
		if(v2 == 14) v2 = 1;
		return v - v2;
	}

}
