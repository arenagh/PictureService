package org.arenadev.pictureservice.model;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

public class PictureComparator {
	
	private static int HASH_PICTURE_SIZE = 16;

	private static PictureComparator comparator = new PictureComparator();
	
	private PictureComparator() {
	}
	
	public static PictureComparator getComparator() {
		return comparator;
	}
	
	public int compare(PictureInfo pInfo1, PictureInfo pInfo2) throws IOException {
		
		if (pInfo1.getPHash() == null || pInfo2.getPHash() == null) {
			return 0xFF;
		}
		
		int bitLen = HASH_PICTURE_SIZE * HASH_PICTURE_SIZE;
		BigInteger diff = pInfo1.getPHash().xor(pInfo2.getPHash());
		return (int) IntStream.range(0, bitLen).filter(i -> diff.testBit(i)).count();
	}

	public BigInteger getPHash(Path imagePath) throws IOException {
		
		System.out.println(imagePath.toString());
		
		BufferedImage srcImg;
		try (InputStream imgInputStream = Files.newInputStream(imagePath)) {
			srcImg = ImageIO.read(imgInputStream);
		} catch (IllegalArgumentException e) { // ?
			return BigInteger.valueOf(0);
		}
		
		if (srcImg == null) {
			System.out.println(String.format("skip:%s", imagePath.toString()));
			return BigInteger.valueOf(0);
		}
		
		BufferedImage dstImg = new BufferedImage(16, 16, BufferedImage.TYPE_BYTE_GRAY);
//		Graphics2D g2 = dstImg.createGraphics();
//		Image img = srcImg.getScaledInstance(HASH_PICTURE_SIZE, HASH_PICTURE_SIZE, Image.SCALE_FAST);
//		g2.drawImage(img, 0, 0, HASH_PICTURE_SIZE, HASH_PICTURE_SIZE, null);
		dstImg.createGraphics().drawImage(srcImg.getScaledInstance(HASH_PICTURE_SIZE, HASH_PICTURE_SIZE, Image.SCALE_FAST), 0, 0, HASH_PICTURE_SIZE, HASH_PICTURE_SIZE, null);

		List<Integer> grayScaleList = new ArrayList<>();
		
		for (int y = 0 ; y < HASH_PICTURE_SIZE ; y++) {
			for (int x = 0 ; x < HASH_PICTURE_SIZE ; x++) {
				grayScaleList.add(dstImg.getRGB(x, y) & 0xFF);
			}
		}
		
		double graySum = grayScaleList.stream().collect(Collectors.averagingInt(i -> i));
		int cutGray = (int) graySum;
		
		BitSet bitSet = new BitSet(256);
		List<Boolean> booleanList = grayScaleList.stream().map(g -> g > cutGray).collect(Collectors.toList());
		IntStream.range(0, booleanList.size()).filter(i -> booleanList.get(i)).forEach(i -> bitSet.set(i));
		
		byte[] bytes = new byte[(bitSet.size() + 7) / 8 + 1];
		byte[] byteArray = bitSet.toByteArray();
		for (int i = 0 ; i < byteArray.length ; i++) {
			bytes[bytes.length - i - 1] = (byte)(byteArray[i] & 0xFF);
		}
		
		return new BigInteger(bytes);
	}

	@Deprecated
	public BigInteger getPHash(FileAccessor accessor) throws IOException {
		
		return getPHash(accessor.getPath());
	}
	
	
}
