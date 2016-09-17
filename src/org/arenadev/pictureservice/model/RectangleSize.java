package org.arenadev.pictureservice.model;

public class RectangleSize {

	private int width;
	private int height;
	
	public RectangleSize(int w, int h) {
		width = w;
		height = h;
	}
	
	public RectangleSize() {
		width = 0;
		height = 0;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
