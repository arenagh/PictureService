package org.arenadev.pictureservice.resource.urlfilter.spi;

import java.net.URI;
import java.util.Objects;

import org.arenadev.pictureservice.resource.urlfilter.URLFilter;

public class TwImgURLFilter implements URLFilter {

	@Override
	public URI normalizeURL(URI src) {
		String path = src.getPath();
		String filename = path.substring(path.lastIndexOf('/') + 1);
		int pos = filename.indexOf(':');
		if (pos > 0) {
			filename = filename.substring(0, pos);
		}
		URI result = src.resolve(filename);
		return result;
	}

	@Override
	public boolean canAdapted(URI uri) {
		return Objects.equals(uri.getHost(), "pbs.twimg.com");
	}

}
