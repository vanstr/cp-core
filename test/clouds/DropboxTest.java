package clouds;

import app.BaseModelTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Logger;
import structures.Song;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


public class DropboxTest extends BaseModelTest {


    private static Cloud dropUnAuth = null; // un authorized dropboz session
    private static Cloud dropAuth = null; // authorized dropboz session

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
        // http://dl.dropboxusercontent.com/1/view/n8cbbuw08p669ku/JUnit/music.mp3
        String fileLink = dropAuth.getFileLink(CORRECT_FILE_DROPBOX);
        assertNotNull("File not found", fileLink);
        assertTrue("File is not correct", fileLink.contains(CORRECT_FILE_DROPBOX));

        if (!fileLink.endsWith(CORRECT_FILE_DROPBOX)) {
            fail("Bad file link:" + fileLink);
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
        } catch (Exception ignored) {
            Logger.debug("OK, Exception catched");
        }
        assertNull("Exception", res5);

        // 2.
        List<Song> res = null;
        try {
            res = dropAuth.getFileList("/", fileTypes);
            Assert.assertTrue(!res.isEmpty());
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

    @Test
    public void testUploadFileByUrl() throws MalformedURLException {

        String fileDest = "/junit/tests/copiedMytestFile.txt";
        URL url = new URL("https://www.dropbox.com/s/ibdqp9r9ldgvoz7/mytestFile.txt?dl=0");
        //URL url = new URL("https://wordpress.org/plugins/about/readme.txt");
        Boolean res = dropAuth.uploadFileByUrl(fileDest, url);
        assertTrue("Failed to upload", res);

        Logger.info("testUploadFileSuccess done");
    }


}
