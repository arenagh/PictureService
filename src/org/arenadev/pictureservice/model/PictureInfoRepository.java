package org.arenadev.pictureservice.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PictureInfoRepository {

	void loadPictureInfoList(String tag) throws IOException;

	Map<String, PictureInfo> getPictureInfos(String tag);

	List<String> getTagList();

	void addPictureInfo(String tag, String id, PictureInfo info);

	void store(String tag) throws IOException;

	void removePictureInfo(String tag, PictureInfo info);

	boolean isTemporary();

}