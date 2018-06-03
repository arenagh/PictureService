package org.arenadev.pictureservice.model;

public class PictureSizeException extends GeometryException {
 
	private static final long serialVersionUID = -5858093655803635238L;

	public PictureSizeException() {
		super();
	}
	
	public PictureSizeException(String message) {
		super(message);
	}
	
	public PictureSizeException(Throwable th) {
		super(th);
	}
	
	public PictureSizeException(String message, Throwable th) {
		super(message, th);
	}

}
