package org.arenadev.pictureservice.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LastDownloadedPictureInfoRepository {

	private static LastDownloadedPictureInfoRepository repository = new LastDownloadedPictureInfoRepository();
	
	private Map<String, PictureInfo> lastDownloads;
	
	private LastDownloadedPictureInfoRepository() {
		
		lastDownloads = Collections.synchronizedMap(new HashMap<>());
	}

	public static LastDownloadedPictureInfoRepository getRepository() {
		return repository;
	}
	
	public synchronized Map<String, PictureInfo> getPictureInfoList() {
		return new HashMap<>(lastDownloads);
	}
	
	public synchronized void addPictureInfo(String id, PictureInfo info) {
		lastDownloads.put(id, info);
	}
	
	public synchronized void clear() {
		lastDownloads.clear();
	}
	
	
}
