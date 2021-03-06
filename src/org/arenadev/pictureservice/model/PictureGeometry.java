package org.arenadev.pictureservice.model;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class PictureGeometry {
	
	private static int HASH_PICTURE_SIZE = 16;

	private static PictureGeometry comparator = new PictureGeometry();
	
	private PictureGeometry() {
	}
	
	public static PictureGeometry getGeometrer() {
		return comparator;
	}
	
	public RectangleSize getPictureSize(Path imagePath) throws PictureSizeException {
		try {
			Mat im = PictureReader.readPictureFile(imagePath);
			RectangleSize result = new RectangleSize(im.width(), im.height());
			im.release();
			return result;
		} catch (IOException | CvException e) {
			throw new PictureSizeException(e);
		}
		
	}
	
	public long getFileSize(Path imagePath) throws FileSizeException {
		try {
			return Files.size(imagePath);
		} catch (IOException e) {
			throw new FileSizeException(e);
		}
	}

	public BigInteger getPHash(Path imagePath) throws PHashException {
		
		Mat im = null;
		try {
			im = PictureReader.readPictureFile(imagePath);
			if (im == null) {
				throw new PHashException("file not read");
			}
		} catch (IOException e) {
			throw new PHashException("file read error", e);
		}
		
		try {
			Mat small = new Mat(HASH_PICTURE_SIZE, HASH_PICTURE_SIZE, CvType.CV_8UC3);
			Imgproc.resize(im, small, new Size(16, 16));
			im.release();
			
			Mat dst = new Mat(HASH_PICTURE_SIZE, HASH_PICTURE_SIZE, CvType.CV_8UC1);
			Imgproc.cvtColor(small, dst, Imgproc.COLOR_RGB2GRAY);
			small.release();

			List<Integer> grayScaleList = new ArrayList<>();
			
			for (int y = 0 ; y < HASH_PICTURE_SIZE ; y++) {
				for (int x = 0 ; x < HASH_PICTURE_SIZE ; x++) {
					grayScaleList.add(((int) dst.get(y, x)[0]) & 0xFF);
				}
			}
			
			dst.release();
			
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
		} catch (CvException e) {
			throw new PHashException(e);
		}
		
	}
	
}
