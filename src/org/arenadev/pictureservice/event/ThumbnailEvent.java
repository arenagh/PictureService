package org.arenadev.pictureservice.event;

import java.nio.file.Path;
import java.time.Instant;

public class ThumbnailEvent extends Event {
	
	private Path imagePath;
	
	private Path thumbnailPath;
	
	private ThumbnailResult result;
	
	private ThumbnailEvent(Instant start, Instant end, Exception exception) {
		super(EventType.THUMBNAIL, start, end, exception);
	}
	
	public static ThumbnailEvent getThumbnailEvent(Path image, Path thumbnail, Instant start, Instant end, ThumbnailResult r, Exception cause) {
		ThumbnailEvent result= new ThumbnailEvent(start, end, cause);
		result.imagePath = image;
		result.thumbnailPath = thumbnail;
		result.result = r;
		
		return result;
		
	}

	public static ThumbnailEvent suceesEvent(Path src, Path dst, Instant s, Instant e) {
		return getThumbnailEvent(src, dst, s, e, ThumbnailResult.SUCCESS, null);
	}
	
	public static ThumbnailEvent exceptionEvent(Path src, Path dst, Instant s, Instant e, Exception ex) {
		return getThumbnailEvent(src, dst, s, e, ThumbnailResult.FAIL, ex);
	}

	public static ThumbnailEvent abortedEVent(Path src, Path dst, Instant s, Instant e) {
		return getThumbnailEvent(src, dst, s, e, ThumbnailResult.ABORTED, null);
	}

	public Path getImagePath() {
		return imagePath;
	}

	public Path getThumbnailPath() {
		return thumbnailPath;
	}

	public ThumbnailResult getResult() {
		return result;
	}

	@Override
	public boolean isError() {
		return (result == ThumbnailResult.FAIL);
	}
}
