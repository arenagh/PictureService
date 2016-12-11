package org.arenadev.pictureservice;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.opencv.core.Core;

@ApplicationPath("/resources")
public class PictureservcieApps extends Application {

	public PictureservcieApps() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
}
