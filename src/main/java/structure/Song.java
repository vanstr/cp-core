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

    private String fileName;
    private String downloadURL;
    private long cloudId;
    private SongMetadata metadata;
    private String driveId; // TODO: what to do??  add field fileUniqueField in gdrive it will be ID in Dropbox fileName

    public Song(String fileName, long cloudId) {
        this.fileName = fileName;
        this.cloudId = cloudId;
    }

    public Song(UserEntity user, long cloudId, String fileName, String downloadURL, String driveId) {
        this.fileName = fileName;
        this.cloudId = cloudId;
        this.driveId = driveId;
        this.downloadURL = downloadURL;

        SongManager manager = new SongManager();
        SongEntity songEntity = manager.getSongByHash(user, cloudId, fileName);
        this.metadata = new SongMetadata(songEntity);
        manager.finalize();

    }


    public Song(SongEntity songEntity) {
        this(songEntity.getFileName(), songEntity.getCloudId());

        this.metadata = new SongMetadata(songEntity);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getCloudId() {
        return cloudId;
    }

    public void setCloudId(long cloudId) {
        this.cloudId = cloudId;
    }


    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public SongMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SongMetadata metadata) {
        this.metadata = metadata;
    }

    public String toString(){
         return "Song name:" + this.fileName + " metadata:" + this.metadata;
    }

}
