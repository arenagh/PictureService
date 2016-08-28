package org.arenadev.pictureservice.model;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
//import java.util.Map;
import java.util.Objects;

public class PictureInfo {
	
	public static final List<String> PICTURE_EXTENTION = Collections.unmodifiableList(Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".tiff", ".tif"));
	
	@Deprecated
	private String filename;
	@Deprecated
	private String folderName;
	private String fileId;
	private Instant created;
	private Instant downloaded;
	private URI source;
	private BigInteger pHash;
	private List<String> tags;
	
	@Deprecated
	private PictureInfo(Path file, URI src, Instant time) {
		this();
		
		filename = file.getFileName().toString();
		folderName = file.getParent().getFileName().toString();
		
		fileId = String.format("%s/%s", folderName, filename);
		
		source = src;

		BasicFileAttributes attrs;
		try {
			attrs = Files.getFileAttributeView(file, BasicFileAttributeView.class).readAttributes();
			downloaded = attrs.creationTime().toInstant();
		} catch (IOException e) {
			downloaded = null;
		}
		created = time;
		if (created == null) {
			created = downloaded;
		}
		pHash = null;
		
		tags.add(folderName);
	}
	
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
	
	public static PictureInfo getPictureInfo(Path file) {
		return getPictureInfo(file, null, null);
	}

	@Deprecated
	public static PictureInfo getPictureInfo(Path file, URI source) {
		return getPictureInfo(file, source, null);
	}

	@Deprecated
	public static PictureInfo getPictureInfo(Path file, URI source, Instant time) {
		if (!isPictureFile(file.toString())) {
			return null;
		}
		return new PictureInfo(file, source, time);
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
	
	@Deprecated
	public String getFilename() {
		return filename;
	}

	@Deprecated
	public String getFolderName() {
		return folderName;
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
