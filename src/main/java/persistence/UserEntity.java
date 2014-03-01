package persistence;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

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
    private String driveAccessToken;
    private String driveRefreshToken;
    private String googleEmail;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @javax.persistence.Column(name = "id")
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

    @javax.persistence.Column(name = "drive_access_token")
    @Basic
    public String getDriveAccessToken() {
        return driveAccessToken;
    }

    public void setDriveAccessToken(String driveAccessToken) {
        this.driveAccessToken = driveAccessToken;
    }

    @javax.persistence.Column(name = "drive_refresh_token")
    @Basic
    public String getDriveRefreshToken() {
        return driveRefreshToken;
    }

    public void setDriveRefreshToken(String driveRefreshToken) {
        this.driveRefreshToken = driveRefreshToken;
    }

    @javax.persistence.Column(name = "google_email")
    public String getGoogleEmail() {
        return googleEmail;
    }

    public void setGoogleEmail(String googleEmail) {
        this.googleEmail = googleEmail;
    }

    @Override
    public String toString(){

        return ToStringBuilder.reflectionToString(this);

    }
}
