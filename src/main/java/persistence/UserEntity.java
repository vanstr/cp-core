package persistence;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * UserEntity: vanstr
 * Date: 13.7.7
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
@javax.persistence.Table(name = "user", schema = "", catalog = "cloud_player")
@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true
)
public class UserEntity {
    private long id;
    private String login;
    private String password;
    private String dropboxAccessKey;
    private String dropboxAccessSecret;
    private String dropboxRequestKey;
    private String dropboxRequestSecret;

    @javax.persistence.Column(name = "id")
    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @javax.persistence.Column(name = "login")
    @Basic
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @javax.persistence.Column(name = "password")
    @Basic
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @javax.persistence.Column(name = "dropbox_access_key")
    @Basic
    public String getDropboxAccessKey() {
        return dropboxAccessKey;
    }

    public void setDropboxAccessKey(String token) {
        this.dropboxAccessKey = token;
    }

    @javax.persistence.Column(name = "dropbox_access_secret")
    @Basic
    public String getDropboxAccessSecret() {
        return dropboxAccessSecret;
    }

    public void setDropboxAccessSecret(String token) {
        this.dropboxAccessSecret = token;
    }

    @javax.persistence.Column(name = "dropbox_request_key")
    @Basic
    public String getDropboxRequestKey() {
        return dropboxRequestKey;
    }

    public void setDropboxRequestKey(String token) {
        this.dropboxRequestKey = token;
    }

    @javax.persistence.Column(name = "dropbox_request_secret")
    @Basic
    public String getDropboxRequestSecret() {
        return dropboxRequestSecret;
    }

    public void setDropboxRequestSecret(String token) {
        this.dropboxRequestSecret = token;
    }


    @Override
    public String toString(){

        return ToStringBuilder.reflectionToString(this);

    }
}
