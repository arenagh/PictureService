package org.arenadev.pictureservice.repository.elastic;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.arenadev.pictureservice.model.PictureInfoRepository;
import org.arenadev.pictureservice.model.PictureRepository;
import org.arenadev.pictureservice.model.RepositoryFactory;

@ElasticRepository
@ApplicationScoped
public class ElasticRepositoryFactory implements RepositoryFactory {
	
	@Inject
	private ClientProducer producer;
	
	public ElasticRepositoryFactory() {
	}

	@Override
	public PictureInfoRepository getPictureInfoRepository() {
		ElasticPictureInfoRepository.setTransportClient(producer.getClient(), false);
		return ElasticPictureInfoRepository.getRepository();
	}

	@Override
	public PictureInfoRepository getTmpPictureInfoRepository() {
		ElasticPictureInfoRepository.setTransportClient(producer.getClient(), true);
		return ElasticPictureInfoRepository.getTmpRepository();
	}

	@Override
	public PictureRepository getPictureRepository() {
		ElasticPictureRepository.setTransportClient(producer.getClient(), false);
		return ElasticPictureRepository.getRepository();
	}

	@Override
	public PictureRepository getTmpPictureRepository() {
		ElasticPictureRepository.setTransportClient(producer.getClient(), true);
		return ElasticPictureRepository.getTmpRepository();
	}

}
