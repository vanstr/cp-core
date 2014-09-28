package models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 13.7.7
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class User extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String login;

    @JsonIgnore
    public String password;
    @JsonIgnore
    public String dropboxAccessKey;
    @JsonIgnore
    public String driveAccessToken;
    @JsonIgnore
    public String driveRefreshToken;

    public String googleEmail;

    public String dropboxUid;

    public Long driveTokenExpires;

    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Song> songEntities = new HashSet<Song>(0);

    public static Model.Finder<Long, User> find = new Model.Finder<Long, User>(Long.class, User.class);

    public static List<User> getUsersByFields(Map<String, Object> fields) {
        List<User> users = null;
        if (fields != null && fields.size() > 0) {
            users = find.where().allEq(fields).findList();
        }
        return users;
    }

    public static User getUserByField(String propertyName, Object value) {
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put(propertyName, value);
        return getUserByFields(fieldMap);

    }

    public static User getUserByFields(Map<String, Object> fields) {
        User user = null;
        if (fields != null && fields.size() > 0) {
            user = find.where().allEq(fields).findUnique();
        }
        return user;
    }


    public static void deleteUserByID(Long id) {
        User user = find.where().eq("id", id).findUnique();
        user.delete();
    }

    public static User getUserById(Long userId) {
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

        User that = (User) o;

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