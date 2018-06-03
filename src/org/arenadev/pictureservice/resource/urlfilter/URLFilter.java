package org.arenadev.pictureservice.resource.urlfilter;

import java.net.URI;

public interface URLFilter {

	URI normalizeURL(URI src);
	
	public boolean canAdapted(URI uri);
}
