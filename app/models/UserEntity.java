package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 13.7.7
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "user")
public class UserEntity extends Model implements Serializable {

    @Id
    private Long id;

    @Column(nullable = false, columnDefinition="varchar(255) NOT NULL")
    private String login;

    @JsonIgnore
    @Column(nullable = false, columnDefinition="varchar(255) NOT NULL")
    private String password;

    @JsonIgnore
    private String dropboxAccessKey;

    @JsonIgnore
    private String driveAccessToken;

    @JsonIgnore
    private String driveRefreshToken;

    private String googleEmail;

    private String dropboxUid;

    private Long driveTokenExpires;

    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    @OneToMany(mappedBy = "user")
    private List<SongEntity> songEntities = new ArrayList<SongEntity>(0);

    public static Model.Finder<Long, UserEntity> find = new Model.Finder<Long, UserEntity>(Long.class, UserEntity.class);

    public UserEntity(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDropboxAccessKey() {
        return dropboxAccessKey;
    }

    public void setDropboxAccessKey(String dropboxAccessKey) {
        this.dropboxAccessKey = dropboxAccessKey;
    }

    public String getDriveAccessToken() {
        return driveAccessToken;
    }

    public void setDriveAccessToken(String driveAccessToken) {
        this.driveAccessToken = driveAccessToken;
    }

    public String getDriveRefreshToken() {
        return driveRefreshToken;
    }

    public void setDriveRefreshToken(String driveRefreshToken) {
        this.driveRefreshToken = driveRefreshToken;
    }

    public String getGoogleEmail() {
        return googleEmail;
    }

    public void setGoogleEmail(String googleEmail) {
        this.googleEmail = googleEmail;
    }

    public String getDropboxUid() {
        return dropboxUid;
    }

    public void setDropboxUid(String dropboxUid) {
        this.dropboxUid = dropboxUid;
    }

    public Long getDriveTokenExpires() {
        return driveTokenExpires;
    }

    public void setDriveTokenExpires(Long driveTokenExpires) {
        this.driveTokenExpires = driveTokenExpires;
    }

    public List<SongEntity> getSongEntities() {
        return songEntities;
    }

    public void addSongEntity(SongEntity songEntity) {
        this.songEntities.add(songEntity);
        if(!songEntity.getUser().equals(this)){
            songEntity.setUser(this);
            songEntity.update();
        }
    }

    public static List<UserEntity> getUsersByFields(Map<String, Object> fields) {
        List<UserEntity> userEntities = null;
        if (fields != null && fields.size() > 0) {
            userEntities = find.where().allEq(fields).findList();
        }
        return userEntities;
    }

    public static UserEntity getUserByField(String propertyName, Object value) {
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put(propertyName, value);
        return getUserByFields(fieldMap);

    }

    public static UserEntity getUserByFields(Map<String, Object> fields) {
        UserEntity userEntity = null;
        if (fields != null && fields.size() > 0) {
            userEntity = find.where().allEq(fields).findUnique();
        }
        return userEntity;
    }

    public static UserEntity getUserById(Long userId) {
        return find.byId(userId);
    }

    public static void deleteUserById(Long userId){
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("user_id", userId);
        Ebean.delete(PlayListEntity.getPlayListsByFields(fields));
        Ebean.delete(SongEntity.getSongsByFields(fields));
        Ebean.delete(UserEntity.getUserById(userId));
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