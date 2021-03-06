package clouds;

import app.BaseModelTest;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Logger;
import structure.Song;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;


public class DropboxTest extends BaseModelTest {


    private static Dropbox dropUnAuth = null; // un authorized dropboz session
    private static Dropbox dropAuth = null; // authorized dropboz session

    private static final String CORRECT_FILE_DROPBOX = "/JUnit/music.mp3";
    private static final String INCORRECT_FILE_DROPBOX = "/balaslkdjasc.mp3";


    @BeforeClass
    public static void method() {
        try {
            dropUnAuth = new Dropbox();

            dropAuth = new Dropbox(originUserEntity.getDropboxAccessKey());
        } catch (Exception e) {
            fail("error in preparing" + e);
        }
        Logger.info("BeforeClass done");
    }

    @Test
    public void testDropboxFail() {
        try {
            new Dropbox(null);
        } catch (Exception ignored) {
            Logger.info("testDropboxFail done");
            return;
        }

        fail("testDropbox wrong result with NULL key pair value");
    }

    @Test
    public void testGetFileLink() {

        // 1. file exists
        String res = null; // http://dl.dropboxusercontent.com/1/view/n8cbbuw08p669ku/JUnit/music.mp3
        try {
            res = dropAuth.getFileLink(CORRECT_FILE_DROPBOX);
        } catch (Exception e) {
            fail("File not found" + e);
        }
        if (!res.endsWith(CORRECT_FILE_DROPBOX)) {
            fail("Bad file link:" + res);
        }

        // 2. file doesnt exist
        String res2 = null;
        try {
            res2 = dropAuth.getFileLink(INCORRECT_FILE_DROPBOX);
        } catch (Exception ignored) {
            Logger.debug("OK, Exception catched");
        }
        assertNull("Bad file link", res2);

        Logger.info("testGetFileLink done");
    }

    @Test
    public void testGetFileList() {

        List<String> fileTypes = new ArrayList<String>();
        fileTypes.add("mp3");

        // 1. should be exception in method because dropbox session not established
        // unworked
        List<Song> res5 = null;
        try {
            res5 = dropUnAuth.getFileList("/", fileTypes);
        }
        catch (Exception ignored) {
            Logger.debug("OK, Exception catched");
        }
        assertNull("Exception", res5);

        // 2.
        List<Song> res = null;
        try {
            res = dropAuth.getFileList("/", fileTypes);
        } catch (Exception e) {
            fail("Exception" + e);
        }

        // check does searched file presents in music list
        boolean filePresents = false;

        for (Song re : res) {
            Logger.debug(re.getFileId());
            if (CORRECT_FILE_DROPBOX.equals(re.getFileId())) {
                filePresents = true;
            }
        }
        assertTrue("Music file not found", filePresents);

        Logger.info("testGetFileList done");
    }
}
