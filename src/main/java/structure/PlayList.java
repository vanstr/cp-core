package structure;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.1.3
 * Time: 22:03
 * To change this template use File | Settings | File Templates.
 */
public class PlayList implements Serializable{
    private long id;
    private String name;
    private ArrayList<Song> songs;

    public PlayList(){
        this.songs = new ArrayList<Song>();
    }

    public PlayList(long id, String name){
        this.id = id;
        this.name = name;
        this.songs = new ArrayList<Song>();
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

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void add(Song song){
        this.songs.add(song);
    }
}
