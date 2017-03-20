package org.arenadev.pictureservice.repository.file;

import javax.enterprise.context.ApplicationScoped;

import org.arenadev.pictureservice.model.PictureInfoRepository;
import org.arenadev.pictureservice.model.PictureRepository;
import org.arenadev.pictureservice.model.RepositoryFactory;

@FileRepository
@ApplicationScoped
public class FileRepositoryFactory implements RepositoryFactory {
	
	public FileRepositoryFactory() {
		
	}

	@Override
	public PictureInfoRepository getPictureInfoRepository() {
		return FilePictureInfoRepository.getRepository();
	}

	@Override
	public PictureInfoRepository getTmpPictureInfoRepository() {
		return FilePictureInfoRepository.getTmpRepository();
	}

	@Override
	public PictureRepository getPictureRepository() {
		return FilePictureRepository.getRepository();
	}

	@Override
	public PictureRepository getTmpPictureRepository() {
		return FilePictureRepository.getTmpRepository();
	}

}
