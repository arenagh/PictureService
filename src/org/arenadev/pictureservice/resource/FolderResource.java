package org.arenadev.pictureservice.resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.arenadev.pictureservice.model.Downloader;
import org.arenadev.pictureservice.model.FileIsDirectoryException;
import org.arenadev.pictureservice.model.LastDownloadedPictureInfoRepository;
import org.arenadev.pictureservice.model.PictureComparator;
import org.arenadev.pictureservice.model.PictureInfo;
import org.arenadev.pictureservice.model.PictureInfoRepository;
import org.arenadev.pictureservice.model.PictureMagnifier;
import org.arenadev.pictureservice.model.PictureRepository;

@Path("folder")
public class FolderResource {

	@GET
	@Path("list")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getFolders() {
		List<String> result = PictureInfoRepository.getRepository().getFolerBaseNames();
		return result;
	}

	@GET
	@Path("tmp/list")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public List<String> getTmpFolders() {
		return PictureInfoRepository.getTmpRepository().getFolerBaseNames();
	}
	
	@POST
	@Path("download_tmp/{folder}")
	public void downloadPictures(@PathParam("folder") String folder, @FormParam(value = "urls") String urlStr) throws IOException {
		
		PictureRepository pRepository = PictureRepository.getTmpRepository();
		PictureInfoRepository infoRepository = PictureInfoRepository.getTmpRepository();
		LastDownloadedPictureInfoRepository dRepository = LastDownloadedPictureInfoRepository.getRepository();
		
		synchronized (dRepository) {
			dRepository.clear();
			for (String urlLine : urlStr.split("\\s")) {
				if (urlLine.length() <= 0) {
					continue;
				}
				try {
					String decodedLine = URLDecoder.decode(urlLine, "UTF-8");
					int startPos = decodedLine.lastIndexOf('/') + 1;
					int endPos = decodedLine.indexOf('?', startPos);
					String filename = endPos < 0 ? decodedLine.substring(startPos) : decodedLine.substring(startPos, endPos);
					if (!PictureInfo.isPictureFile(filename)) {
						continue;
					}
					String fileId = String.format("%s/%s", folder, filename);
					java.nio.file.Path path = pRepository.getPath(fileId);
					URI uri = new URI(urlLine);
					PictureInfo info = Downloader.downloadFile(path, uri, folder);
					info.setPHash(PictureComparator.getComparator().getPHash(path));
					PictureMagnifier.getMaker().makeThumbnail(info, pRepository);
					infoRepository.addPictureInfo(folder, info);
					infoRepository.store(folder);
					dRepository.addPictureInfo(info);
				} catch (URISyntaxException | IOException | FileIsDirectoryException e) {
					continue;
				}
			}
		}
		
	}
	
}
