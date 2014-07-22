package persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 7/21/14
 * Time: 10:55 PM
 * To change this template use File | Settings | File Templates.
 */
@Table(name = "playlist_song", schema = "", catalog = "cloud_player")
@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true
)
public class PlaylistSongEntity implements Serializable {
    private long playListId;
    private long songId;

    @Id
    @Column(name = "playlist_id")
    public long getPlayListId() {
        return playListId;
    }

    public void setPlayListId(long playListId) {
        this.playListId = playListId;
    }

    @Id
    @Column(name = "song_id")
    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }
}
