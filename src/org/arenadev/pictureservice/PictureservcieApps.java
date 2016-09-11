package org.arenadev.pictureservice;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.opencv.core.Core;

@ApplicationPath("/resources")
public class PictureservcieApps extends Application {

	public PictureservcieApps(@Context ServletContext context) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
}
