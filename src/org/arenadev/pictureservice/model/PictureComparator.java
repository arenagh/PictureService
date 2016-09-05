package org.arenadev.pictureservice.model;

import java.math.BigInteger;
import java.util.stream.IntStream;

public class PictureComparator {
	
	private static int HASH_PICTURE_SIZE = 16;

	private static PictureComparator comparator = new PictureComparator();
	
	private PictureComparator() {
	}
	
	public static PictureComparator getComparator() {
		return comparator;
	}
	
	public int compare(PictureInfo pInfo1, PictureInfo pInfo2) {
		
		if (pInfo1.getPHash() == null || pInfo2.getPHash() == null) {
			return 0xFF;
		}
		
		int bitLen = HASH_PICTURE_SIZE * HASH_PICTURE_SIZE;
		BigInteger diff = pInfo1.getPHash().xor(pInfo2.getPHash());
		return (int) IntStream.range(0, bitLen).filter(i -> diff.testBit(i)).count();
	}
	
}
