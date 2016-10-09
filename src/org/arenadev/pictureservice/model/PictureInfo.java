package org.arenadev.pictureservice.model;

import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PictureInfo {
	
	public static final List<String> PICTURE_EXTENTION = Collections.unmodifiableList(Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".tiff", ".tif"));
	
	private String fileId;
	private Instant created;
	private Instant downloaded;
	private URI source;
	private RectangleSize pictureSize;
	private long size;
	private BigInteger pHash;
	private List<String> tags;
	private String title;
	private String author;
	private String description;
	private URI referer;
	private boolean temporary;
	
	private PictureInfo(String id, URI src, Instant createdTime, Instant downloadedTime, RectangleSize picSize, long fileSize, URI ref, boolean tmp, String... tagArray) {
		this();
		
		fileId = id;
		
		source = src;
		created = createdTime;
		downloaded = downloadedTime;
		pictureSize = picSize;
		size = fileSize;
		referer = ref;
		pHash = null;
		
		Arrays.stream(tagArray).forEach(t -> tags.add(t));
		
		description = null;
		temporary = tmp;
	}
	
	public PictureInfo() {
		tags= new ArrayList<>();
	}
	
	public static PictureInfo getPictureInfo(String id, URI src, Instant createdTime, Instant downloadedTime, RectangleSize picSize, long fileSize, URI ref, String... tagArray) {
		return new PictureInfo(id, src, createdTime, downloadedTime, picSize, fileSize, ref, true, tagArray);
	}
	
	public static PictureInfo getTmpPictureInfo(String id, URI src, Instant createdTime, Instant downloadedTime, RectangleSize picSize, long fileSize, URI ref, String... tagArray) {
		return new PictureInfo(id, src, createdTime, downloadedTime, picSize, fileSize, ref, true, tagArray);
	}
	
	public PictureInfo patch(URI src, Instant createdTime, Instant downloadedTime, RectangleSize picSize, Long fileSize, URI ref, Boolean tmp) {
		URI newSource = (src == null) ? source : src;
		Instant newCreatedTime = (createdTime == null) ? created : createdTime;
		Instant newDownloadedTime = (downloadedTime == null) ? downloaded : downloadedTime;
		RectangleSize newPicSize = (picSize == null) ? pictureSize : picSize;
		long newFileSize = (fileSize == null) ? size : fileSize;
		URI newRef = (ref == null) ? referer : ref;
		boolean newTmp = (tmp == null) ? temporary : tmp;
		
		return new PictureInfo(fileId, newSource, newCreatedTime, newDownloadedTime, newPicSize, newFileSize, newRef, newTmp, tags.toArray(new String[0]));
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

	public RectangleSize getPictureSize() {
		return pictureSize;
	}

	public long getSize() {
		return size;
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
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public URI getReferer() {
		return referer;
	}

	public boolean isTemporary() {
		return temporary;
	}
	
	public void setTemporary(boolean tmp) {
		temporary = tmp;
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
