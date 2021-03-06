package org.arenadev.pictureservice.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class PictureMagnifier {

	private static final int SIZE = 160;

	private static PictureMagnifier maker = new PictureMagnifier();
	
	private PictureMagnifier() {
		
	}
	
	public static PictureMagnifier getMaker() {
		return maker;
	}
	
	public Mat magnify(Path path, Integer targetWidth, Integer targetHeight) throws MagnifyException {
		
		Mat im = null;
		try {
			im = PictureReader.readPictureFile(path);
		} catch (IOException e) {
			throw new MagnifyException("file read fail", e, path);
		}
		if (im == null) {
			throw new MagnifyException("file not read", path);
		}

		try {
			if ((targetWidth == null) && (targetHeight == null)) {
				return im;
			}

			int width = im.width();
			int height = im.height();
			
			double scale;
			if (targetWidth == null) {
				scale = (float) targetHeight / (float) height;
			} else if (targetHeight == null) {
				scale = (float) targetWidth / (float) width;
			} else {
				scale = Math.min((float) targetWidth / (float) width, (float) targetHeight / (float) height);
			}
			
			double scaledWidth = width * scale;
			double scaledHeight = height * scale;
			
			Mat scaledIm = new Mat((int) scaledHeight, (int) scaledWidth, im.type());
			Imgproc.resize(im, scaledIm, new Size(scaledWidth, scaledHeight), scale, scale, Imgproc.INTER_LANCZOS4);
			im.release();
			
			return scaledIm;
		} catch (CvException e) {
			throw new MagnifyException(e, path);
		}
		
	}

	public Path makeThumbnail(PictureInfo info, Path picPath, Path thumbPath) throws MagnifyException {

		Mat thumb = magnify(picPath, SIZE, SIZE);
		
		if (!Files.exists(thumbPath.getParent())) {
			try {
				Files.createDirectories(thumbPath.getParent());
			} catch (IOException e) { // directory creation failure
				throw new MagnifyException("directory creation fail", e, picPath, thumbPath);
			}
		}
		
		Imgcodecs.imwrite(thumbPath.toString(), thumb);
		thumb.release();
		
		return thumbPath;
		
	}

	public BufferedImage magnifyToBufferedImage(Path path, Integer targetWidth, Integer targetHeight) throws MagnifyException {

		Mat scaledIm = magnify(path, targetWidth, targetHeight);
		Mat converted = new Mat(targetHeight, targetWidth, CvType.CV_8UC3);
		Imgproc.cvtColor(scaledIm, converted, Imgproc.COLOR_RGB2BGR);
		
        byte[] data = new byte[converted.width() * converted.height() * (int)converted.elemSize()];
        converted.get(0, 0, data);

        BufferedImage result = new BufferedImage(converted.width(), converted.height(), BufferedImage.TYPE_3BYTE_BGR);
        result.getRaster().setDataElements(0, 0, converted.width(), scaledIm.height(), data);
        scaledIm.release();
        
        return result;

	}
	
}
