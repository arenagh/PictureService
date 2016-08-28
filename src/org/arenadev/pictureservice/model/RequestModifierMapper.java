package org.arenadev.pictureservice.model;

import java.net.URI;

public interface RequestModifierMapper {

	InvocationBuilderGenerator getUriTransformer(URI uri);

}
