package org.arenadev.pictureservice.model;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class PictureRepository {

	private static final PictureRepository pictureRepository = new PictureRepository(System.getenv("PICTURE_ROOT"), System.getenv("PICTURE_THUMBNAIL_ROOT"));
	
	private static final PictureRepository tmpPictureRepository = new PictureRepository(System.getenv("PICTURE_TMP_ROOT"), System.getenv("PICTURE_TMP_THUMBNAIL_ROOT"));
	
	private Path root;
	private Path thumbRoot;
	
	private PictureRepository(String pictureRoot, String thumbnailRoot) {
		root = FileSystems.getDefault().getPath(pictureRoot);
		thumbRoot = FileSystems.getDefault().getPath(thumbnailRoot);
	}

	public static PictureRepository getRepository() {
		return pictureRepository;
	}
	
	public static PictureRepository getTmpRepository() {
		return tmpPictureRepository;
	}
	
	public Path getPath(String id) {
		return root.resolve(id);
	}
	
	public Path getThumbnailPath(String id) {
		String thumbPath = id.substring(0, id.lastIndexOf('.')) + ".png";
		return thumbRoot.resolve(thumbPath);
	}
	
	public Path getRoot() {
		return root;
	}

}
