package org.arenadev.pictureservice.tools;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;

import org.arenadev.pictureservice.model.FileIsDirectoryException;
import org.arenadev.pictureservice.model.PictureInfo;
import org.arenadev.pictureservice.model.PictureInfoRepository;
import org.arenadev.pictureservice.model.PictureRepository;

public class MakeThumbnails {

	private static final int SIZE = 160;

	public static void main(String[] args) throws IOException, FileIsDirectoryException {

		PictureInfoRepository infoRepository = PictureInfoRepository.getRepository();
		PictureRepository picRepository = PictureRepository.getRepository();
		generateThumbnails(infoRepository, picRepository);

		PictureInfoRepository tmpInfoRepository = PictureInfoRepository.getTmpRepository();
		PictureRepository tmpPicRepository = PictureRepository.getTmpRepository();
		generateThumbnails(tmpInfoRepository, tmpPicRepository);

	}

	private static void generateThumbnails(PictureInfoRepository infoRepository, PictureRepository picRepository)
			throws IOException, FileNotFoundException, FileIsDirectoryException {
		List<String> folderBaseList = infoRepository.getFolerBaseNames();

		for (String folderBase : folderBaseList) {
			List<PictureInfo> infoList = infoRepository.getPictureInfoList(folderBase);
			for (PictureInfo info : infoList) {
				System.out.println(info.getFileId());
				Path path = picRepository.getPath(info.getFileId());

				try {
					BufferedImage image = ImageIO.read(path.toFile());
					if (image == null) {
						continue;
					}
					int width = image.getWidth();
					int height = image.getHeight();

					int scaledWidth = (width >= height) ? SIZE : SIZE * width / height;
					int scaledHeight = (width <= height) ? SIZE : SIZE * height / width;

					BufferedImage thumb;
					thumb = new BufferedImage(scaledWidth, scaledHeight, image.getType());
					thumb.getGraphics().drawImage(image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH), 0, 0, scaledWidth, scaledHeight, null);

					Path thumbPath = picRepository.getThumbnailPath(info.getFileId());
					if (!Files.exists(thumbPath.getParent())) {
						Files.createDirectories(thumbPath.getParent());
					}

					ImageIO.write(thumb, "png", thumbPath.toFile());
				} catch (Exception e) {
					continue;
				}
			}
		}
	}

}
