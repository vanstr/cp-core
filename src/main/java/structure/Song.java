package structure;

import persistence.SongEntity;
import persistence.UserEntity;
import persistence.utility.SongManager;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.1.3
 * Time: 19:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Song implements Serializable {

    private String filePath;
    private String fileName;
    private String url;
    private long cloudId;
    private SongMetadata metadata;
    private String driveId; // TODO: what to do??  add field fileUniqueField in gdrive it will be ID in Dropbox filePath

    public Song(UserEntity user, long cloudId, String filePath, String url, String driveId) {
        this.filePath = filePath;
        this.fileName = createFileNameFromFilePath(filePath);
        this.cloudId = cloudId;
        this.driveId = driveId;
        this.url = url;

        SongManager manager = new SongManager();
        SongEntity songEntity = manager.getSongByHash(user, cloudId, filePath);
        if (songEntity != null) {
            this.metadata = new SongMetadata(songEntity);
        }
        manager.finalize();

    }

    public String createFileNameFromFilePath(String filePath){
        return filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getCloudId() {
        return cloudId;
    }

    public void setCloudId(long cloudId) {
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

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }


    public String toString() {
        return "Song name:" + this.filePath + " metadata:" + this.metadata;
    }

}
