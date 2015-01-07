package models;

import app.BaseModelTest;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;

/**
 * Created by alex on 10/1/14.
 */
public class PlayListEntityTest extends BaseModelTest {
    @Test
    public void testCreatePlayList(){
        UserEntity user = UserEntity.getUserById(1L);
        PlayListEntity playListEntity = new PlayListEntity(user, "My test playlist");
        List<SongEntity> songEntityList = new ArrayList<SongEntity>();
        songEntityList.add(originSongEntity);
        playListEntity.addSongEntities(songEntityList);
        playListEntity.setCreated(new Timestamp(System.currentTimeMillis()));
        playListEntity.setUpdated(new Timestamp(System.currentTimeMillis()));
        playListEntity.save();

        assertFalse(PlayListEntity.find.all().isEmpty());
    }

    //TODO
    @Test
    public void testDeleteUserPlaylists(){
        // Create two playlists for user
        // call method
        // check that user playlist count is 0
    }

}
