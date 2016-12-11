package org.arenadev.pictureservice.resource;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.arenadev.pictureservice.model.PictureRepository;
import org.arenadev.pictureservice.model.spi.FilePictureRepository;

@Path("thumbnail")
public class ThumbnailResource {
	
	public static final MimetypesFileTypeMap MIME_MAP = new MimetypesFileTypeMap();

	@GET
	@Path("tmp/{id:.+}")
	public Response downloadTmpThumbnails(@PathParam("id") String id) {
		
		return makeResponceForThumbnail(id, FilePictureRepository.getTmpRepository());
		
	}
	
	@GET
	@Path("{id:.+}")
	public Response downloadThumbnails(@PathParam("id") String id) {
		
		return makeResponceForThumbnail(id, FilePictureRepository.getRepository());
		
	}

	private Response makeResponceForThumbnail(String id, PictureRepository repository) {
		ResponseBuilder result;
		java.nio.file.Path path = repository.getThumbnailPath(id);
		result = Response.ok(path.toFile());
		result.type(MIME_MAP.getContentType(path.toFile()));
		
		return result.build();
	}

}
