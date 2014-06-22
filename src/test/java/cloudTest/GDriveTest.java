package cloudTest;

import cloud.*;
import commons.*;
import junit.framework.*;
import org.junit.*;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.*;
import persistence.*;
import persistence.utility.*;
import structure.*;

import java.util.*;

import static junit.framework.Assert.*;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 6/22/14
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class GDriveTest {

    static final Logger logger = LoggerFactory.getLogger(GDriveTest.class);
    private static String CORRECT_FILE = Initializator.getLocalProperties().getProperty("test.drive.correct_file");
    private static String INCORRECT_FILE = Initializator.getLocalProperties().getProperty("test.drive.incorrect_file");
    private static String FILE_ID = Initializator.getLocalProperties().getProperty("test.drive.correct_file_id");
    private static GDrive gDrive = null;

    @BeforeClass
    public static void method() {
        try{
            UserManager manager = new UserManager();
            UserEntity user = manager.getUserById(-1);
            gDrive = new GDrive(user.getDriveAccessToken(), user.getDriveRefreshToken());
        }catch (Exception e){
            logger.warn("Initialization failed", e);
            fail("Initialization failed");
        }
        logger.info("BeforeClass done");
    }

    @Test
    public void testGetFileList(){
        List<String> fileTypes = new ArrayList<String>();
        fileTypes.add("mp3");
        try {
            List<Song> songList = gDrive.getFileList("/", fileTypes);
            assertNotNull(songList);
            boolean isFilePresent = false;
            for(Song song : songList){
                if(FILE_ID.equals(song.getFileId())){
                    isFilePresent = true;
                }
            }
            assertTrue(isFilePresent);
        }catch (Exception e){
            logger.warn("getFileList error", e);
            fail("Error getting file list");
        }
        logger.info("testGetFileList done");
    }

    @Test
    public void testGetFileLink(){
        try{
            String correctLink = gDrive.getFileLink(FILE_ID);
            assertNotNull("File not found", correctLink);
            assertTrue("File is not correct", correctLink.contains(FILE_ID));
        }catch (Exception e){
            logger.warn("getFileLink error", e);
            fail("Error getting file link");
        }

        String incorrectLink = null;
        try{
            incorrectLink = gDrive.getFileLink(INCORRECT_FILE);
        }catch (Exception e){
            assertNull("Incorrect file link", incorrectLink);
        }
        logger.info("testGetFileLink done");
    }


}
