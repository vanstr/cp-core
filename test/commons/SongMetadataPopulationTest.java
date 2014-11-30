package commons;

import app.BaseModelTest;
import org.junit.Test;
import play.Logger;
import structure.PlayList;
import structure.Song;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class SongMetadataPopulationTest extends BaseModelTest {


    @Test
    public void test1PopulatePlaylist() {

        List<Song> data = new ArrayList<Song>();

        Song trackHasMetadata = new Song(originSongEntity.getCloudId(), originSongEntity.getFileId(),
                originSongEntity.getFileName(), null, null);
        Song trackDoesNotHasMetadata = new Song(SystemProperty.DROPBOX_CLOUD_ID, "NoThatSong", "", null, null);
        data.add(trackHasMetadata);
        data.add(trackDoesNotHasMetadata);

        PlayList playList = SongMetadataPopulation.populate(data, originUserEntity.getId());

        Logger.debug("Songs in playlist:" + playList.getSongs().size());
        try {
            for (Song song : playList.getSongs()) {
                if (song.getFileName().equals(originSongEntity.getFileName())) {
                    Logger.debug("song:" + song.getMetadata().getTitle());
                    assertTrue("Incorrect authors", song.getMetadata().getTitle()
                            .equals(originSongEntity.getMetadataTitle()));
                } else {
                    Logger.debug("line " + song.getMetadata());
                    assertNull("Metadata should not present", song.getMetadata());
                }
                Logger.debug(song.toString());
            }
        } catch (RuntimeException e) {
            Logger.debug("Exception: " + e);
        }
        Logger.info("test1PopulatePlaylist done");
    }
}
