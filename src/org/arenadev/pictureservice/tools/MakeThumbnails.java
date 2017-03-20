package org.arenadev.pictureservice.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import org.arenadev.pictureservice.model.FileIsDirectoryException;
import org.arenadev.pictureservice.model.PathGenerator;
import org.arenadev.pictureservice.model.PictureInfo;
import org.arenadev.pictureservice.model.PictureInfoRepository;
import org.arenadev.pictureservice.model.PictureReader;
import org.arenadev.pictureservice.repository.file.FilePictureInfoRepository;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

@Deprecated
public class MakeThumbnails {

	private static final int SIZE = 160;

	public static void main(String[] args) throws IOException, FileIsDirectoryException {

		PictureInfoRepository infoRepository = FilePictureInfoRepository.getRepository();
		generateThumbnails(infoRepository);

		PictureInfoRepository tmpInfoRepository = FilePictureInfoRepository.getTmpRepository();
		generateThumbnails(tmpInfoRepository);

	}

	private static void generateThumbnails(PictureInfoRepository infoRepository)
			throws IOException, FileNotFoundException, FileIsDirectoryException {
		List<String> folderBaseList = infoRepository.getTagList();

		for (String folderBase : folderBaseList) {
			Collection<PictureInfo> infoList = infoRepository.getPictureInfos(folderBase).values();
			for (PictureInfo info : infoList) {
				System.out.println(info.getFileId());
				PathGenerator pGen = new PathGenerator(info.getFileId(), info.isTemporary());
				Path path = pGen.getPath();

				try {
					
					Mat im = PictureReader.readPictureFile(path);

					if (im == null) {
						continue;
					}
					int width = im.width();;
					int height = im.height();

					int scaledWidth = (width >= height) ? SIZE : SIZE * width / height;
					int scaledHeight = (width <= height) ? SIZE : SIZE * height / width;

					Mat thumb = new Mat(scaledHeight, scaledWidth, im.type());
					Imgproc.resize(im, thumb, new Size(scaledHeight, scaledWidth), scaledWidth / width, scaledHeight / height, Imgproc.INTER_LANCZOS4);
					im.release();
					
					Path thumbPath = pGen.getThumbnailPath();
					if (!Files.exists(thumbPath.getParent())) {
						Files.createDirectories(thumbPath.getParent());
					}

					Imgcodecs.imwrite(thumbPath.toString(), thumb);
					thumb.release();
				} catch (Exception e) {
					continue;
				}
			}
		}
	}

}
