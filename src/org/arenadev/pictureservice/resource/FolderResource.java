package org.arenadev.pictureservice.resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.arenadev.pictureservice.event.CommonEventHandler;
import org.arenadev.pictureservice.event.DownloadEvent;
import org.arenadev.pictureservice.event.EventHandler;
import org.arenadev.pictureservice.event.ThumbnailEvent;
import org.arenadev.pictureservice.event.UndefinedEvent;
import org.arenadev.pictureservice.model.DownloadException;
import org.arenadev.pictureservice.model.Downloader;
import org.arenadev.pictureservice.model.LastDownloadedPictureInfoRepository;
import org.arenadev.pictureservice.model.MagnifyException;
import org.arenadev.pictureservice.model.PathGenerator;
import org.arenadev.pictureservice.model.PictureInfo;
import org.arenadev.pictureservice.model.PictureInfoRepository;
import org.arenadev.pictureservice.model.PictureMagnifier;
import org.arenadev.pictureservice.model.PictureRepository;
import org.arenadev.pictureservice.model.RepositoryFactory;

@Path("folder")
public class FolderResource {

	@Inject
	private RepositoryFactory repoFactory;
	
	@Inject
	private Downloader downloader;
	
	private EventHandler eventHandler = new CommonEventHandler();
	
	@POST
	@Path("download_tmp/{folder}")
	public void downloadPictures(@PathParam("folder") String folder, List<String> urlStrList) {

		eventHandler.start("download request");
		
		PictureRepository pRepository = repoFactory.getTmpPictureRepository();
		PictureInfoRepository infoRepository = repoFactory.getTmpPictureInfoRepository();
		LastDownloadedPictureInfoRepository dRepository = LastDownloadedPictureInfoRepository.getRepository();
		
		synchronized (dRepository) {
			dRepository.clear();
			eventHandler.start("download");
			for (String urlLine : urlStrList) {
				if (urlLine.length() <= 0) {
					continue;
				}
				Instant start = Instant.now();
				eventHandler.start(urlLine);
				try {
					PathGenerator pGen = new PathGenerator(folder, urlLine, true);
					
					if (!PictureInfo.isPictureFile(pGen.getPath())) {
						continue;
					}
					
					URI uri = new URI(urlLine);
					PictureInfo info = downloader.downloadFile(pGen.getPath(), uri, folder);
					eventHandler.handle(DownloadEvent.suceesEvent(uri, pGen.getPath(), start, Instant.now()));
					
					// replace pGen by actual fileId
					pGen = new PathGenerator(info.getFileId(), true);
					
					Instant thumbStart = Instant.now();
					PictureMagnifier.getMaker().makeThumbnail(info, pGen.getPath(), pGen.getThumbnailPath());
					eventHandler.handle(ThumbnailEvent.suceesEvent(pGen.getPath(), pGen.getThumbnailPath(), thumbStart, Instant.now()));
					
					infoRepository.addPictureInfo(folder, pGen.getID(), info);
					infoRepository.store(folder);
					pRepository.registPicture(pGen.getID(), pGen.getPath(), pGen.getThumbnailPath());
					dRepository.addPictureInfo(pGen.getID(), info);
					
				} catch (URISyntaxException e) { // illegal URL specified
					eventHandler.handle(DownloadEvent.exceptionEvent(null, null, start, Instant.now(), e));
					e.printStackTrace();
				} catch (DownloadException e) { // download fail
					eventHandler.handle(DownloadEvent.exceptionEvent(e.getUri(), e.getPath(), start, Instant.now(), e));
					e.printStackTrace();
				} catch (MagnifyException e) { // generating thumbnail fail
					eventHandler.handle(ThumbnailEvent.exceptionEvent(e.getImagePath(), e.getThumbnailPath(), start, Instant.now(), e));
					e.printStackTrace();
				} catch (IOException e) { // flashing meta file fail
					// TODO Auto-generated catch block
					eventHandler.handle(UndefinedEvent.genEvent(start, Instant.now()));
					e.printStackTrace();
				}
				eventHandler.end(urlLine);
			}
			eventHandler.end("download");
		}
		eventHandler.end("download request");
	}
	
}
;