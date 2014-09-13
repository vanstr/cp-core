package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class SongEntity extends Model {

  @Id
  public long id;
  public long cloudId;
  public Date lastTimeAccessed;
  public String fileName;
  public long fileSize;
  public String metadataTitle;
  public String metadataArtist;
  public String metadataAlbum;
  public String metadataYear;
  public String metadataGenre;
  public int metadataLengthSeconds;

  @ManyToOne
  public UserEntity user;


  public static Model.Finder<Long, SongEntity> find = new Model.Finder<Long, SongEntity>(Long.class, SongEntity.class);


  public static List<SongEntity> getSongsByFields(Map<String, Object> fields) {

    List<SongEntity> songs = null;
    if (fields != null && fields.size() > 0) {
      songs = find.where().allEq(fields).findList();
    }
    return songs;
  }
  public static SongEntity getSongByFields(Map<String, Object> fields) {
    SongEntity song = find.where().allEq(fields).findUnique();

    return song;
  }


  public static void deleteSongsByID(List<Long> ids) {
    List<SongEntity> songs = find.where().idIn(ids).findList();
    for (SongEntity song : songs) {
      song.delete();
    }
  }


  public static SongEntity getSongByHash(UserEntity user, long cloudId, String fileName) {

    Map<String, Object> fieldMap = new HashMap<String, Object>();
    fieldMap.put("cloudId", cloudId);
    fieldMap.put("fileName", fileName);
    fieldMap.put("user", user);
    SongEntity song = getSongByFields(fieldMap);

    return song;
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
