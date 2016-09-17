package org.arenadev.pictureservice.model;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class PictureInfoRepository {
	
	private static final List<String> TAG_LIST = Arrays.asList("GER", "M_H", "P_H", "bandaid", "kado", "lez", "ner", "nyo", "onanie", "pakkuri", "suji", "vib");
	private static final String CURRENT_SUFFIX = "_current";
	

	private static final List<String> TMP_TAG_LIST = Arrays.asList("e", "man", "ner", "test");
	private static final String TMP_CURRENT_SUFFIX = "";
	
	private static PictureInfoRepository pictureInfoRepository = new PictureInfoRepository(TAG_LIST, CURRENT_SUFFIX, System.getenv("PICTURE_META_ROOT"));
	private static PictureInfoRepository tmpInfoRepository = new PictureInfoRepository(TMP_TAG_LIST, TMP_CURRENT_SUFFIX, System.getenv("PICTURE_TMP_META_ROOT"));

	private ObjectMapper mapper;
	
	private Path metaRoot;
	private List<String> folderBaseList;
	private List<String> tagList;
	private String currentSuffix;
	
	private Map<String, List<PictureInfo>> infoMap;
	
	protected PictureInfoRepository() {}
	
	private PictureInfoRepository(List<String> tags, String suffix, String mRoot) {
		
		FileSystem fs = FileSystems.getDefault();
		
		metaRoot = fs.getPath(mRoot);
		folderBaseList = tags;
		tagList = tags;
		currentSuffix = suffix;
		
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		infoMap = new HashMap<>();
		for (String tag : tagList) {
			try {
				loadPictureInfoList(tag);
			} catch (IOException e) {
				infoMap.put(tag, new ArrayList<>());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static PictureInfoRepository getRepository() {
		return pictureInfoRepository;
	}
	
	public static PictureInfoRepository getTmpRepository() {
		return tmpInfoRepository;
	}
	
	public void loadPictureInfoList(String tag) throws IOException { // TODO 複数tag対応

		if (!folderBaseList.contains(tag)) {
			return;
		}

		Path meta = metaRoot.resolve(tag);
		if (!Files.exists(meta)) {
			infoMap.put(tag, new ArrayList<>());
			return;
		}
		
		List<String> lines = Files.readAllLines(meta);
		List<PictureInfo> list = lines.stream().map(l -> l.trim()).filter(l -> !l.isEmpty()).map(this::unmarshall).collect(Collectors.toList());
		infoMap.put(tag, list);
	}
	
	public List<PictureInfo> getPictureInfos(String tag) {
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
	
	public List<String> getTagList() {
		return tagList;
	}
	
	public String getCurrentFolder(String folderBase) {
		return folderBase + currentSuffix;
	}

	public void addPictureInfo(String tag, PictureInfo info) {
		infoMap.get(tag).add(info);
	}

	public void store(String tag) throws IOException { // metaファイルがtag別なのは便宜上

		List<PictureInfo> pictureInfoList = getPictureInfos(tag);
		
		if (pictureInfoList.size() > 0) {
			List<String> lines = pictureInfoList.stream().map(this::marshall).collect(Collectors.toList());
			Path meta = metaRoot.resolve(tag);
			Files.write(meta, lines);
		}
	}

	public void removePictureInfo(String tag, PictureInfo info) {
		infoMap.get(tag).remove(info);
	}
}
 