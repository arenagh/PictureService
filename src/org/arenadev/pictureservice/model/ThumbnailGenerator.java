package org.arenadev.pictureservice.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.arenadev.pictureservice.model.spi.FilePictureInfoRepository;
import org.arenadev.pictureservice.model.spi.FilePictureRepository;
import org.opencv.core.CvException;

public class ThumbnailGenerator {
	
	private static ThumbnailGenerator thumbnailGenerator = new ThumbnailGenerator();
	
	private PictureInfoRepository infoRepository;
	private PictureRepository picRepository;

	private PictureInfoRepository tmpInfoRepository;
	private PictureRepository tmpPicRepository;
	
	private boolean running = false;
	private int progress = 0;
	
	private ThumbnailGenerator() {
		infoRepository = FilePictureInfoRepository.getRepository();
		picRepository = FilePictureRepository.getRepository();

		tmpInfoRepository = FilePictureInfoRepository.getTmpRepository();
		tmpPicRepository = FilePictureRepository.getTmpRepository();
	}

	public static ThumbnailGenerator getGenerator() {
		return thumbnailGenerator;
	}

	public synchronized boolean isGenerating() {
		return running;
	}

	public void generate() throws FileIsDirectoryException, IOException {
		synchronized (this) {
			if (running) {
				return;
			}
			running = true;
			progress = 0;
		}
		
		List<PictureInfo> totalInfo = new ArrayList<>();
		for (String tag : infoRepository.getTagList()) {
			totalInfo.addAll(infoRepository.getPictureInfos(tag));
		}
		for (String tag : tmpInfoRepository.getTagList()) {
			totalInfo.addAll(tmpInfoRepository.getPictureInfos(tag));
		}
		int totalCount = totalInfo.size();
		
		int count = 0;
		for (PictureInfo info : totalInfo) {
			try {
				PictureMagnifier.getMaker().makeThumbnail(info, picRepository);
			} catch (CvException e) {
				System.out.println(String.format("Thumbnail generation failed(fileID):%s", info.getFileId()));
			}
			count++;
			synchronized (this) {
				progress = count / totalCount;
			}
		}
		
		synchronized (this) {
			running = false;
		}
	}
	
	public synchronized int getProgress() {
		return progress;
	}

}
