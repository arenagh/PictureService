package org.arenadev.pictureservice.resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.arenadev.pictureservice.model.Downloader;
import org.arenadev.pictureservice.model.FileIsDirectoryException;
import org.arenadev.pictureservice.model.LastDownloadedPictureInfoRepository;
import org.arenadev.pictureservice.model.PathGenerator;
import org.arenadev.pictureservice.model.PictureInfo;
import org.arenadev.pictureservice.model.PictureInfoRepository;
import org.arenadev.pictureservice.model.PictureMagnifier;
import org.arenadev.pictureservice.model.PictureRepository;
import org.arenadev.pictureservice.model.RepositoryFactory;
import org.opencv.core.CvException;

@Path("folder")
public class FolderResource {

	@Inject
	private RepositoryFactory repoFactory;
	
	@Inject
	private Downloader downloader;

	@POST
	@Path("download_tmp/{folder}")
	public void downloadPictures(@PathParam("folder") String folder, List<String> urlStrList) throws IOException {
		
		PictureRepository pRepository = repoFactory.getTmpPictureRepository();
		PictureInfoRepository infoRepository = repoFactory.getTmpPictureInfoRepository();
		LastDownloadedPictureInfoRepository dRepository = LastDownloadedPictureInfoRepository.getRepository();
		
		synchronized (dRepository) {
			dRepository.clear();
			for (String urlLine : urlStrList) {
				if (urlLine.length() <= 0) {
					continue;
				}
				try {
					PathGenerator pGen = new PathGenerator(folder, urlLine, true);
					
					if (!PictureInfo.isPictureFile(pGen.getPath())) {
						continue;
					}
					
					URI uri = new URI(urlLine);
					PictureInfo info = downloader.downloadFile(pGen.getPath(), uri, folder);
					
					// replace pGen by actual fileId
					pGen = new PathGenerator(info.getFileId(), true);
					
					PictureMagnifier.getMaker().makeThumbnail(info, pGen.getPath(), pGen.getThumbnailPath());
					
					infoRepository.addPictureInfo(folder, pGen.getID(), info);
					infoRepository.store(folder);
					pRepository.registPicture(pGen.getID(), pGen.getPath(), pGen.getThumbnailPath());
					dRepository.addPictureInfo(pGen.getID(), info);
				} catch (URISyntaxException | IOException | FileIsDirectoryException | CvException e) {
					continue;
				}
			}
		}
		
	}
	
}
;