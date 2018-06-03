package org.arenadev.pictureservice.migration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.arenadev.pictureservice.model.PictureInfo;
import org.arenadev.pictureservice.model.PictureInfoRepository;
import org.arenadev.pictureservice.model.PictureRepository;
import org.arenadev.pictureservice.repository.file.FilePictureInfoRepository;
import org.arenadev.pictureservice.repository.file.FilePictureRepository;
import org.arenadev.pictureservice.util.ObjectMapperFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ElasticSearchDataFileGenerator {

	public static final String ACTION_AND_META = "{ \"%s\" : { \"_type\" : \"%s\", \"_id\" : \"%s\" } }";
	public static final String FILE_DATA = "{ \"file\" : \"%s\", \"thumbnail\" : \"%s\" }";

	public static final String INDEX_ACTION = "index";
	public static final String INFO_TYPE = "info";
	public static final String FILE_TYPE = "file";
	
	private PictureInfoRepository infoRepository;
	private PictureRepository picRepository;

	private PictureInfoRepository tmpInfoRepository;
	private PictureRepository tmpPicRepository;
	
	private ObjectMapper mapper;
	
	private Set<String> usedNames;
	
	public ElasticSearchDataFileGenerator() {
		
		mapper = ObjectMapperFactory.newObjectMapper();
		
		infoRepository = FilePictureInfoRepository.getRepository();
		picRepository = FilePictureRepository.getRepository();

		tmpInfoRepository = FilePictureInfoRepository.getTmpRepository();
		tmpPicRepository = FilePictureRepository.getTmpRepository();
		
	}
	
	public List<String> gen(boolean tmp) throws JsonProcessingException {
		
		return tmp ? generate(tmpInfoRepository, tmpPicRepository) : generate(infoRepository, picRepository);
	}
	
	public List<String> generate(PictureInfoRepository infoRepo, PictureRepository picRepo) throws JsonProcessingException {
		
		List<PictureInfo> infos = new ArrayList<>();
		for (String tag : infoRepo.getTagList()) {
			infos.addAll(infoRepo.getPictureInfos(tag).values());
		}
		
		List<String> result = new ArrayList<>();
		usedNames = new HashSet<>();
		for (PictureInfo info : infos) {
			String fileId = info.getFileId();
			String elasticId = genId(fileId);
			result.add(String.format(ACTION_AND_META, INDEX_ACTION, INFO_TYPE, elasticId));
			result.add(mapper.writeValueAsString(info));
			result.add(String.format(ACTION_AND_META, INDEX_ACTION, FILE_TYPE, elasticId));
			result.add(String.format(FILE_DATA, picRepo.getPath(fileId), picRepo.getThumbnailPath(fileId)));
		}
		
		return result;
	}

	private String genId(String fileId) {
		
		String name = fileId.replaceAll(" ", "").replaceAll("\"", "").replaceAll("/", "");

		String curName = name;
		for (int i = 1 ; usedNames.contains(curName) ; i++) {
			curName = name + "_" + i;
		}
		usedNames.add(curName);
		
		return curName;
		
	}
	
}
