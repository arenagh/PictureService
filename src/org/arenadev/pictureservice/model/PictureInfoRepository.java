package org.arenadev.pictureservice.model;

import java.io.IOException;
import java.util.List;

public interface PictureInfoRepository {

	void loadPictureInfoList(String tag) throws IOException;

	List<PictureInfo> getPictureInfos(String tag);

	List<String> getTagList();

	void addPictureInfo(String tag, PictureInfo info);

	void store(String tag) throws IOException;

	void removePictureInfo(String tag, PictureInfo info);

}