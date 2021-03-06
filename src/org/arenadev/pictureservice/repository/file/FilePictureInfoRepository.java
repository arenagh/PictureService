package org.arenadev.pictureservice.repository.file;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.arenadev.pictureservice.model.PictureInfo;
import org.arenadev.pictureservice.model.PictureInfoRepository;
import org.arenadev.pictureservice.util.ObjectMapperFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class FilePictureInfoRepository implements PictureInfoRepository {
	
	private static final List<String> TAG_LIST = Arrays.asList("GER", "M_H", "P_H", "bandaid", "kado", "lez", "ner", "nyo", "onanie", "pakkuri", "suji", "vib");
	private static final String CURRENT_SUFFIX = "_current";
	

	private static final List<String> TMP_TAG_LIST = Arrays.asList("e", "man", "ner", "test");
	private static final String TMP_CURRENT_SUFFIX = "";
	
	private static PictureInfoRepository pictureInfoRepository = new FilePictureInfoRepository(TAG_LIST, CURRENT_SUFFIX, System.getenv("PICTURE_META_ROOT"), false);
	private static PictureInfoRepository tmpInfoRepository = new FilePictureInfoRepository(TMP_TAG_LIST, TMP_CURRENT_SUFFIX, System.getenv("PICTURE_TMP_META_ROOT"), true);

	private ObjectMapper mapper;
	
	private Path metaRoot;
	private List<String> folderBaseList;
	private List<String> tagList;
	private String currentSuffix;
	
	private Map<String, Map<String, PictureInfo>> infoMap;
	
	private boolean temporary;
	
	protected FilePictureInfoRepository() {}
	
	private FilePictureInfoRepository(List<String> tags, String suffix, String mRoot, boolean tmp) {
		
		FileSystem fs = FileSystems.getDefault();
		
		metaRoot = fs.getPath(mRoot);
		folderBaseList = tags;
		tagList = tags;
		currentSuffix = suffix;
		
		mapper = ObjectMapperFactory.newObjectMapper();
		
		infoMap = new HashMap<>();
		for (String tag : tagList) {
			try {
				loadPictureInfoList(tag);
			} catch (IOException e) {
				infoMap.put(tag, new HashMap<>());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		temporary =tmp;
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

		if (!folderBaseList.contains(tag)) {
			return;
		}

		Path meta = metaRoot.resolve(tag);
		if (!Files.exists(meta)) {
			infoMap.put(tag, new HashMap<>());
			return;
		}
		
		List<String> lines = Files.readAllLines(meta);
		Map<String, PictureInfo> map = lines.stream().map(l -> l.trim()).filter(l -> !l.isEmpty()).map(this::unmarshall).collect(Collectors.toMap(p -> p.getFileId(), Function.identity()));
		infoMap.put(tag, map);
	}
	
	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureInfoRepository#getPictureInfos(java.lang.String)
	 */
	@Override
	public Map<String, PictureInfo> getPictureInfos(String tag) {
		return infoMap.get(tag);
	}
	
	private PictureInfo unmarshall(String json) {
		try {
			PictureInfo p = mapper.readValue(json, PictureInfo.class);
			return p;
		} catch (IOException e) {
			return null;
		}
	}
	
	private String marshall(PictureInfo pInfo) {
		try {
			return mapper.writeValueAsString(pInfo);
		} catch (JsonProcessingException e) {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureInfoRepository#getTagList()
	 */
	@Override
	public List<String> getTagList() {
		return tagList;
	}
	
	public String getCurrentFolder(String folderBase) {
		return folderBase + currentSuffix;
	}

	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureInfoRepository#addPictureInfo(java.lang.String, org.arenadev.pictureservice.model.PictureInfo)
	 */
	@Override
	public void addPictureInfo(String tag, String id, PictureInfo info) {
		infoMap.get(tag).put(info.getFileId(), info);
	}

	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureInfoRepository#store(java.lang.String)
	 */
	@Override
	public void store(String tag) throws IOException { // metaファイルがtag別なのは便宜上

		Map<String, PictureInfo> pictureInfoList = getPictureInfos(tag);
		
		if (pictureInfoList.size() > 0) {
			List<String> lines = pictureInfoList.values().stream().map(this::marshall).collect(Collectors.toList());
			Path meta = metaRoot.resolve(tag);
			Files.write(meta, lines);
		}
	}

	/* (non-Javadoc)
	 * @see org.arenadev.pictureservice.model.PictureInfoRepository#removePictureInfo(java.lang.String, org.arenadev.pictureservice.model.PictureInfo)
	 */
	@Override
	public void removePictureInfo(String tag, PictureInfo info) {
		infoMap.get(tag).remove(info);
	}

	@Override
	public boolean isTemporary() {
		return temporary;
	}
}
 