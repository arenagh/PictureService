package org.arenadev.pictureservice.resource;

import java.nio.file.Files;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.arenadev.pictureservice.model.PictureRepository;
import org.arenadev.pictureservice.model.RepositoryFactory;

@Path("thumbnail")
public class ThumbnailResource {
	
	public static final MimetypesFileTypeMap MIME_MAP = new MimetypesFileTypeMap();

	@Inject
	private RepositoryFactory repoFactory;

	@GET
	@Path("tmp/{id:.+}")
	public Response downloadTmpThumbnails(@PathParam("id") String id) {
		
		return makeResponceForThumbnail(id, repoFactory.getTmpPictureRepository());
		
	}
	
	@GET
	@Path("{id:.+}")
	public Response downloadThumbnails(@PathParam("id") String id) {
		
		return makeResponceForThumbnail(id, repoFactory.getPictureRepository());
		
	}

	private Response makeResponceForThumbnail(String id, PictureRepository repository) {
		ResponseBuilder result;
		java.nio.file.Path path = repository.getThumbnailPath(id);
		if ((path == null) || (!Files.exists(path))) {
			return Response.status(Status.NOT_FOUND).build();
		}
		result = Response.ok(path.toFile());
		result.type(MIME_MAP.getContentType(path.toFile()));
		
		return result.build();
	}

}
