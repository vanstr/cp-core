package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import play.Logger;
import play.db.ebean.Model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

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

    //TODO why not tested?
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
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
        if (fields != null && !fields.isEmpty()) {
            songEntities = find.where().allEq(fields).findList();
        }
        return songEntities;
    }

    public static void deletePlayListById(Long playListId){
        Ebean.delete(find.byId(playListId));
    }

    public static void deletePlayListsByUserId(Long userId){
        ExpressionList<PlayListEntity> userPlayListsExpression = find.where().eq("user_id", userId);
        List<PlayListEntity> userEntity1 = userPlayListsExpression.findList();
        Logger.debug("before delete: " + userEntity1.size());
        Ebean.delete(userEntity1);

        List<PlayListEntity> userEntity2 = userPlayListsExpression.findList();
        Logger.debug("after delete: " + userEntity2.size());
    }

    public PlayListEntity(){
        setCreated(new Timestamp(System.currentTimeMillis()));
        setUpdated(new Timestamp(System.currentTimeMillis()));
    }
}
