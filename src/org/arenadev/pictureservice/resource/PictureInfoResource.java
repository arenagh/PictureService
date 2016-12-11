package org.arenadev.pictureservice.resource;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.arenadev.pictureservice.model.LastDownloadedPictureInfoRepository;
import org.arenadev.pictureservice.model.PictureInfo;
import org.arenadev.pictureservice.model.RepositoryFactory;

@Path("info")
public class PictureInfoResource {
	
	@Inject
	private RepositoryFactory repoFactory;
	
	@GET
	@Path("list/{folder}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<PictureInfo> getPictureInfoListByFoler(@PathParam("folder") String folder) throws IOException {
		return repoFactory.getPictureInfoRepository().getPictureInfos(folder);
	}
	
	@GET
	@Path("list")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<PictureInfo> getPictureInfoList(@QueryParam("tag") List<String> tagList) throws IOException {
		// TODO 複数対応
		return repoFactory.getPictureInfoRepository().getPictureInfos(tagList.get(0));
	}

	@GET
	@Path("tmp/list/{folder}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<PictureInfo> getPictureInfoTmpListByFoler(@PathParam("folder") String folder) throws IOException {
		return repoFactory.getTmpPictureInfoRepository().getPictureInfos(folder);
	}

	@GET
	@Path("tmp/list")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<PictureInfo> getPictureInfoTmpList(@QueryParam("tag") List<String> tagList) throws IOException {
		// TODO 複数対応
		return repoFactory.getTmpPictureInfoRepository().getPictureInfos(tagList.get(0));
	}

	@GET
	@Path("downloaded/list")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<PictureInfo> getPictureInfoDownloadedList() throws IOException {
		return LastDownloadedPictureInfoRepository.getRepository().getPictureInfoList();
	}

}
