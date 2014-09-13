package models;

import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.*;

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
    List<UserEntity> users = null;
    if (fields != null && fields.size() > 0) {
      users = find.where().allEq(fields).findList();
    }
    return users;
  }

  public static UserEntity getUserByField(String propertyName, Object value) {
    Map<String, Object> fieldMap = new HashMap<String, Object>();
    fieldMap.put(propertyName, value);
    return getUserByFields(fieldMap);

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

  public static UserEntity getUserById(Long userId) {
    return find.byId(userId);
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

    UserEntity that = (UserEntity) o;

    if (id != that.id) {
      return false;
    }
    if (dropboxUid != null ? !dropboxUid.equals(that.dropboxUid) : that.dropboxUid != null) {
      return false;
    }
    if (googleEmail != null ? !googleEmail.equals(that.googleEmail) : that.googleEmail != null) {
      return false;
    }
    if (login != null ? !login.equals(that.login) : that.login != null) {
      return false;
    }
    if (password != null ? !password.equals(that.password) : that.password != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (int) (id ^ (id >>> 32));
    result = 31 * result + (login != null ? login.hashCode() : 0);
    return result;
  }
}