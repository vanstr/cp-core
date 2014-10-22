package structure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import models.SongEntity;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.1.3
 * Time: 19:04
 * To change this template use File | Settings | File Templates.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Song implements Serializable {
    private String fileId;
    private String fileName;
    private String url;
    private Long cloudId;
    private SongMetadata metadata;
    private Long urlExpiresTime;
    private Long fileSize;
    private Boolean hasMetadata;

    public Song(){}

    public Song(long cloudId, String fileId, String fileName, String url, Long urlExpiresTime) {
        this.fileName = fileName;
        this.cloudId = cloudId;
        this.fileId = fileId;
        this.url = url;
        this.urlExpiresTime = urlExpiresTime;
    }

    public Song(SongEntity entity){
        this.fileName = entity.getFileName();
        this.cloudId = entity.getCloudId();
        this.fileId = entity.getFileId();

        SongMetadata metadata = new SongMetadata(entity);
        this.metadata = metadata;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }


    public Long getCloudId() {
        return cloudId;
    }

    public void setCloudId(Long cloudId) {
        this.cloudId = cloudId;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SongMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SongMetadata metadata) {
        this.metadata = metadata;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getUrlExpiresTime() {
        return urlExpiresTime;
    }

    public void setUrlExpiresTime(Long urlExpiresTime) {
        this.urlExpiresTime = urlExpiresTime;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Boolean isHasMetadata() {
        return hasMetadata;
    }

    public void setHasMetadata(Boolean hasMetadata) {
        this.hasMetadata = hasMetadata;
    }

    public String toString() {
        return "Song name:" + this.fileName + " metadata:" + this.metadata;
    }

}
