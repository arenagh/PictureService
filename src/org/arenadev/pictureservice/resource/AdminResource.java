package org.arenadev.pictureservice.resource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.arenadev.pictureservice.migration.ElasticSearchDataFileGenerator;
import org.arenadev.pictureservice.model.FileIsDirectoryException;
import org.arenadev.pictureservice.model.GeometryException;
import org.arenadev.pictureservice.model.MetafileGenerator;
import org.arenadev.pictureservice.model.PHashException;
import org.arenadev.pictureservice.model.ThumbnailGenerator;

@Path("admin")
public class AdminResource {
	
	private ThumbnailGenerator tGen = ThumbnailGenerator.getGenerator();
	
	private MetafileGenerator mGen = MetafileGenerator.getGenerator();
	
	@Path("thumb/progress")
	@GET
	public int getThumbProgress() {
		return tGen.getProgress();
	}
		
	@Path("thumb")
	@POST
	public Response genThumbnail() {
				
		synchronized (tGen) {
			if (tGen.isGenerating()) {
				return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
			}
			ExecutorService singleThread = Executors.newSingleThreadExecutor();
			singleThread.execute(new Runnable() {
				@Override
				public void run() {
					try {
						tGen.generate();
					} catch (FileIsDirectoryException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		return Response.ok().build();
	}
	
	@Path("meta/progress")
	@GET
	public int getMetaProgress() {
		return mGen.getProgress();
	}
	
	@Path("meta")
	@POST
	public Response genMeta() {
		synchronized (mGen) {
			if (mGen.isGenerating()) {
				return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
			}
			ExecutorService singleThread = Executors.newSingleThreadExecutor();
			singleThread.execute(new Runnable() {
				@Override
				public void run() {
					try {
						mGen.generate();
					} catch (PHashException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		return Response.ok().build();
	}
	
	@Path("migration/progress")
	@GET
	public int getMigrationProgress() {
		return mGen.getProgress();
	}
	
	@Path("migration")
	@POST
	public Response genMigration() {
		synchronized (mGen) {
			if (mGen.isGenerating()) {
				return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
			}
			ExecutorService singleThread = Executors.newSingleThreadExecutor();
			singleThread.execute(new Runnable() {
				@Override
				public void run() {
					try {
						mGen.migrate();
					} catch (GeometryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		return Response.ok().build();
	}


	@Path("tags")
	@POST
	public Response putTags() {
		
		mGen.putTags();
		
		return Response.ok().build();
	}
	
	@Path("tmp")
	@POST
	public Response putTemporary() {
		mGen.putTmp();
		
		return Response.ok().build();
	}
	
	@Path("getData/tmp")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM + "; charset=UTF-8")
	public Reader getTmpData() throws Exception {
		return getData(true);
	}
	
	@Path("getData")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM + "; charset=UTF-8")
	public Reader getDataSaved() throws Exception {
		return getData(false);
	}
	
	public Reader getData(boolean tmp) throws Exception {
		
		ElasticSearchDataFileGenerator eGen = new ElasticSearchDataFileGenerator();
		String data = eGen.gen(tmp).stream().collect(Collectors.joining(System.lineSeparator()));
		
		return new StringReader(data);
	}

}
