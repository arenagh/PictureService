package org.arenadev.pictureservice.model;

import java.net.URI;
import java.nio.file.Path;

public class DownloadException extends Exception {

	private static final long serialVersionUID = -4078101244395704107L;

	private Path path;
	private URI uri;
	
	public DownloadException(Path p, URI u) {
		super();
		path = p;
		uri = u;
	}
	
	public DownloadException(String message, Path p, URI u) {
		super(message);
		path = p;
		uri = u;
	}
	
	public DownloadException(Throwable th, Path p, URI u) {
		super(th);
		path = p;
		uri = u;
	}
	
	public DownloadException(String message, Throwable th, Path p, URI u) {
		super(message, th);
		path = p;
		uri = u;
	}

	public Path getPath() {
		return path;
	}

	public URI getUri() {
		return uri;
	}
}
