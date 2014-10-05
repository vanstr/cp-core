package models;

import com.avaje.ebean.Ebean;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
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
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity userEntity;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "timestamp NOT NULL DEFAULT 0")
    private Timestamp created;

    @Column(nullable = false, columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updated;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
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

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
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

    public void addSongEntity(SongEntity songEntity) {
        this.songs.add(songEntity);
        songEntity.addPlayList(this);
    }

    public static PlayListEntity getPlayListById(Long playListId) {
        return find.byId(playListId);
    }

    public static List<PlayListEntity> getSongsByFields(Map<String, Object> fields) {
        List<PlayListEntity> songEntities = null;
        if (fields != null && fields.size() > 0) {
            songEntities = find.where().allEq(fields).findList();
        }
        return songEntities;
    }

    public static void deletePlayListById(Long playListId){
        Ebean.delete(PlayListEntity.find.byId(playListId));
    }
}