package org.arenadev.pictureservice.model;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class PictureReader {
	
	public static final MimetypesFileTypeMap MIME_MAP = new MimetypesFileTypeMap();
	
	public static Mat readPictureFile(Path file) throws IOException {
		
		Mat im = null;
		if (Objects.equals(MIME_MAP.getContentType(file.toFile()), "image/gif")) {
			BufferedImage image = ImageIO.read(file.toFile());
			BufferedImage converted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		    converted.getGraphics().drawImage(image, 0, 0, null);
			byte[] buf = ((DataBufferByte) converted.getRaster().getDataBuffer()).getData();
			im = new Mat(converted.getHeight(), converted.getWidth(), CvType.CV_8UC3);
			im.put(0, 0, buf);
		} else {
			im = Imgcodecs.imread(file.toString());
		}
		if (im == null) {
			System.out.println(String.format("skip:%s", file.toString()));
			return null;
		}
		Imgcodecs.imwrite("/home/mitchey/gifto.png", im);

		return im;
	}


}
