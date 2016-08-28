package org.arenadev.pictureservice.model.spi;

import java.net.URI;

import org.arenadev.pictureservice.model.InvocationBuilderGenerator;

public interface InvocationBuilderGeneratorSpi extends InvocationBuilderGenerator {
	
	boolean canAdapted(URI uri);

}
