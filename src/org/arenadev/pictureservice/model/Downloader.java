package org.arenadev.pictureservice.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

@Singleton
public class Downloader {
	
	private static final Pattern FILE_SUFFIX_PATTERN = Pattern.compile("_[0-9]+$");
	
	@Inject
	private RequestModifierMapper mapper;
	
	public PictureInfo downloadFile(Path path, URI uri, String folder) throws IOException {
		
		String urlFilename = path.getFileName().toString();

		Path dest = path;
		String name = urlFilename;
		while (Files.exists(dest)) {
			name = altName(name);
			dest = dest.getParent().resolve(name);
		}
		
		ClientConfig config = new ClientConfig();
		
		Client client = ClientBuilder.newClient(config);
		InvocationBuilderGenerator transformer = mapper.getUriTransformer(uri);
		Invocation.Builder invocationBuilder = transformer.generate(client, uri);
		Response response = invocationBuilder.get();
		Path downloadedFile = response.readEntity(File.class).toPath();
		
		Date date = response.getLastModified();
		Instant timestamp = (date != null) ? date.toInstant() : Optional.ofNullable(response.getDate()).map(d -> d.toInstant()).orElse(Instant.now());
		
		Files.move(downloadedFile, dest);
		
		PictureGeometry geometrer = PictureGeometry.getGeometrer();
		
		PictureInfo info = PictureInfo.getPictureInfo(String.format("%s/%s", folder, name), uri, timestamp, Instant.now(), geometrer.getPictureSize(dest), geometrer.getFileSize(dest), transformer.getReferer(), folder);
		info.setPHash(geometrer.getPHash(dest));

		return info;
	}

	private static String altName(String filename) {
		
		String stripped = filename.substring(0, filename.lastIndexOf('.'));
		String ext = filename.substring(filename.lastIndexOf('.'));
		
		Matcher matcher = FILE_SUFFIX_PATTERN.matcher(stripped);
		StringBuilder builder = new StringBuilder();
		if (matcher.find()) {
			String base = stripped.substring(0, matcher.start());
			int num = Integer.parseInt(stripped.substring(matcher.start() + 1));
			builder.append(base).append('_').append(num + 1).append(ext);
		} else {
			builder.append(stripped).append("_1").append(ext);
		}
		return builder.toString();
	}

}
