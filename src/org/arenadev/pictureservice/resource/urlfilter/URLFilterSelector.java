package org.arenadev.pictureservice.resource.urlfilter;

import java.net.URI;

public interface URLFilterSelector {

	URLFilter selectURLFilter(URI uri);
}
