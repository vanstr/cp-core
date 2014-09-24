package models;

import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by alex on 9/23/14.
 */
public class PlayListEntity extends Model {
    private long id;
    private UserEntity userEntity;
    private String name;
    private Timestamp created;
    private Timestamp updated;
    private Set<SongEntity> songEntities = new HashSet<SongEntity>(0);

    public static Model.Finder<Long, PlayListEntity> find = new Model.Finder<Long, PlayListEntity>(Long.class, PlayListEntity.class);

    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name="playlist_song",
            joinColumns={@JoinColumn(name="playlist_id")},
            inverseJoinColumns={@JoinColumn(name="song_id")})
    public Set<SongEntity> getSongEntities() {
        return songEntities;
    }

    public void setSongEntities(Set<SongEntity> songEntities) {
        this.songEntities = songEntities;
    }

    public static PlayListEntity getUserById(Long playListId) {
        return find.byId(playListId);
    }
}
