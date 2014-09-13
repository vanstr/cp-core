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
    SongEntity song = find.where().allEq(fieldMap).findUnique();

    return song;
  }
}
