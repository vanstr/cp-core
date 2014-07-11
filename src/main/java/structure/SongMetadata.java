package structure;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.1.3
 * Time: 20:56
 * To change this template use File | Settings | File Templates.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SongMetadata implements Serializable{
    private String title;
    private String artist;
    private String album;
    private long lengthSeconds;
    private String year;
    private String genre;

    public SongMetadata(){}

    public SongMetadata(String title, String artist, String album, long length, String year, String genre) {
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.year = year;
        this.genre = genre;
        this.lengthSeconds = length;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getLengthSeconds() {
        return lengthSeconds;
    }

    public void setLengthSeconds(long lengthSeconds) {
        this.lengthSeconds = lengthSeconds;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String toString(){
        return title + " " + album + " " + artist;
    }

}
