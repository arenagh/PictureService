package org.arenadev.pictureservice.model;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

public class SimpleInvocationBuilderGenerator implements InvocationBuilderGenerator {

	@Override
	public Invocation.Builder generate(Client client, URI uri) {
		return client.target(uri).request();
	}

}
