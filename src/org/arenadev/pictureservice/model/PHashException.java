package org.arenadev.pictureservice.model;

public class PHashException extends GeometryException {
 
	private static final long serialVersionUID = -5858093655803635238L;

	public PHashException() {
		super();
	}
	
	public PHashException(String message) {
		super(message);
	}
	
	public PHashException(Throwable th) {
		super(th);
	}
	
	public PHashException(String message, Throwable th) {
		super(message, th);
	}

}
