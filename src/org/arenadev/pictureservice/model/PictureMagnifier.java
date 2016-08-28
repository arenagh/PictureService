package org.arenadev.pictureservice.model;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

public class PictureMagnifier {

	private static final int SIZE = 160;

	private static PictureMagnifier maker = new PictureMagnifier();
	
	private PictureMagnifier() {
		
	}
	
	public static PictureMagnifier getMaker() {
		return maker;
	}
	
	public BufferedImage magnify(Path path, Integer targetWidth, Integer targetHeight) throws FileIsDirectoryException, IOException {
		
		BufferedImage image = ImageIO.read(path.toFile());
		if (image == null) {
			return null;
		}

		if ((targetWidth == null) && (targetHeight == null)) {
			return image;
		}

		int width = image.getWidth();
		int height = image.getHeight();
		
		double scale;
		if (targetWidth == null) {
			scale = (float) targetHeight / (float) height;
		} else if (targetHeight == null) {
			scale = (float) targetWidth / (float) width;
		} else {
			scale = Math.min((float) targetWidth / (float) width, (float) targetHeight / (float) height);
		}
		
		BufferedImage scaledImage = new BufferedImage((int) (width * scale), (int) (height * scale), image.getType());
		AffineTransformOp atOp = new AffineTransformOp(AffineTransform.getScaleInstance(scale, scale), AffineTransformOp.TYPE_BICUBIC);
		atOp.filter(image, scaledImage);
		
		return scaledImage;
		
	}

	@Deprecated
	public BufferedImage magnify(FileAccessor accessor, Integer targetWidth, Integer targetHeight) throws FileIsDirectoryException, IOException {
		
		return magnify(accessor.getPath(), targetWidth, targetHeight);
	}

	public void makeThumbnail(PictureInfo info, PictureRepository pRepository) throws FileIsDirectoryException, IOException {

		BufferedImage thumb = magnify(pRepository.getPath(info.getFileId()), SIZE, SIZE);
		Path thumbPath = pRepository.getThumbnailPath(info.getFileId());
		
		if (!Files.exists(thumbPath.getParent())) {
			Files.createDirectories(thumbPath.getParent());
		}
		
		ImageIO.write(thumb, "png", thumbPath.toFile());
		
	}

}
