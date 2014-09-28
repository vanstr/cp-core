package models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import play.db.ebean.Model;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Song extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @ManyToOne
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    public User user;

    public long cloudId;
    public String fileId;
    public String fileName;
    public long fileSize;
    public Date lastTimeAccessed;
    public String metadataAlbum;
    public String metadataArtist;
    public String metadataGenre;
    public int metadataLengthSeconds;
    public String metadataTitle;
    public String metadataYear;
    public Boolean hasMetadata;

    public static Model.Finder<Long, Song> find = new Model.Finder<Long, Song>(Long.class, Song.class);


    public static List<Song> getSongsByFields(Map<String, Object> fields) {

        List<Song> songs = null;
        if (fields != null && fields.size() > 0) {
            songs = find.where().allEq(fields).findList();
        }
        return songs;
    }
    public static Song getSongByFields(Map<String, Object> fields) {
        Song song = find.where().allEq(fields).findUnique();

        return song;
    }


    public static void deleteSongsByID(List<Long> ids) {
        List<Song> songs = find.where().idIn(ids).findList();
        for (Song song : songs) {
            song.delete();
        }
    }


    public static Song getSongByHash(User user, long cloudId, String fileName) {

        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("cloudId", cloudId);
        fieldMap.put("fileName", fileName);
        fieldMap.put("user.id", user.id);
        Song song = getSongByFields(fieldMap);

        return song;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Song that = (Song) o;

        if (cloudId != that.cloudId) {
            return false;
        }
        if (fileSize != that.fileSize) {
            return false;
        }
        if (id != that.id) {
            return false;
        }
        if (metadataLengthSeconds != that.metadataLengthSeconds) {
            return false;
        }
        if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) {
            return false;
        }
        if (metadataAlbum != null ? !metadataAlbum.equals(that.metadataAlbum) : that.metadataAlbum != null) {
            return false;
        }
        if (metadataArtist != null ? !metadataArtist.equals(that.metadataArtist) : that.metadataArtist != null) {
            return false;
        }
        if (metadataGenre != null ? !metadataGenre.equals(that.metadataGenre) : that.metadataGenre != null) {
            return false;
        }
        if (metadataTitle != null ? !metadataTitle.equals(that.metadataTitle) : that.metadataTitle != null) {
            return false;
        }
        if (metadataYear != null ? !metadataYear.equals(that.metadataYear) : that.metadataYear != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (cloudId ^ (cloudId >>> 32));
        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        return result;
    }
}
