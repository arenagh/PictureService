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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.arenadev.pictureservice.model.spi.FilePictureInfoRepository;
import org.arenadev.pictureservice.model.spi.FilePictureRepository;
import org.opencv.core.CvException;

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
		infoRepository = FilePictureInfoRepository.getRepository();
		picRepository = FilePictureRepository.getRepository();

		tmpInfoRepository = FilePictureInfoRepository.getTmpRepository();
		tmpPicRepository = FilePictureRepository.getTmpRepository();
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
	
	public void migrate() {
		synchronized (this) {
			if (running) {
				return;
			}
			running = true;
			progress = 0;
		}
		
		migrateMetaFiles(infoRepository, picRepository, false);
		progress = FULL_PROGRESS_PER_REPO;
		migrateMetaFiles(tmpInfoRepository, tmpPicRepository, true);
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
					try {
						pInfo.setPHash(PictureGeometry.getGeometrer().getPHash(picRepo.getPath(pInfo.getFileId())));
						if (pInfo.getTagList().size() == 0) {
							pInfo.addTag(tag);
						}
						infoRepo.addPictureInfo(tag, pInfo);
					} catch (CvException e) {
						System.out.println(String.format("invalid file(fileID):%s skipped...", pInfo.getFileId()));
					}
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
	
	private void migrateMetaFiles(PictureInfoRepository infoRepo, PictureRepository picRepo, boolean tmp) {
		int baseProgress = progress;
		List<String> tagList = infoRepo.getTagList();
		Map<String, Map<String, PictureInfo>> pictureInfoMap = new HashMap<>();
		Map<String, List<Path>> pathMap = new HashMap<>();
		for (String tag : tagList) {
			try {
				infoRepo.loadPictureInfoList(tag);
				pictureInfoMap.put(tag, infoRepo.getPictureInfos(tag).stream().collect(Collectors.toMap(PictureInfo::getFileId, Function.identity())));
				
				List<Path> dirList = getChildList(Files.newDirectoryStream(picRepo.getRoot(), tag + "*"));
				List<Path> pathList = new ArrayList<>();
				
				for (Path dir : dirList) {
					pathList.addAll(getChildList(Files.newDirectoryStream(dir)).stream().filter(p -> PictureInfo.isPictureFile(p.toString())).collect(Collectors.toList()));
				}
				pathMap.put(tag, pathList);
			} catch (IOException e) {
			}
		}
		
		int fileCount = pictureInfoMap.entrySet().stream().mapToInt(e -> e.getValue().size()).sum();
		if (fileCount == 0) {
			progress += FULL_PROGRESS_PER_REPO;
			return;
		}
		int doneCount = 0;
		
		for (Map.Entry<String, List<Path>> entry : pathMap.entrySet()) {
			String tag = entry.getKey();
			Map<String, PictureInfo> tagPictureInfoMap = pictureInfoMap.get(tag);
			try {
				for (Path path : entry.getValue()) {
					String fileId = FilePictureRepository.getFileId(path);
					PictureInfo newInfo = null;
					
					try {
						PictureGeometry geom = PictureGeometry.getGeometrer();
						RectangleSize picSize = geom.getPictureSize(path);
						long fileSize = geom.getFileSize(path);
						
						if (tagPictureInfoMap.containsKey(fileId)) {
							PictureInfo info = tagPictureInfoMap.get(fileId);
							newInfo = info.patch(null, null, null, picSize, fileSize, null, tmp);
							if (newInfo.getTagList().size() == 0) {
								newInfo.addTag(tag);
							}
							infoRepo.removePictureInfo(tag, info);
						} else {
							newInfo = genPictureInfo(path, tag);
						}
						newInfo.setPHash(geom.getPHash(path));
						infoRepo.addPictureInfo(tag, newInfo);
					} catch (CvException e) {
						System.out.println(String.format("invalid file(fileID):%s skipped...", fileId));
					}
					
					doneCount++;
					progress = baseProgress + (FULL_PROGRESS_PER_REPO * doneCount) / fileCount;
				}

				infoRepo.store(tag);
			} catch(IOException e) {
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

		String fileId = FilePictureRepository.getFileId(path);

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
		
		return PictureInfo.getPictureInfo(fileId, null, time, time, picSize, size, null, tag);
	}

	public void putTags() {
		
		addTags(infoRepository);
		addTags(tmpInfoRepository);
		
		
	}

	private void addTags(PictureInfoRepository infoRepo) {

		List<String> tagList = infoRepo.getTagList();
		for (String tag : tagList) {
			try {
				infoRepo.loadPictureInfoList(tag);
				infoRepo.getPictureInfos(tag).stream().filter(p -> p.getTagList().size() == 0).forEach(p -> p.addTag(tag));
				infoRepo.store(tag);
			} catch (IOException e) {
			}
		}

	}

	public void putTmp() {
		
		setTmp(infoRepository, false);
		setTmp(tmpInfoRepository, true);
		
	}

	private void setTmp(PictureInfoRepository infoRepo, boolean tmp) {

		List<String> tagList = infoRepo.getTagList();
		for (String tag : tagList) {
			try {
				infoRepo.loadPictureInfoList(tag);
				infoRepo.getPictureInfos(tag).stream().filter(p -> p.getTagList().size() == 0).forEach(p -> p.setTemporary(tmp));
				infoRepo.store(tag);
			} catch (IOException e) {
			}
		}

	}
}