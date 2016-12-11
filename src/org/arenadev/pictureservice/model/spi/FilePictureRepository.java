package org.arenadev.pictureservice.model.spi;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.arenadev.pictureservice.model.PictureRepository;

public class FilePictureRepository implements PictureRepository {

	private static final PictureRepository pictureRepository = new FilePictureRepository(System.getenv("PICTURE_ROOT"), System.getenv("PICTURE_THUMBNAIL_ROOT"));
	
	private static final PictureRepository tmpPictureRepository = new FilePictureRepository(System.getenv("PICTURE_TMP_ROOT"), System.getenv("PICTURE_TMP_THUMBNAIL_ROOT"));
	
	private Path root;
	private Path thumbRoot;
	
	private FilePictureRepository(String pictureRoot, String thumbnailRoot) {
		root = FileSystems.getDefault().getPath(pictureRoot);
		thumbRoot = FileSystems.getDefault().getPath(thumbnailRoot);
	}

	public static PictureRepository getRepository() {
		return pictureRepository;
	}
	
	public static PictureRepository getTmpRepository() {
		return tmpPictureRepository;
	}
	
	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureRepository#getPath(java.lang.String)
	 */
	@Override
	public Path getPath(String id) {
		return root.resolve(id);
	}
	
	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureRepository#getThumbnailPath(java.lang.String)
	 */
	@Override
	public Path getThumbnailPath(String id) {
		String thumbPath = id.substring(0, id.lastIndexOf('.')) + ".png";
		return thumbRoot.resolve(thumbPath);
	}
	
	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureRepository#getRoot()
	 */
	@Override
	public Path getRoot() {
		return root;
	}
	
	public static String getFileId(Path path) {
		String filename = path.getFileName().toString();
		String folderName = path.getParent().getFileName().toString();
		
		return String.format("%s/%s", folderName, filename);
	}

}
