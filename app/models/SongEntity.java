package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Junction;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class SongEntity extends Model {

    @Id
    public long id;

    @ManyToOne
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    public UserEntity userEntity;

    public long cloudId;
    public String fileId;
    public String fileName;
    public long fileSize;
    public Date lastTimeAccessed;
    public String metadataAlbum;
    public String metadataArtist;
    public String metadataGenre;
    public int metadataLengthSeconds;
    public String metadataTitle;
    public String metadataYear;
    public Boolean hasMetadata;

    public static Model.Finder<Long, SongEntity> find = new Model.Finder<Long, SongEntity>(Long.class, SongEntity.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
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
        fieldMap.put("user_id", userEntity.id);
        SongEntity songEntity = getSongByFields(fieldMap);

        return songEntity;
    }

    public static List<SongEntity> getSongsByMultipleFields(List<Map<String, Object>> fields){
        List<SongEntity> result = null;
        Query query = Ebean.createQuery(SongEntity.class);
        Junction junction = query.where().disjunction();
        for(Map<String, Object> entry : fields){
            junction.add(Expr.allEq(entry));
        }
        result = junction.endJunction().findList();
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
