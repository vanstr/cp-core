package models;

import app.BaseModelTest;
import org.junit.Test;

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
        PlayListEntity playListEntity = new PlayListEntity();
        playListEntity.setName("My test playlist");

        playListEntity.setUserEntity(user);
        List<SongEntity> songEntityList = new ArrayList<SongEntity>();
        songEntityList.add(originSongEntity);
        playListEntity.addSongEntities(songEntityList);
        playListEntity.save();

        assertFalse(PlayListEntity.find.all().isEmpty());
    }
}
