package org.arenadev.pictureservice.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ThumbnailGenerator {
	
	private static ThumbnailGenerator thumbnailGenerator = new ThumbnailGenerator();
	
	@Inject
	private RepositoryFactory repoFactory;
	
	private PictureInfoRepository infoRepository;

	private PictureInfoRepository tmpInfoRepository;
	
	private boolean running = false;
	private int progress = 0;
	
	private ThumbnailGenerator() {
		infoRepository = repoFactory.getPictureInfoRepository();

		tmpInfoRepository = repoFactory.getTmpPictureInfoRepository();
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
			totalInfo.addAll(infoRepository.getPictureInfos(tag).values());
		}
		for (String tag : tmpInfoRepository.getTagList()) {
			totalInfo.addAll(tmpInfoRepository.getPictureInfos(tag).values());
		}
		int totalCount = totalInfo.size();
		
		int count = 0;
		for (PictureInfo info : totalInfo) {
			try {
				PathGenerator pGen = new PathGenerator(info.getFileId(), info.isTemporary());
				PictureMagnifier.getMaker().makeThumbnail(info, pGen.getPath(), pGen.getThumbnailPath());
				// TODO store picture file info
			} catch (MagnifyException e) {
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
