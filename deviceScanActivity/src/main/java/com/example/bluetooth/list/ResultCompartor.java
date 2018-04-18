package com.example.bluetooth.list;

import java.util.Comparator;

public class ResultCompartor implements Comparator<Result> {
	@Override
	public int compare(Result o1, Result o2) {
		if (o1.getDate().compareTo(o2.getDate())==0) {
			
			return o1.getTime().compareTo(o2.getTime());
		}
		else {
			return o1.getDate().compareTo(o2.getDate());
		}
	}

}
