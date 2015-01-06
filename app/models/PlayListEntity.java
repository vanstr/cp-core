package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alex on 9/23/14.
 */
@Entity
@Table(name = "playlist")
public class PlayListEntity extends Model implements Serializable {

    @Id
    private Long id;

    @ManyToOne(targetEntity=UserEntity.class, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String name;

    // TODO
    @Column(nullable = false, columnDefinition = "timestamp NOT NULL DEFAULT 0")
    private Timestamp created;

    @Column(nullable = false, columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updated;

    //TODO why not tested?
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="playlist_song",
            joinColumns={@JoinColumn(name="playlist_id")},
            inverseJoinColumns={@JoinColumn(name="song_id")})
    private Set<SongEntity> songs = new HashSet<SongEntity>(0);

    public static Model.Finder<Long, PlayListEntity> find = new Model.Finder<Long, PlayListEntity>(Long.class, PlayListEntity.class);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

    public Set<SongEntity> getSongs() {
        return songs;
    }

    public void addSongEntities(Collection<SongEntity> songEntityList) {
        this.songs.addAll(songEntityList);
        for(SongEntity songEntity : songEntityList) {
            songEntity.addPlayList(this);
        }
    }

    public void removeSongEntity(SongEntity songEntity) {
        this.songs.remove(songEntity);
        songEntity.removePlayList(this);
    }

    public static PlayListEntity getPlayListById(Long playListId) {
        return find.byId(playListId);
    }

    public static List<PlayListEntity> getPlayListsByFields(Map<String, Object> fields) {
        List<PlayListEntity> songEntities = null;
        if (fields != null && fields.size() > 0) {
            songEntities = find.where().allEq(fields).findList();
        }
        return songEntities;
    }

    public PlayListEntity(UserEntity user, String name){
        setUser(user);
        setName(name);
    }
}
