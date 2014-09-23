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
public class SongTest extends BaseModelTest {

  private static Dropbox dropUnAuth = null; // un authorized dropboz session
  private static Dropbox dropAuth = null; // authorized dropboz session

  private static Song originLocalSong = null;

  @BeforeClass
  public static void method() {


    try {
      dropUnAuth = new Dropbox();

      originUser = User.getUserByField("login", "test");

      dropAuth = new Dropbox(originUser.dropboxAccessKey);
    }
    catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      fail("error in preparing");
    }

  }

  @Test
  public void test1SaveSongs() {
    originLocalSong = new Song();
    originLocalSong.user = originUser;
    originLocalSong.cloudId = 1;
    originLocalSong.fileName = "Saga.mp3";
    originLocalSong.fileSize = 66666;
    originLocalSong.metadataTitle = "Basldlsa dasdas";
    originLocalSong.save();

    Song testSong = Song.find.byId(originLocalSong.id);
    assertThat(testSong).isNotNull();
    assertThat(testSong).isEqualTo(originLocalSong);

    Logger.info("test1SaveSongs done");
  }

  @Test
  public void test2GetSongByFields() {

    Map<String, Object> whereClause = new HashMap<String, Object>();
    whereClause.put("id", originSong.id);
    //whereClause.put("originUser", originUser);
    List<Song> list = Song.getSongsByFields(whereClause);
    Logger.debug("list: " + list);
    assertNotNull(list);
    assertThat(list.contains(originSong)).isTrue();

    Logger.info("test2GetSongs done");
  }

  @Test
  public void test3RemoveSongsById() {

    List<Long> ids = new ArrayList<Long>();
    ids.add(originLocalSong.id);
    Song.deleteSongsByID(ids);
    Song deletedSong = Song.find.byId(originLocalSong.id);
    assertNull("Created song not removed", deletedSong);

    Logger.info("test3RemoveSongsById done");
  }
}
