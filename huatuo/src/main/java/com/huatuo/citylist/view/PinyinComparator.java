package com.huatuo.citylist.view;

import java.util.Comparator;

import com.huatuo.citylist.Content;

public class PinyinComparator implements Comparator<Content> {

	public int compare(Content o1, Content o2) {
		if (o1.getInitialCharacter().equals("@")
				|| o2.getInitialCharacter().equals("#")) {
			return -1;
		} else if (o1.getInitialCharacter().equals("#")
				|| o2.getInitialCharacter().equals("@")) {
			return 1;
		} else {
			return o1.getInitialCharacter().compareTo(o2.getInitialCharacter());
		}
	}

}
