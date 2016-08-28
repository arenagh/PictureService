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
		progress = 500;
		genMetaFiles(tmpInfoRepository, tmpPicRepository);
		progress = 1000;
		
		synchronized (this) {
			running = false;
		}
	}
	
	private void genMetaFiles(PictureInfoRepository infoRepo, PictureRepository picRepo) {
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
			progress += 500;
			return;
		}
		int tick = 500 / fileCount;
		int unitFiles = fileCount / 500;
		PictureComparator comparator = PictureComparator.getComparator();
		int rest = unitFiles;
		for (Map.Entry<String, List<PictureInfo>> entry : pictureInfoMapFromImage.entrySet()) {
			try {
				String tag = entry.getKey();
				for (PictureInfo pInfo : entry.getValue()) {
					pInfo.setPHash(comparator.getPHash(picRepo.getPath(pInfo.getFileId())));
					infoRepo.addPictureInfo(tag, pInfo);
					// progress advanced
					if (tick > 0) {
						progress += tick;
					} else {
						rest--;
						if (rest <= 0) {
							progress++;
							rest = unitFiles;
						}
					}
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
		try {
			attrs = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
			time = attrs.creationTime().toInstant();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return PictureInfo.getPictureInfo(fileId, null, time, time, tag);
	}
}