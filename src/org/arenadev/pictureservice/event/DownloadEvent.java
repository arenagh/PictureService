package org.arenadev.pictureservice.event;

import java.net.URI;
import java.nio.file.Path;
import java.time.Instant;

public class DownloadEvent extends Event {
	
	private URI source;
	
	private Path destination;
	
	private DownloadResult result;
	
	private DownloadEvent(Instant start, Instant end, Exception exception) {
		super(EventType.DOWNLOAD, start, end, exception);
	}
	
	public static DownloadEvent getDownloadEvent(URI src, Path dst, Instant start, Instant end, DownloadResult r, Exception cause) {
		DownloadEvent result= new DownloadEvent(start, end, cause);
		result.source = src;
		result.destination = dst;
		result.result = r;
		
		return result;
		
	}

	public static DownloadEvent suceesEvent(URI src, Path dst, Instant s, Instant e) {
		return getDownloadEvent(src, dst, s, e, DownloadResult.SUCCESS, null);
	}
	
	public static DownloadEvent exceptionEvent(URI src, Path dst, Instant s, Instant e, Exception ex) {
		return getDownloadEvent(src, dst, s, e, DownloadResult.FAIL, ex);
	}

	public static DownloadEvent abortedEVent(URI src, Path dst, Instant s, Instant e) {
		return getDownloadEvent(src, dst, s, e, DownloadResult.ABORTED, null);
	}

	public URI getSource() {
		return source;
	}

	public Path getDestination() {
		return destination;
	}

	public DownloadResult getResult() {
		return result;
	}

	@Override
	public boolean isError() {
		return (result == DownloadResult.FAIL);
	}
}
