package org.arenadev.pictureservice.repository.elastic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.arenadev.pictureservice.model.PictureInfo;
import org.arenadev.pictureservice.model.PictureInfoRepository;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ApplicationScoped
public class ElasticPictureInfoRepository implements PictureInfoRepository {
	
	private static ElasticPictureInfoRepository pictureInfoRepository = new ElasticPictureInfoRepository(false);
	private static ElasticPictureInfoRepository tmpInfoRepository = new ElasticPictureInfoRepository(true);

	private ObjectMapper mapper;
	
//	private TransportClient client;
	
	private List<String> tagList;
	private boolean temporary;
	
	private TransportClient client;
	
	protected ElasticPictureInfoRepository() {}
	
	private ElasticPictureInfoRepository(boolean tmp) {

		client = null;

		temporary = tmp;
		
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}
	
	private void setTagList() {
		try {
			SearchResponse response = client.prepareSearch("picture").setTypes("info").setSize(0).setQuery(QueryBuilders.termQuery("temporary", temporary)).addAggregation(AggregationBuilders.terms("tag_list").field("tagList")).get();
			Terms tagAgg = response.getAggregations().get("tag_list");
			tagList = tagAgg.getBuckets().stream().map(b -> b.getKeyAsString()).collect(Collectors.toList());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setTransportClient(TransportClient c, boolean tmp) {
		ElasticPictureInfoRepository repo = tmp ? tmpInfoRepository : pictureInfoRepository;
		repo.client = c;
		repo.setTagList();
	}

	public static PictureInfoRepository getRepository() {
		return pictureInfoRepository;
	}
	
	public static PictureInfoRepository getTmpRepository() {
		return tmpInfoRepository;
	}
	
	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureInfoRepository#loadPictureInfoList(java.lang.String)
	 */
	@Override
	public void loadPictureInfoList(String tag) throws IOException { // TODO 複数tag対応
	}
	
	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureInfoRepository#getPictureInfos(java.lang.String)
	 */
	@Override
	public Map<String, PictureInfo> getPictureInfos(String tag) {
		try {
			SearchRequestBuilder reqBuilder = client.prepareSearch("picture").setTypes("info").setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("temporary", temporary)).must(QueryBuilders.termQuery("tagList", tag)));
			reqBuilder.setSize(0);
			int size = (int) reqBuilder.get().getHits().getTotalHits();
			reqBuilder.setSize(size);
			SearchResponse response = reqBuilder.get();
			Map<String, PictureInfo> result = new HashMap<>();
			for (SearchHit json : response.getHits().hits()) {
				result.put(json.getId(), mapper.readValue(json.getSourceAsString(), PictureInfo.class));
			}
			return result;
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new HashMap<>();
	}
	
	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureInfoRepository#getTagList()
	 */
	@Override
	public List<String> getTagList() {
		return tagList;
	}
	
	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureInfoRepository#addPictureInfo(java.lang.String, org.arenadev.pictureservice.model.PictureInfo)
	 */
	@Override
	public void addPictureInfo(String tag, String id, PictureInfo info) {
		try {
			String json = mapper.writeValueAsString(info);
			IndexResponse response = client.prepareIndex("picture", "info", id).setSource(json).get();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureInfoRepository#store(java.lang.String)
	 */
	@Override
	public void store(String tag) throws IOException { // do nothing
	}

	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureInfoRepository#removePictureInfo(java.lang.String, org.arenadev.pictureservice.model.PictureInfo)
	 */
	@Override
	public void removePictureInfo(String tag, PictureInfo info) {
		try {
			DeleteResponse response = client.prepareDelete("picture", "info", info.getFileId()).get();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isTemporary() {
		return temporary;
	}
}
 