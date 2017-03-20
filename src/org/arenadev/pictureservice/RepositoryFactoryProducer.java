package org.arenadev.pictureservice;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.arenadev.pictureservice.model.RepositoryFactory;
import org.arenadev.pictureservice.repository.elastic.ElasticRepository;
import org.arenadev.pictureservice.repository.elastic.ElasticRepositoryFactory;
import org.arenadev.pictureservice.repository.file.FileRepository;
import org.arenadev.pictureservice.repository.file.FileRepositoryFactory;

@Dependent
public class RepositoryFactoryProducer {
	
	
	@ElasticRepository
	@Inject
	ElasticRepositoryFactory elasticRepositoryFactory;
	
	@FileRepository
	@Inject
	FileRepositoryFactory fileRepositoryFactory;
	
	
	@Default
	@Produces
	@RequestScoped
	public RepositoryFactory getRepositoryFactory() {
		
		switch (System.getenv("PERSISTENT")) {
		case "ELASTIC":
			return elasticRepositoryFactory;
			
		case "FILE":
			return fileRepositoryFactory;
		}
		
		return elasticRepositoryFactory;
	}

}
