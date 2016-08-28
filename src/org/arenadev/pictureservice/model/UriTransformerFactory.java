package org.arenadev.pictureservice.model;

import java.net.URI;

import org.arenadev.pictureservice.model.spi.RequestModifierMapperSpi;

public class UriTransformerFactory {
	
	private static UriTransformerFactory factory = new UriTransformerFactory();

	private RequestModifierMapper mapper;
	
	private UriTransformerFactory() {
		mapper = new RequestModifierMapperSpi();
	}
	
	public static UriTransformerFactory getInstance() {
		return factory;
	}

	public InvocationBuilderGenerator getTransformer(URI uri) {
		InvocationBuilderGenerator transformer = mapper.getUriTransformer(uri);
		return (transformer == null) ? new SimpleInvocationBuilderGenerator() : transformer;
	}

}
