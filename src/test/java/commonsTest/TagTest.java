package commonsTest;

import cloud.Dropbox;
import org.junit.BeforeClass;
import org.junit.Test;
import persistence.SongEntity;
import persistence.SongManager;
import persistence.UserEntity;
import persistence.UserManager;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.19.2
 * Time: 20:12
 * To change this template use File | Settings | File Templates.
 */
public class TagTest {

    private static Dropbox dropUnAuth = null; // un authorized dropboz session
    private static Dropbox dropAuth = null; // authorized dropboz session
    private static UserManager userManager = null;
    private static UserEntity user  = null;
    private static SongManager songManger = null;

    @BeforeClass
    public static void method() {
        try {
            dropUnAuth = new Dropbox();

            // ATTENTION user with 1 id should be JUnit user and with access keys
            userManager = new UserManager();
            songManger = new SongManager();
            user = userManager.getUserById(1);

            dropAuth = new Dropbox(user.getDropboxAccessKey(), user.getDropboxAccessSecret());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            fail("error in preparing");
        }

    }

    @Test
    public void testSaveSongsInformation(){

        SongEntity song1 = new SongEntity();
        song1.setUser(user);
        song1.setCloudId(1);
        song1.setFileName("BAraga.mp3");
        song1.setFileSize(6666666);
        song1.setMetadataTitle("Basldlsa dasdas");
        assertTrue(songManger.addSong(song1));

    }

    @Test
    public void testGetTagsForSongs(){

    }

    @Test
    public void testRemoveTags(){

    }

    @Test
    public void getTagsThatWasNotUsedTooLong(){

    }
}
