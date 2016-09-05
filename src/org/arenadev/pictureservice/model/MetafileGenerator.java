package org.arenadev.pictureservice.model;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MetafileGenerator {

	private static final int FULL_PROGRESS_PER_REPO = 500;

	private static MetafileGenerator metafileGenerator = new MetafileGenerator();
	
	private PictureInfoRepository infoRepository;
	private PictureRepository picRepository;

	private PictureInfoRepository tmpInfoRepository;
	private PictureRepository tmpPicRepository;
	
	private boolean running = false;
	private volatile int progress = 0;
	
	private MetafileGenerator() {
		infoRepository = PictureInfoRepository.getRepository();
		picRepository = PictureRepository.getRepository();

		tmpInfoRepository = PictureInfoRepository.getTmpRepository();
		tmpPicRepository = PictureRepository.getTmpRepository();
	}

	public static MetafileGenerator getGenerator() {
		return metafileGenerator;
	}

	public synchronized boolean isGenerating() {
		return running;
	}

	public void generate() {
		synchronized (this) {
			if (running) {
				return;
			}
			running = true;
			progress = 0;
		}
		
		genMetaFiles(infoRepository, picRepository);
		progress = FULL_PROGRESS_PER_REPO;
		genMetaFiles(tmpInfoRepository, tmpPicRepository);
		progress = FULL_PROGRESS_PER_REPO * 2;
		
		synchronized (this) {
			running = false;
		}
	}
	
	private void genMetaFiles(PictureInfoRepository infoRepo, PictureRepository picRepo) {
		int baseProgress = progress;
		List<String> tagList = infoRepo.getTagList();
		Map<String, List<PictureInfo>> pictureInfoMapFromMeta = new HashMap<>();
		Map<String, List<PictureInfo>> pictureInfoMapFromImage = new HashMap<>();
		for (String tag : tagList) {
			try {
				infoRepo.loadPictureInfoList(tag);
				List<PictureInfo> pictureInfoListFromMeta = infoRepo.getPictureInfos(tag);
				
				List<Path> dirList = getChildList(Files.newDirectoryStream(picRepo.getRoot(), tag + "*"));
				
				List<PictureInfo> pictureInfoListFromImage = new ArrayList<>();
				for (Path dir : dirList) {
					pictureInfoListFromImage.addAll(getChildList(Files.newDirectoryStream(dir)).stream().filter(p -> PictureInfo.isPictureFile(p.toString())).map(p -> genPictureInfo(p, tag)).filter(p -> !pictureInfoListFromMeta.contains(p)).collect(Collectors.toList()));
					System.out.println(String.format("dir:%s, file count:%d", dir.toString(), pictureInfoListFromImage.size()));
				}
				pictureInfoMapFromMeta.put(tag, pictureInfoListFromMeta);
				pictureInfoMapFromImage.put(tag, pictureInfoListFromImage);
				
			} catch (IOException e) {
			}
		}
		
		int fileCount = pictureInfoMapFromImage.entrySet().stream().map(e -> e.getValue().size()).reduce((i, j) -> i + j).orElse(0);
		if (fileCount == 0) {
			progress += FULL_PROGRESS_PER_REPO;
			return;
		}
		int doneCount = 0;
		for (Map.Entry<String, List<PictureInfo>> entry : pictureInfoMapFromImage.entrySet()) {
			try {
				String tag = entry.getKey();
				for (PictureInfo pInfo : entry.getValue()) {
					pInfo.setPHash(PictureGeometry.getGeometrer().getPHash(picRepo.getPath(pInfo.getFileId())));
					infoRepo.addPictureInfo(tag, pInfo);
					// progress advanced
					doneCount++;
					progress = baseProgress + (FULL_PROGRESS_PER_REPO * doneCount) / fileCount;
				}
				List<PictureInfo> result = new ArrayList<>();
				result.addAll(pictureInfoMapFromMeta.get(tag));
				result.addAll(entry.getValue());
				
				result.forEach(p -> { // tag rewrite
					p.clearTags();
					p.addTag(tag);
				});

				infoRepo.store(tag);
			} catch (IOException e) {
			}
		}
	}
	
	public synchronized int getProgress() {
		return progress;
	}
	
	private List<Path> getChildList(DirectoryStream<Path> dir) {
		List<Path> children = new ArrayList<>();
		dir.forEach(p -> children.add(p));
		
		return children;
	}
	
	private PictureInfo genPictureInfo(Path path, String tag) {

		String filename = path.getFileName().toString();
		String folderName = path.getParent().getFileName().toString();
		
		String fileId = String.format("%s/%s", folderName, filename);

		Instant time = null;
		BasicFileAttributes attrs;
		RectangleSize picSize = null;
		long size = 0;
		try {
			attrs = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
			time = attrs.creationTime().toInstant();
			
			PictureGeometry geom = PictureGeometry.getGeometrer();
			picSize = geom.getPictureSize(path);
			size = geom.getFileSize(path);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return PictureInfo.getPictureInfo(fileId, null, time, time, picSize, size, tag);
	}
}