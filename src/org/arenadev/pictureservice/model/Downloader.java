package org.arenadev.pictureservice.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;

import org.glassfish.jersey.client.ClientConfig;

public class Downloader {
	
	@Deprecated
	public static PictureInfo downloadFile(FileAccessor accessor, String urlStr) throws URISyntaxException, IOException {
		Path path = accessor.getPath();
		String folder = path.getParent().getFileName().toString();
		return downloadFile(accessor.getPath(), new URI(urlStr), folder);
	}
	
	public static PictureInfo downloadFile(Path path, URI uri, String folder) throws IOException {
		
		String urlFilename = path.getFileName().toString();

		Path dest = path;
		String name = urlFilename;
		while (Files.exists(dest)) {
			name = altName(name);
			dest = dest.getParent().resolve(name);
		}
		
		ClientConfig config = new ClientConfig();
		
		Client client = ClientBuilder.newClient(config);
		InvocationBuilderGenerator transformer = UriTransformerFactory.getInstance().getTransformer(uri);
		Invocation.Builder invocationBuilder = transformer.generate(client, uri);
		Response response = invocationBuilder.get();
		Path downloadedFile = response.readEntity(File.class).toPath();
		
		Date date = response.getLastModified();
		Instant timestamp = (date != null) ? date.toInstant() : Optional.ofNullable(response.getDate()).map(d -> d.toInstant()).orElse(Instant.now());
		
		Files.move(downloadedFile, dest);
		
		return PictureInfo.getPictureInfo(String.format("%s/%s", folder, name), uri, timestamp, Instant.now(), folder);
	}

	private static String altName(String filename) {
		
		String stripped = filename.substring(0, filename.lastIndexOf('.'));
		String ext = filename.substring(filename.lastIndexOf('.'));
		
		Matcher matcher = Pattern.compile("\\([0-9]+\\)$").matcher(stripped);
		StringBuilder builder = new StringBuilder();
		if (matcher.find()) {
			String base = stripped.substring(matcher.start());
			int num = Integer.parseInt(stripped.substring(matcher.start() + 1, matcher.end()));
			builder.append(base).append('(').append(num + 1).append(')').append(ext);
		} else {
			builder.append(stripped).append("(1)").append(ext);
		}
		return builder.toString();
	}

}
