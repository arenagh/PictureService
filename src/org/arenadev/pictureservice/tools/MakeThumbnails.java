package org.arenadev.pictureservice.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.arenadev.pictureservice.model.FileIsDirectoryException;
import org.arenadev.pictureservice.model.PictureInfo;
import org.arenadev.pictureservice.model.PictureInfoRepository;
import org.arenadev.pictureservice.model.PictureRepository;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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
		List<String> folderBaseList = infoRepository.getTagList();

		for (String folderBase : folderBaseList) {
			List<PictureInfo> infoList = infoRepository.getPictureInfos(folderBase);
			for (PictureInfo info : infoList) {
				System.out.println(info.getFileId());
				Path path = picRepository.getPath(info.getFileId());

				try {
					
					Mat im = Imgcodecs.imread(path.toString());

					if (im == null) {
						continue;
					}
					int width = im.width();;
					int height = im.height();

					int scaledWidth = (width >= height) ? SIZE : SIZE * width / height;
					int scaledHeight = (width <= height) ? SIZE : SIZE * height / width;

					Mat thumb = new Mat(scaledHeight, scaledWidth, im.type());
					Imgproc.resize(im, thumb, new Size(scaledHeight, scaledWidth), scaledWidth / width, scaledHeight / height, Imgproc.INTER_LANCZOS4);
					
					Path thumbPath = picRepository.getThumbnailPath(info.getFileId());
					if (!Files.exists(thumbPath.getParent())) {
						Files.createDirectories(thumbPath.getParent());
					}

					Imgcodecs.imwrite(thumbPath.toString(), thumb);
				} catch (Exception e) {
					continue;
				}
			}
		}
	}

}
