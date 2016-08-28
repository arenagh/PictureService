package org.arenadev.pictureservice.model;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import javax.activation.MimetypesFileTypeMap;

@Deprecated
public class FileAccessor {
	
	private Path file; 
	
	private MimetypesFileTypeMap fileTypeMap;

	public FileAccessor(Path f) throws FileNotFoundException, FileIsDirectoryException {
		
		file = f;
		fileTypeMap = new MimetypesFileTypeMap();
	}

	public Object get() {
		return file.toFile();
	}

	public String getContentType() {
		return fileTypeMap.getContentType(file.toFile());
	}

	public Path getPath() {
		return file;
	}

}
