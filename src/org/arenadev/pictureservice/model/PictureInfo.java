package org.arenadev.pictureservice.model;

import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
//import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PictureInfo {
	
	public static final List<String> PICTURE_EXTENTION = Collections.unmodifiableList(Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".tiff", ".tif"));
	
	private String fileId;
	private Instant created;
	private Instant downloaded;
	private URI source;
	private BigInteger pHash;
	private List<String> tags;
	
	private PictureInfo(String id, URI src, Instant createdTime, Instant downloadedTime, String... tagArray) {
		this();
		
		fileId = id;
		
		source = src;
		created = createdTime;
		downloaded = downloadedTime;
		pHash = null;
		
		Arrays.stream(tagArray).forEach(t -> tags.add(t));
	}
	
	public PictureInfo() {
		tags= new ArrayList<>();
	}
	
	public static PictureInfo getPictureInfo(String id, URI source, Instant createdTime, Instant downloadedTime, String... tagArray) {
		return new PictureInfo(id, source, createdTime, downloadedTime, tagArray);
	}

	public static boolean isPictureFile(String filename) {
		String filenameToLower = filename.toLowerCase();
		return PICTURE_EXTENTION.stream().anyMatch(e -> filenameToLower.endsWith(e));
	}
	
	public String getFileId() {
		return fileId;
	}

	public Instant getCreated() {
		return created;
	}
	
	public Instant getDownloaded() {
		return downloaded;
	}
	
	public URI getSource() {
		return source;
	}

	public BigInteger getPHash() {
		return pHash;
	}
	
	public void setPHash(BigInteger ph) {
		pHash = ph;
	}
	
	public List<String> getTagList() {
		return Collections.unmodifiableList(tags);
	}
	
	public void setTagList(List<String> tags) {
		tags.clear();
		tags.addAll(tags);
	}
	
	public void addTag(String... tag) {
		tags.addAll(Arrays.asList(tag));
	}
	
	public void removeTag(String... tag) {
		tags.removeAll(Arrays.asList(tag));
	}
	
	public void clearTags() {
		tags.clear();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(fileId);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PictureInfo)) {
			return false;
		}
		
		PictureInfo p = (PictureInfo) o;
		
		return Objects.equals(fileId, p.fileId);
	}
}
