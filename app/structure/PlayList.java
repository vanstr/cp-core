package structure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import models.PlayListEntity;
import models.SongEntity;

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
    private String nextPageToken;

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

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
}
