package org.arenadev.pictureservice.resource.urlfilter.spi;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.arenadev.pictureservice.resource.urlfilter.BypassURLFilter;
import org.arenadev.pictureservice.resource.urlfilter.URLFilter;
import org.arenadev.pictureservice.resource.urlfilter.URLFilterSelector;

public class URLFilterSelectorSpi implements URLFilterSelector {

	private static List<URLFilter> urlFilterList = Arrays.asList(new TwImgURLFilter());
	
	@Override
	public URLFilter selectURLFilter(URI uri) {
		return urlFilterList.stream().filter(f -> f.canAdapted(uri)).findFirst().orElse(new BypassURLFilter());
	}
}