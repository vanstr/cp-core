package persistence;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;


/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.10.2
 * Time: 20:24
 * To change this template use File | Settings | File Templates.
 */
@Table(name = "song", schema = "", catalog = "cloud_player")
@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true
)
public class SongEntity {

    private long id;
    //private long userId;
    private long cloudId;
    private Timestamp lastTimeAccessed;
    private String fileName;
    private long fileSize;
    private String metadataTitle;
    private String metadataArtist;
    private String metadataAlbum;
    private String metadataYear;
    private String metadataGenre;
    private int metadataLengthSeconds;

    private UserEntity user;

    @Id
    @Column(name = "id")
    @GenericGenerator(name="gen",strategy="increment")
    @GeneratedValue(generator="gen")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "cloud_id")
    public long getCloudId() {
        return cloudId;
    }

    public void setCloudId(long cloud_id) {
        this.cloudId = cloud_id;
    }

    @Column(name = "last_time_accessed")
    public Timestamp getLastTimeAccessed() {
        return lastTimeAccessed;
    }

    public void setLastTimeAccessed(Timestamp lastAccess) {
        this.lastTimeAccessed = lastAccess;
    }

    @Column(name = "file_name")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String file_name) {
        this.fileName = file_name;
    }

    @Column(name = "file_size")
    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long file_size) {
        this.fileSize = file_size;
    }

    @Column(name = "metadata_title")
    public String getMetadataTitle() {
        return metadataTitle;
    }

    public void setMetadataTitle(String metadata_title) {
        this.metadataTitle = metadata_title;
    }

    @Column(name = "metadata_artist")
    public String getMetadataArtist() {
        return metadataArtist;
    }

    public void setMetadataArtist(String metadata_author) {
        this.metadataArtist = metadata_author;
    }

    @Column(name = "metadata_length_seconds")
    public int getMetadataLengthSeconds() {
        return metadataLengthSeconds;
    }

    public void setMetadataLengthSeconds(int metadata_length_seconds) {
        this.metadataLengthSeconds = metadata_length_seconds;
    }

    @Column(name = "metadata_album")
    public String getMetadataAlbum() {
        return metadataAlbum;
    }

    public void setMetadataAlbum(String metadata_album) {
        this.metadataAlbum = metadata_album;
    }

    @Column(name = "metadata_genre")
    public String getMetadataGenre() {
        return metadataGenre;
    }

    public void setMetadataGenre(String metadataGenre) {
        this.metadataGenre = metadataGenre;
    }

    @Column(name = "metadata_year")
    public String getMetadataYear() {
        return metadataYear;
    }

    public void setMetadataYear(String metadataYear) {
        this.metadataYear = metadataYear;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public UserEntity getUser() {
        return this.user;
    }

    public void setUser(UserEntity user){
        this.user = user;
    }

    @Override
    public String toString() {
        return this.cloudId + " " + this.fileName;
    }



}
