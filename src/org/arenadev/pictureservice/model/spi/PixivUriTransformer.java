package org.arenadev.pictureservice.model.spi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.arenadev.pictureservice.model.SimpleInvocationBuilderGenerator;

public class PixivUriTransformer extends SimpleInvocationBuilderGenerator implements InvocationBuilderGeneratorSpi {

	private static Pattern numeric = Pattern.compile("[0-9]*");
	
	private URI referer = null;
	
	@Override
	public Invocation.Builder generate(Client client, URI uri) {
		Invocation.Builder builder = super.generate(client, uri);

		String uriName = uri.toString();
		uriName = uriName.substring(uriName.lastIndexOf("/"));
		Matcher matcher = numeric.matcher(uriName);
		matcher.find();
		String id = matcher.group();
		String refererStr = "http://www.pixiv.net/member_illust.php?mode=medium&illust_id=" + id;
		builder.header("Referer", refererStr);
		try {
			referer = new URI(refererStr);
		} catch (URISyntaxException e) {
			referer = null;
		}

		return builder;
	}
	
	@Override
	public URI getReferer() {
		return referer;
	}

	@Override
	public boolean canAdapted(URI uri) {
		return uri.getHost().endsWith("pixiv.net");
	}

}
