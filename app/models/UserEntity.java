package models;

import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * UserEntity: vanstr
 * Date: 13.7.7
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class UserEntity extends Model {

  @Id
  public long id;

  public String login;

  public String password;

  public String dropboxAccessKey;

  public String driveAccessToken;

  public String driveRefreshToken;

  public String googleEmail;

  public String dropboxUid;

  public Long driveTokenExpires;

  @OneToMany(cascade = CascadeType.ALL)
  private Set<SongEntity> songEntities = new HashSet<SongEntity>(0);

  public static Model.Finder<Long, UserEntity> find = new Model.Finder<Long, UserEntity>(Long.class, UserEntity.class);

  public static List<UserEntity> getUsersByFields(Map<String, Object> fields) {
    List<UserEntity> songs = null;
    if (fields != null && fields.size() > 0) {
      songs = find.where().allEq(fields).findList();
    }
    return songs;
  }

  public static UserEntity getUserByFields(Map<String, Object> fields) {
    UserEntity user = null;
    if (fields != null && fields.size() > 0) {
      user = find.where().allEq(fields).findUnique();
    }
    return user;
  }


  public static void deleteUserByID(Long id) {
    UserEntity user = find.where().eq("id", id).findUnique();
    user.delete();
  }
}