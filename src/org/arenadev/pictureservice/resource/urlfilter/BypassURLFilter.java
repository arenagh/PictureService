package org.arenadev.pictureservice.resource.urlfilter;

import java.net.URI;

public class BypassURLFilter implements URLFilter {

	@Override
	public URI normalizeURL(URI src) {
		return src;
	}

	@Override
	public boolean canAdapted(URI uri) {
		return true;
	}

}
