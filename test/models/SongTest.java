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

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.19.2
 * Time: 20:12
 * To change this template use File | Settings | File Templates.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SongTest extends BaseModelTest {

  private static Dropbox dropUnAuth = null; // un authorized dropboz session
  private static Dropbox dropAuth = null; // authorized dropboz session
  private static UserEntity user = null;
  private static SongEntity song1 = null;

  @BeforeClass
  public static void method() {
    try {
      dropUnAuth = new Dropbox();

      user = UserEntity.getUserById(1l);

      dropAuth = new Dropbox(user.dropboxAccessKey);
    }
    catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      fail("error in preparing");
    }

    Logger.info("BeforeClass done");
  }

  @Test
  public void test1SaveSongs() {
    song1 = new SongEntity();
    song1.user = user;
    song1.cloudId = 1;
    song1.fileName = "Saga.mp3";
    song1.fileSize = 66666;
    song1.metadataTitle = "Basldlsa dasdas";
    song1.save();

    assertThat(song1).isNotNull();

    Logger.info("test1SaveSongs done");
  }

  @Test
  public void test2GetSongs() {

    Map<String, Object> whereClause = new HashMap<String, Object>();
    whereClause.put("id", song1.id);
    //whereClause.put("user", user);
    List<SongEntity> list = SongEntity.getSongsByFields(whereClause);
    Logger.debug("list: " + list);
    assertNotNull(list);

    Logger.info("test2GetSongs done");
  }

  @Test
  public void test3RemoveSongsById() {

    List<Long> ids = new ArrayList<Long>();
    ids.add(song1.id);
    SongEntity.deleteSongsByID(ids);
    SongEntity deletedSong = SongEntity.find.byId(song1.id);
    assertNull(deletedSong);


    assertNull("Created song not removed", deletedSong);

    Logger.info("test3RemoveSongsById done");
  }
}
