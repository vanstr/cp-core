package models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import play.db.ebean.Model;

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
    private long cloudId;

    @Column(nullable = false)
    private String fileId;

    @Column(nullable = false)
    private String fileName;
    private long fileSize;
    private Date lastTimeAccessed;
    private String metadataAlbum;
    private String metadataArtist;
    private String metadataGenre;
    private int metadataLengthSeconds;
    private String metadataTitle;
    private String metadataYear;

    @Column(nullable = false, columnDefinition="tinyint(1) NOT NULL DEFAULT '0'")
    private Boolean hasMetadata;
    private Set<PlayListEntity> playLists = new HashSet<PlayListEntity>(0);

    public static Model.Finder<Long, SongEntity> find = new Model.Finder<Long, SongEntity>(Long.class, SongEntity.class);

    public SongEntity(){}

    public SongEntity(UserEntity user, Long cloudId, String fileId, String fileName, boolean hasMetadata){
        this.setUser(user);
        this.setCloudId(cloudId);
        this.setFileId(fileId);
        this.setFileName(fileName);
        this.setHasMetadata(hasMetadata);
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

    public long getCloudId() {
        return cloudId;
    }

    public void setCloudId(long cloudId) {
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
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

    public int getMetadataLengthSeconds() {
        return metadataLengthSeconds;
    }

    public void setMetadataLengthSeconds(int metadataLengthSeconds) {
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

    public Boolean getHasMetadata() {
        return hasMetadata;
    }

    public void setHasMetadata(Boolean hasMetadata) {
        this.hasMetadata = hasMetadata;
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
        SongEntity songEntity = find.where().allEq(fields).findUnique();

        return songEntity;
    }


    public static void deleteSongsByID(List<Long> ids) {
        List<SongEntity> songEntities = find.where().idIn(ids).findList();
        for (SongEntity songEntity : songEntities) {
            songEntity.delete();
        }
    }


    public static SongEntity getSongByHash(UserEntity userEntity, long cloudId, String fileName) {
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("cloudId", cloudId);
        fieldMap.put("fileName", fileName);
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
        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        return result;
    }
}
