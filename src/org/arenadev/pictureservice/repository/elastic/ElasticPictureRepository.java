package org.arenadev.pictureservice.repository.elastic;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.context.ApplicationScoped;

import org.arenadev.pictureservice.model.PictureRepository;
import org.arenadev.pictureservice.util.ObjectMapperFactory;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ApplicationScoped
public class ElasticPictureRepository implements PictureRepository {
	
	public static final String JSON_STRING_TEMPLATE = "{\"file\":\"%s\", \"thumbnail\":\"%s\"}";

	private static final ElasticPictureRepository pictureRepository = new ElasticPictureRepository(false);
	
	private static final ElasticPictureRepository tmpPictureRepository = new ElasticPictureRepository(true);
	
	private boolean temporary;
	
	private ObjectMapper mapper;
	
	private TransportClient client;
	
	private ElasticPictureRepository(boolean tmp) {
		
		client = null;

		temporary = tmp;
		
		mapper = ObjectMapperFactory.newObjectMapper();
	}
	
	public static void setTransportClient(TransportClient c, boolean tmp) {
		if (tmp) {
			tmpPictureRepository.client = c;
		} else {
			pictureRepository.client = c;
		}
	}

	public static PictureRepository getRepository() {
		return pictureRepository;
	}
	
	public static PictureRepository getTmpRepository() {
		return tmpPictureRepository;
	}
	
	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureRepository#getPath(java.lang.String)
	 */
	@Override
	public Path getPath(String id) {
		
		try {
			GetResponse response = client.prepareGet("picture", "file", id).get();
			if (!response.isExists()) {
				return null;
			}
			
			Object filePath = response.getSourceAsMap().get("file");
			if (filePath == null) {
				return null;
			}
			return Paths.get(filePath.toString());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureRepository#getThumbnailPath(java.lang.String)
	 */
	@Override
	public Path getThumbnailPath(String id) {
		
		try {
			GetResponse response = client.prepareGet("picture", "file", id).get();
			if (!response.isExists()) {
				return null;
			}
			
			Object thumbPath = response.getSourceAsMap().get("thumbnail");
			if (thumbPath == null) {
				return null;
			}
			return Paths.get(thumbPath.toString());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureRepository#getRoot()
	 */
	@Override
	public Path getRoot() {
		return null;
	}

	@Override
	public void registPicture(String id, Path pic, Path thumbnail) {
		try {
			String json = String.format(JSON_STRING_TEMPLATE, pic.toString(), thumbnail.toString());
			IndexResponse response = client.prepareIndex("picture", "file", id).setSource(json).get();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
