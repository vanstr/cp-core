package persistence;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * UserEntity: vanstr
 * Date: 13.7.7
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
@Table(name = "user", schema = "", catalog = "cloud_player")
@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true
)
public class UserEntity {
    private long id;
    private String login;
    private String password;
    private String dropboxAccessKey;
    private String driveAccessToken;
    private String driveRefreshToken;
    private String googleEmail;
    private String dropboxUid;
    private Long driveTokenExpires;
    private Set<SongEntity> songEntities = new HashSet<SongEntity>(0);

    @Id
    @Column(name = "id")
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "user")
    public Set<SongEntity> getSongEntities() {
        return this.songEntities;
    }

    public void setSongEntities(Set<SongEntity> songEntities) {
        this.songEntities = songEntities;
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

    @javax.persistence.Column(name = "dropbox_uid")
    public String getDropboxUid() {
        return dropboxUid;
    }

    public void setDropboxUid(String dropboxUid) {
        this.dropboxUid = dropboxUid;
    }

    @javax.persistence.Column(name = "drive_token_expires")
    public Long getDriveTokenExpires() {
        return driveTokenExpires;
    }

    public void setDriveTokenExpires(Long driveTokenExpires) {
        this.driveTokenExpires = driveTokenExpires;
    }

    @Override
    public String toString(){
        return "User login:" + this.getLogin();
    }
}
