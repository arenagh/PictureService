package org.arenadev.pictureservice.resource;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.arenadev.pictureservice.model.RepositoryFactory;

@Path("tag")
public class TagResource {

	@Inject
	private RepositoryFactory repoFactory;

	@GET
	@Path("list")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getTags() {
		return repoFactory.getPictureInfoRepository().getTagList();
	}

	@GET
	@Path("tmp/list")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getTmpTags() {
		return repoFactory.getTmpPictureInfoRepository().getTagList();
	}
	
}
