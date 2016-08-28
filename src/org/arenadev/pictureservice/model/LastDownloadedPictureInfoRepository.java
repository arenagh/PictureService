package org.arenadev.pictureservice.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LastDownloadedPictureInfoRepository {

	private static LastDownloadedPictureInfoRepository repository = new LastDownloadedPictureInfoRepository();
	
	private List<PictureInfo> lastDownloadList;
	
	private LastDownloadedPictureInfoRepository() {
		
		lastDownloadList = Collections.synchronizedList(new ArrayList<>());
	}

	public static LastDownloadedPictureInfoRepository getRepository() {
		return repository;
	}
	
	public synchronized List<PictureInfo> getPictureInfoList() {
		
		return new ArrayList<>(lastDownloadList);
	}
	
	public synchronized void addPictureInfo(PictureInfo info) {
		lastDownloadList.add(info);
	}
	
	public synchronized void clear() {
		lastDownloadList.clear();
	}
	
	
}
