package org.arenadev.pictureservice.model;

public interface RepositoryFactory {

	PictureInfoRepository getPictureInfoRepository();
	PictureInfoRepository getTmpPictureInfoRepository();
	
	PictureRepository getPictureRepository();
	PictureRepository getTmpPictureRepository();
	
}
