package structure;

import persistence.SongEntity;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.1.3
 * Time: 20:56
 * To change this template use File | Settings | File Templates.
 */
public class SongMetadata implements Serializable{
    private String title;
    private String author;
    private String album;
    private long lengthSeconds;

    public SongMetadata(SongEntity songEntity) {
        if (songEntity != null) {
            this.title = songEntity.getMetadataTitle();
            this.album = songEntity.getMetadataAlbum();
            this.author = songEntity.getMetadataAuthor();
            this.lengthSeconds = songEntity.getMetadataLengthSeconds();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String toString(){
        return title + " " + album + " " + author;
    }

}
