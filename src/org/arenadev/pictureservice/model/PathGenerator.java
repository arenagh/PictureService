package org.arenadev.pictureservice.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class PathGenerator {
	
	private static Path PICTURE_ROOT = FileSystems.getDefault().getPath(System.getenv("PICTURE_ROOT"));
	private static Path PICTURE_THUMBNAIL_ROOT = FileSystems.getDefault().getPath(System.getenv("PICTURE_THUMBNAIL_ROOT"));

	private static Path PICTURE_TMP_ROOT = FileSystems.getDefault().getPath(System.getenv("PICTURE_TMP_ROOT"));
	private static Path PICTURE_TMP_THUMBNAIL_ROOT = FileSystems.getDefault().getPath(System.getenv("PICTURE_TMP_THUMBNAIL_ROOT"));
	
	private Path root;
	private Path thumbRoot;
	
	private String fileId;
	private String id;
	private Path path;
	private Path thumbnailPath;
	
	public PathGenerator(String folder, String urlStr, boolean tmp) throws UnsupportedEncodingException {
		
		String decodedLine = URLDecoder.decode(urlStr, "UTF-8");
		int startPos = decodedLine.lastIndexOf('/') + 1;
		int endPos = decodedLine.indexOf('?', startPos);
		String filename = endPos < 0 ? decodedLine.substring(startPos) : decodedLine.substring(startPos, endPos);

		setParam(String.format("%s/%s", folder, filename), tmp);

	}
	
	public PathGenerator(String fId, boolean tmp) {
		
		setParam(fId, tmp);
	}
	
	private void setParam(String fId, boolean tmp) {
		
		root = getRoot(tmp);
		thumbRoot = getThumbnailRoot(tmp);
		
		fileId = fId;
		id = fileId.replaceAll(" ", "").replaceAll("\"", "").replaceAll("/", "");
		
		path = root.resolve(fileId);
		
		String thumbPathStr = fileId.substring(0, fileId.lastIndexOf('.')) + ".png";
		thumbnailPath = thumbRoot.resolve(thumbPathStr);
		
	}

	public String getFileID() {
		return fileId;
	}
	
	public String getID() {
		return id;
	}
	
	public Path getPath() {
		return path;
	}
	
	public Path getThumbnailPath() {
		return thumbnailPath;
	}
	
	public static Path getRoot(boolean tmp) {
		return tmp ? PICTURE_TMP_ROOT : PICTURE_ROOT;
	}
	
	public static Path getThumbnailRoot(boolean tmp) {
		return tmp ? PICTURE_TMP_THUMBNAIL_ROOT : PICTURE_THUMBNAIL_ROOT;
	}

}
