package models;

import app.BaseModelTest;
import com.avaje.ebean.Ebean;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import play.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.fest.assertions.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SongEntityTest extends BaseModelTest {

    private static SongEntity originLocalSongEntity = null;


    @Test
    public void test1SaveSongs() {
        originLocalSongEntity = new SongEntity();
        originLocalSongEntity.setUser(originUserEntity);
        originLocalSongEntity.setCloudId(1L);
        originLocalSongEntity.setFileName("Saga.mp3");
        originLocalSongEntity.setFileId("Saga.mp3");
        originLocalSongEntity.setFileSize(66666L);
        originLocalSongEntity.setMetadataTitle("Basldlsa dasdas");
        originLocalSongEntity.save();

        SongEntity testSongEntity = SongEntity.find.byId(originLocalSongEntity.getId());
        assertNotNull(testSongEntity);
        Logger.info("test1SaveSongs done");
    }

    @Test
    public void test2GetSongByFields() {
        Map<String, Object> whereClause = new HashMap<String, Object>();
        whereClause.put("id", originLocalSongEntity.getId());
        List<SongEntity> list = SongEntity.getSongsByFields(whereClause);
        assertThat(list).isNotNull();
        assertThat(originLocalSongEntity.getId()).isEqualTo(list.get(0).getId());
        Logger.info("test2GetSongs done");
    }

    @Test
    public void test3UpdateSong(){
        SongEntity existingSong = SongEntity.find.byId(originSongEntity.getId());
        existingSong.setMetadataArtist("artist");
        existingSong.update();
        SongEntity testSongEntity = SongEntity.find.byId(originSongEntity.getId());
        assertThat(testSongEntity.getMetadataArtist()).isEqualTo("artist");
        Logger.info("test3UpdateSong done");
    }

    @Test
    public void test4RemoveSongsById() {
        List<Long> ids = new ArrayList<Long>();
        ids.add(originLocalSongEntity.getId());
        SongEntity.deleteSongsByID(ids);
        SongEntity deletedSongEntity = SongEntity.find.byId(originLocalSongEntity.getId());
        assertNull("Created song not removed", deletedSongEntity);

        Logger.info("test3RemoveSongsById done");
    }

    @Test
    public void test5GetSongsByMultipleFields(){
        SongEntity song1 = new SongEntity(UserEntity.getUserById(1L), 1L, "/test/song1.mp3", "song1.mp3");
        song1.save();
        SongEntity song2 = new SongEntity(UserEntity.getUserById(1L), 2L, "QWERTY123456", "song2.mp3");
        song2.save();
        List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("cloudId", "1");
        map1.put("fileId", "/test/song1.mp3");
        fields.add(map1);
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("cloudId", "2");
        map2.put("fileId", "QWERTY123456");
        fields.add(map2);
        assertThat(SongEntity.getSongsByMultipleFields(fields)).isNotNull();
        assertThat(SongEntity.getSongsByMultipleFields(fields).size()).isEqualTo(2);
        Ebean.delete(song1);
        Ebean.delete(song2);
        Logger.info("test5GetSongsByMultipleFields done");
    }
}
