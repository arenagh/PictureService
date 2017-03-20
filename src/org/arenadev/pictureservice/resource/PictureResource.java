package org.arenadev.pictureservice.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.arenadev.pictureservice.model.FileIsDirectoryException;
import org.arenadev.pictureservice.model.PictureMagnifier;
import org.arenadev.pictureservice.model.PictureReader;
import org.arenadev.pictureservice.model.PictureRepository;
import org.arenadev.pictureservice.model.RepositoryFactory;
import org.opencv.core.CvException;

@Path("picture")
public class PictureResource {
	
	@Inject
	private RepositoryFactory repoFactory;

//	public static final MimetypesFileTypeMap MIME_MAP = new MimetypesFileTypeMap();

	@GET
	@Path("tmp/{id:.+}")
	public Response getTmpPicture(@PathParam("id") String id, @QueryParam("width") Integer width, @QueryParam("height") Integer height) throws IOException {

		return makeResponceForPicture(id, repoFactory.getTmpPictureRepository(), width, height);

	}

	@GET
	@Path("{id:.+}")
	public Response getPicture(@PathParam("id") String id, @QueryParam("width") Integer width, @QueryParam("height") Integer height) throws IOException {

		return makeResponceForPicture(id, repoFactory.getPictureRepository(), width, height);

	}

	private Response makeResponceForPicture(String id, PictureRepository repository, Integer width, Integer height) throws IOException {
		
		ResponseBuilder result;
		try {
			java.nio.file.Path path = repository.getPath(id);
			if ((path == null) || (!Files.exists(path))) {
				Response.status(Status.NOT_FOUND).build();
			}
			
			Object contents = path.toFile();
			
			if ((width != null) || (height != null)) {
				contents = PictureMagnifier.getMaker().magnifyToBufferedImage(path, width, height);
			}

			result = Response.ok(contents);
			result.type(PictureReader.MIME_MAP.getContentType(path.toFile()));
		} catch(FileNotFoundException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		} catch(FileIsDirectoryException e) {
			throw new WebApplicationException(e, Response.status(Status.FORBIDDEN).entity(String.format("\"%s\" is a directory.", id)).build());
		} catch (CvException e) {
			throw new WebApplicationException(e, Response.status(Status.BAD_REQUEST).entity(String.format("\"%s\" is a wrong file.", id)).build());
		}
		return result.build();
	}
}