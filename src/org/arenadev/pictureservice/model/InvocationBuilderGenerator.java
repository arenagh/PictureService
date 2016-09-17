package org.arenadev.pictureservice.model;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

public interface InvocationBuilderGenerator {
	
	Invocation.Builder generate(Client client, URI uri);
	
	URI getReferer();

}
