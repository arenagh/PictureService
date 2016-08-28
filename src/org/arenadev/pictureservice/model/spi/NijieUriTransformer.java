package org.arenadev.pictureservice.model.spi;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.arenadev.pictureservice.model.SimpleInvocationBuilderGenerator;

public class NijieUriTransformer extends SimpleInvocationBuilderGenerator implements InvocationBuilderGeneratorSpi {

	@Override
	public Invocation.Builder generate(Client client, URI uri) {
		URI actualUri;
		try {
			actualUri = uri.getScheme().equals("https") ? new URI("http" + uri.toString().substring(5)) : uri;
		} catch (URISyntaxException e) {
			actualUri = uri;
		}
		
		return super.generate(client, actualUri);
	}

	@Override
	public boolean canAdapted(URI uri) {
		return uri.getHost().endsWith("nijie.info");
	}

}
