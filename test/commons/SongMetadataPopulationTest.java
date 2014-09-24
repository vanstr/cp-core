package commons;

import app.BaseModelTest;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Logger;
import structure.PlayList;
import structure.Song;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.1.3
 * Time: 22:41
 * To change this template use File | Settings | File Templates.
 */
public class SongMetadataPopulationTest extends BaseModelTest {


  @BeforeClass
  public static void method() {
    Logger.info("BeforeClass done");
  }

  @Test
  public void test1PopulatePlaylist() {

    List<Song> data = new ArrayList<Song>();

    Song trackHasMetadata = new Song(originSongEntity.cloudId, originSongEntity.fileName, originSongEntity.fileName, null, null);
    Song trackDoesNotHasMetadata = new Song(1, "NoThatSong", "", null, null);
    data.add(trackHasMetadata);
    data.add(trackDoesNotHasMetadata);

    PlayList playList = SongMetadataPopulation.populate(data, originUserEntity.id);

    Logger.debug("Songs in playlist:" + playList.getSongs().size());
    try {

      for (Song song : playList.getSongs()) {
        if (song.getFileName().equals(originSongEntity.fileName)) {
          Logger.debug("song:" + song.getMetadata().getTitle());
          assertTrue("Incorrect authors", song.getMetadata().getTitle().equals(originSongEntity.metadataTitle));
        }
        else {
          Logger.debug("line " + song.getMetadata());
          assertNull("Methodata should not present", song.getMetadata());
        }
        Logger.debug(song.toString());
      }
    }
    catch (RuntimeException e) {
      Logger.debug("Exception: " + e);
    }
    Logger.info("test1PopulatePlaylist done");
  }
}
