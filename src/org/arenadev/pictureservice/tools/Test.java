package org.arenadev.pictureservice.tools;

import java.io.IOException;
import java.time.Instant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Test {

	public static void main(String[] args) throws IOException {
		
//		Instant t = Instant.now();
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectMapper jsr310Mapper = new ObjectMapper();
		jsr310Mapper.registerModule(new JavaTimeModule());
		jsr310Mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		Target test1 = new Target("aaaaaa", "bbbbbb", "cccccc");
		String json = mapper.writeValueAsString(test1);
		Target rev = mapper.readValue(json, Target.class);
		
		System.out.println(json);
		System.out.println(String.format("aaa:%s, bbb:%s, ccc:%s", rev.getAaa(), rev.getBbb(), rev.getCcc()));
		
	}
	
	public static class Target {
		private String aaa;
		private String bbb;
		private String ccc;
		
		public Target() {
		}
		
		public Target(String a, String b, String c) {
			aaa = a;
			bbb = b;
			ccc = c;
		}
		
		public String getAaa() {
			return aaa;
		}
		
		public String getBbb() {
			return bbb;
		}
		
		public String getCcc() {
			return ccc;
		}
		
		
	}

}
