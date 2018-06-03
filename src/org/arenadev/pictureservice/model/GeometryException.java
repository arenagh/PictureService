package org.arenadev.pictureservice.model;

public class GeometryException extends Exception {
 
	private static final long serialVersionUID = 6821285559193895803L;

	public GeometryException() {
		super();
	}
	
	public GeometryException(String message) {
		super(message);
	}
	
	public GeometryException(Throwable th) {
		super(th);
	}
	
	public GeometryException(String message, Throwable th) {
		super(message, th);
	}

}
