package org.arenadev.pictureservice.resource;

import java.io.FileNotFoundException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.arenadev.pictureservice.model.FileAccessor;
import org.arenadev.pictureservice.model.FileIsDirectoryException;
import org.arenadev.pictureservice.model.PictureRepository;

@Path("thumb")
public class ThumbnailResourceOld {
	
	@GET
	@Path("{folder}/{file}")
	public Response downloadThumbnails(@PathParam("folder") String folder, @PathParam("file") String filename) {
		
		return makeResponceForThumbnail(folder, filename, PictureRepository.getRepository());
		
	}

	@GET
	@Path("tmp/{folder}/{file}")
	public Response downloadTmpThumbnails(@PathParam("folder") String folder, @PathParam("file") String filename) {
		
		return makeResponceForThumbnail(folder, filename, PictureRepository.getTmpRepository());
		
	}

	private Response makeResponceForThumbnail(String folder, String filename,
			PictureRepository repository) {
		ResponseBuilder result;
		try {
			FileAccessor accessor = repository.getThumbnailAccessor(folder, filename);
				result = Response.ok(accessor.get());
				result.type(accessor.getContentType());
		} catch(FileNotFoundException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		} catch(FileIsDirectoryException e) {
			throw new WebApplicationException(e, Response.status(Status.FORBIDDEN).entity(String.format("\"%s/%s\" is a directory.", folder, filename)).build());
		}
		
		return result.build();
	}

}
