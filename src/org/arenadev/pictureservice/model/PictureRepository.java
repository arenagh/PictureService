package org.arenadev.pictureservice.model;

import java.nio.file.Path;

public interface PictureRepository {

	Path getPath(String id);

	Path getThumbnailPath(String id);

	Path getRoot();

}