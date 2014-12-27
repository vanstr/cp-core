package structures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import models.PlayListEntity;
import models.SongEntity;
import models.UserEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.1.3
 * Time: 22:03
 * To change this template use File | Settings | File Templates.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayList implements Serializable{
    private long id;
    private String name;
    private List<Song> songs;

    public PlayList(){
        this.songs = new ArrayList<Song>();
    }

    public PlayList(long id, String name){
        this.id = id;
        this.name = name;
        this.songs = new ArrayList<Song>();
    }

    public PlayList(PlayListEntity entity){
        this.id = entity.getId();
        this.name = entity.getName();
        this.songs = new ArrayList<Song>();
        for(SongEntity songEntity : entity.getSongs()){
            this.songs.add(new Song(songEntity));
        }
    }

    public static PlayList mergePlayLists(PlayList playList1, PlayList playList2){
        PlayList playList = new PlayList();
        if(playList1 != null){
            playList.addSongs(playList1.getSongs());
        }
        if(playList2 != null){
            playList.addSongs(playList2.getSongs());
        }
        return playList;
    }

    public static void populate(PlayList playList, long userId) {

        UserEntity userEntity = UserEntity.getUserById(userId);

        if(playList != null) {
            int size = playList.getSongs().size();
            for (int i = 0; i < size; i++) {
                Song song = playList.getSongs().get(i);

                SongEntity songEntity = SongEntity.getSongByHash(userEntity, song);
                if (songEntity != null) {
                    SongMetadata metadata = new SongMetadata(songEntity);
                    song.setMetadata(metadata);
                }
            }
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public void add(Song song){
        this.songs.add(song);
    }

    public void addSongs(List<Song> newSongs){
        this.songs.addAll(newSongs);
    }

}
