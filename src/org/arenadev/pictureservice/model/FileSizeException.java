package org.arenadev.pictureservice.model;

public class FileSizeException extends GeometryException {
 
	private static final long serialVersionUID = -5858093655803635238L;

	public FileSizeException() {
		super();
	}
	
	public FileSizeException(String message) {
		super(message);
	}
	
	public FileSizeException(Throwable th) {
		super(th);
	}
	
	public FileSizeException(String message, Throwable th) {
		super(message, th);
	}

}
