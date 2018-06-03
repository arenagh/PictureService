package org.arenadev.pictureservice.model;

import java.nio.file.Path;

public class MagnifyException extends Exception {

	private static final long serialVersionUID = -7349521989738609663L;

	private Path imagePath;
	private Path thumbnailPath;
	
	public MagnifyException(Path image) {
		super();
		imagePath = image;
		thumbnailPath = null;
	}

	public MagnifyException(Path image, Path thumbnail) {
		super();
		imagePath = image;
		thumbnailPath = thumbnail;
	}

	public MagnifyException(String message, Path image) {
		super(message);
		imagePath = image;
		thumbnailPath = null;
	}

	public MagnifyException(String message, Path image, Path thumbnail) {
		super(message);
		imagePath = image;
		thumbnailPath = thumbnail;
	}

	public MagnifyException(Throwable cause, Path image) {
		super(cause);
		imagePath = image;
		thumbnailPath = null;
	}

	public MagnifyException(Throwable cause, Path image, Path thumbnail) {
		super(cause);
		imagePath = image;
		thumbnailPath = thumbnail;
	}

	public MagnifyException(String message, Throwable cause, Path image) {
		super(message, cause);
		imagePath = image;
		thumbnailPath = null;
	}

	public MagnifyException(String message, Throwable cause, Path image, Path thumbnail) {
		super(message, cause);
		imagePath = image;
		thumbnailPath = thumbnail;
	}

	public Path getImagePath() {
		return imagePath;
	}

	public Path getThumbnailPath() {
		return thumbnailPath;
	}

}
