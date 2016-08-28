package org.arenadev.pictureservice.resource;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.arenadev.pictureservice.model.FileAccessor;
import org.arenadev.pictureservice.model.FileIsDirectoryException;
import org.arenadev.pictureservice.model.PictureMagnifier;
import org.arenadev.pictureservice.model.PictureRepository;

@Deprecated
@Path("pic")
public class PictureResourceOld {

	@GET
	@Path("{folder}/{file}")
	public Response getPicture(@PathParam("folder") String folder, @PathParam("file") String filename, @QueryParam("width") Integer width, @QueryParam("height") Integer height) throws IOException {

		return makeResponceForPicture(folder, filename, PictureRepository.getRepository(), width, height);

	}

	@GET
	@Path("tmp/{folder}/{file}")
	public Response getTmpPicture(@PathParam("folder") String folder, @PathParam("file") String filename, @QueryParam("width") Integer width, @QueryParam("height") Integer height) throws IOException {

		return makeResponceForPicture(folder, filename, PictureRepository.getTmpRepository(), width, height);

	}

	private Response makeResponceForPicture(String folder, String filename, PictureRepository repository, Integer width, Integer height) throws IOException {
		
		ResponseBuilder result;
		try {
			FileAccessor accessor = repository.getAccessor(folder, filename);
			
			Object contents = accessor.get();
			
			if ((width != null) || (height != null)) {
				contents = PictureMagnifier.getMaker().magnify(accessor, width, height);
			}

			result = Response.ok(contents);
			result.type(accessor.getContentType());
		} catch(FileNotFoundException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		} catch(FileIsDirectoryException e) {
			throw new WebApplicationException(e, Response.status(Status.FORBIDDEN).entity(String.format("\"%s/%s\" is a directory.", folder, filename)).build());
		}
		return result.build();
	}
}