package org.arenadev.pictureservice.tools;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

public class GetGrayScale {

	public static void main(String[] args) throws IOException {
		
		Path src = FileSystems.getDefault().getPath(args[0]);
		Path dst = src.getParent().resolve(src.getFileName().toString() + "_gray.png");
		
		BufferedImage srcImg = ImageIO.read(Files.newInputStream(src));
		
		BufferedImage dstImg = new BufferedImage(16, 16, BufferedImage.TYPE_BYTE_GRAY);
		dstImg.createGraphics().drawImage(srcImg.getScaledInstance(16, 16, Image.SCALE_FAST), 0, 0, 16, 16, null);
		
		ImageIO.write(dstImg, "png", Files.newOutputStream(dst));

	}

}
