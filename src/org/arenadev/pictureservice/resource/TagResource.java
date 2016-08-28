package org.arenadev.pictureservice.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.arenadev.pictureservice.model.PictureInfoRepository;

@Path("tag")
public class TagResource {

	@GET
	@Path("list")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getTags() {
		return PictureInfoRepository.getRepository().getTagList();
	}

	@GET
	@Path("tmp/list")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getTmpTags() {
		return PictureInfoRepository.getTmpRepository().getTagList();
	}
	
}
