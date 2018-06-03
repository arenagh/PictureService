package org.arenadev.pictureservice.model.spi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.arenadev.pictureservice.model.SimpleInvocationBuilderGenerator;

public class TwImgUriTransformer extends SimpleInvocationBuilderGenerator implements InvocationBuilderGeneratorSpi {

	@Override
	public Invocation.Builder generate(Client client, URI uri) {
		URI actualUri;
		try {
			actualUri = new URI(uri.toString() + ":orig");
		} catch (URISyntaxException e) {
			actualUri = uri;
		}
		
		return super.generate(client, actualUri);
	}

	@Override
	public boolean canAdapted(URI uri) {
		return Objects.equals(uri.getHost(), "pbs.twimg.com");
	}

}
