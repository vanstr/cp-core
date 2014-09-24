package models;

import app.BaseModelTest;
import clouds.Dropbox;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import play.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.19.2
 * Time: 20:12
 * To change this template use File | Settings | File Templates.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SongEntityTest extends BaseModelTest {

  private static Dropbox dropUnAuth = null; // un authorized dropboz session
  private static Dropbox dropAuth = null; // authorized dropboz session

  private static SongEntity originLocalSongEntity = null;

  @BeforeClass
  public static void method() {


    try {
      dropUnAuth = new Dropbox();

      originUserEntity = UserEntity.getUserByField("login", "test");

      dropAuth = new Dropbox(originUserEntity.dropboxAccessKey);
    }
    catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      fail("error in preparing");
    }

  }

  @Test
  public void test1SaveSongs() {
    originLocalSongEntity = new SongEntity();
    originLocalSongEntity.userEntity = originUserEntity;
    originLocalSongEntity.cloudId = 1;
    originLocalSongEntity.fileName = "Saga.mp3";
    originLocalSongEntity.fileSize = 66666;
    originLocalSongEntity.metadataTitle = "Basldlsa dasdas";
    originLocalSongEntity.save();

    SongEntity testSongEntity = SongEntity.find.byId(originLocalSongEntity.id);
    assertThat(testSongEntity).isNotNull();
    assertThat(testSongEntity).isEqualTo(originLocalSongEntity);

    Logger.info("test1SaveSongs done");
  }

  @Test
  public void test2GetSongByFields() {

    Map<String, Object> whereClause = new HashMap<String, Object>();
    whereClause.put("id", originSongEntity.id);
    //whereClause.put("originUser", originUser);
    List<SongEntity> list = SongEntity.getSongsByFields(whereClause);
    Logger.debug("list: " + list);
    assertNotNull(list);
    assertThat(list.contains(originSongEntity)).isTrue();

    Logger.info("test2GetSongs done");
  }

  @Test
  public void test3RemoveSongsById() {

    List<Long> ids = new ArrayList<Long>();
    ids.add(originLocalSongEntity.id);
    SongEntity.deleteSongsByID(ids);
    SongEntity deletedSongEntity = SongEntity.find.byId(originLocalSongEntity.id);
    assertNull("Created song not removed", deletedSongEntity);

    Logger.info("test3RemoveSongsById done");
  }
}
