package models;

import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import play.db.ebean.Model;
import structure.Song;
import structure.SongMetadata;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "song")
public class SongEntity extends Model implements Serializable {

    @Id
    private Long id;

    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne(targetEntity=UserEntity.class, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private Long cloudId;

    @Column(nullable = false)
    private String fileId;

    @Column(nullable = false)
    private String fileName;
    private Long fileSize;
    private Date lastTimeAccessed;
    private String metadataAlbum;
    private String metadataArtist;
    private String metadataGenre;
    private Long metadataLengthSeconds;
    private String metadataTitle;
    private String metadataYear;

    private Set<PlayListEntity> playLists = new HashSet<PlayListEntity>(0);

    public static Model.Finder<Long, SongEntity> find = new Model.Finder<Long, SongEntity>(Long.class, SongEntity.class);

    public SongEntity(){}

    public SongEntity(Song song){
        this.setCloudId(song.getCloudId());
        this.setFileId(song.getFileId());
        this.setFileName(song.getFileName());
        this.setFileSize(song.getFileSize());
        this.setMetadata(song);
    }

    public SongEntity(UserEntity user, Long cloudId, String fileId, String fileName){
        this.setUser(user);
        this.setCloudId(cloudId);
        this.setFileId(fileId);
        this.setFileName(fileName);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Long getCloudId() {
        return cloudId;
    }

    public void setCloudId(Long cloudId) {
        this.cloudId = cloudId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Date getLastTimeAccessed() {
        return lastTimeAccessed;
    }

    public void setLastTimeAccessed(Date lastTimeAccessed) {
        this.lastTimeAccessed = lastTimeAccessed;
    }

    public String getMetadataAlbum() {
        return metadataAlbum;
    }

    public void setMetadataAlbum(String metadataAlbum) {
        this.metadataAlbum = metadataAlbum;
    }

    public String getMetadataArtist() {
        return metadataArtist;
    }

    public void setMetadataArtist(String metadataArtist) {
        this.metadataArtist = metadataArtist;
    }

    public String getMetadataGenre() {
        return metadataGenre;
    }

    public void setMetadataGenre(String metadataGenre) {
        this.metadataGenre = metadataGenre;
    }

    public Long getMetadataLengthSeconds() {
        return metadataLengthSeconds;
    }

    public void setMetadataLengthSeconds(Long metadataLengthSeconds) {
        this.metadataLengthSeconds = metadataLengthSeconds;
    }

    public String getMetadataTitle() {
        return metadataTitle;
    }

    public void setMetadataTitle(String metadataTitle) {
        this.metadataTitle = metadataTitle;
    }

    public String getMetadataYear() {
        return metadataYear;
    }

    public void setMetadataYear(String metadataYear) {
        this.metadataYear = metadataYear;
    }


    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "songs", cascade = CascadeType.ALL)
    public Set<PlayListEntity> getPlayLists() {
        return playLists;
    }

    public void addPlayList(PlayListEntity playList) {
        this.playLists.add(playList);
    }

    public static List<SongEntity> getSongsByFields(Map<String, Object> fields) {
        List<SongEntity> songEntities = null;
        if (fields != null && fields.size() > 0) {
            songEntities = find.where().allEq(fields).findList();
        }
        return songEntities;
    }

    public static SongEntity getSongByFields(Map<String, Object> fields) {
        ExpressionList<SongEntity> expressionList = find.where().allEq(fields);
        SongEntity songEntity = null;
        if(expressionList != null){
            songEntity = expressionList.findUnique();
        }

        return songEntity;
    }


    public static void deleteSongsByID(List<Long> ids) {
        List<SongEntity> songEntities = find.where().idIn(ids).findList();
        for (SongEntity songEntity : songEntities) {
            songEntity.delete();
        }
    }


    public static SongEntity getSongByHash(UserEntity userEntity, Long cloudId, String fileId) {
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("cloud_id", cloudId);
        fieldMap.put("file_id", fileId);
        fieldMap.put("user_id", userEntity.getId());
        SongEntity songEntity = getSongByFields(fieldMap);

        return songEntity;
    }

    public static List<SongEntity> getSongsByMultipleFields(List<Map<String, Object>> fields){
        //TODO with 1 query
        List<SongEntity> result = new ArrayList<SongEntity>();
        for(Map<String, Object> entry : fields){
            result.addAll(getSongsByFields(entry));
        }
        return result;
    }

    public void setMetadata(Song song) {
        SongMetadata metadata = song.getMetadata();
        if (metadata != null) {
            this.setMetadataTitle(metadata.getTitle());
            this.setMetadataAlbum(metadata.getAlbum());
            this.setMetadataArtist(metadata.getArtist());
            this.setMetadataGenre(metadata.getGenre());
            this.setMetadataYear(metadata.getYear());
            this.setMetadataLengthSeconds(metadata.getLengthSeconds());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SongEntity that = (SongEntity) o;

        if (cloudId != that.cloudId) {
            return false;
        }
        if (fileSize != that.fileSize) {
            return false;
        }
        if (id != that.id) {
            return false;
        }
        if (metadataLengthSeconds != that.metadataLengthSeconds) {
            return false;
        }
        if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) {
            return false;
        }
        if (metadataAlbum != null ? !metadataAlbum.equals(that.metadataAlbum) : that.metadataAlbum != null) {
            return false;
        }
        if (metadataArtist != null ? !metadataArtist.equals(that.metadataArtist) : that.metadataArtist != null) {
            return false;
        }
        if (metadataGenre != null ? !metadataGenre.equals(that.metadataGenre) : that.metadataGenre != null) {
            return false;
        }
        if (metadataTitle != null ? !metadataTitle.equals(that.metadataTitle) : that.metadataTitle != null) {
            return false;
        }
        if (metadataYear != null ? !metadataYear.equals(that.metadataYear) : that.metadataYear != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (cloudId ^ (cloudId >>> 32));
        return result;
    }
}
