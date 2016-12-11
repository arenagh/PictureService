package org.arenadev.pictureservice.tools;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ElasticBulkLogAnalysis {

	public static void main(String[] args) throws Exception {
		
		Path logFile = FileSystems.getDefault().getPath(args[0]);
		List<String> logs = Files.readAllLines(logFile);
		
		ObjectMapper mapper = new ObjectMapper();
		
		for (String json : logs) {
			Map<String, ?> entry = mapper.readValue(json, Map.class);
			System.out.println(entry);
		}

	}

}
