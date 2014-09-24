package cloudTest;

import app.BaseModelTest;
import clouds.GDrive;
import commons.SystemProperty;
import models.UserEntity;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Logger;
import structure.Song;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 6/22/14
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class GDriveTest extends BaseModelTest {


  private static String INCORRECT_FILE = SystemProperty.getLocalProperties().getProperty("test.drive.incorrect_file");
  private static String FILE_ID = SystemProperty.getLocalProperties().getProperty("test.drive.correct_file_id");
  private static GDrive gDrive = null;

  @BeforeClass
  public static void method() {
    try {

      UserEntity userEntity = UserEntity.getUserById(-1l);
      gDrive = new GDrive(userEntity.driveAccessToken, userEntity.driveRefreshToken);
      String newToken = gDrive.refreshToken(userEntity.driveRefreshToken);
      gDrive.setAccessToken(newToken);
      userEntity.driveAccessToken = newToken;
      userEntity.update();
    }
    catch (Exception e) {
      Logger.warn("Initialization failed", e);
      fail("Initialization failed");
    }
    Logger.info("BeforeClass done");
  }

  @Test
  public void testGetFileList() {
    List<String> fileTypes = new ArrayList<String>();
    fileTypes.add("mp3");
    try {
      List<Song> songList = gDrive.getFileList("/", fileTypes).getSongs();
      assertNotNull(songList);
      boolean isFilePresent = false;
      for (Song song : songList) {
        if (FILE_ID.equals(song.getFileId())) {
          isFilePresent = true;
        }
      }
      assertTrue(isFilePresent);
    }
    catch (Exception e) {
      fail("Error getting file list" + e);
    }
    Logger.info("testGetFileList done");
  }

  @Test
  public void testGetFileLink() {
    try {
      String correctLink = gDrive.getFileLink(FILE_ID);
      assertNotNull("File not found", correctLink);
      assertTrue("File is not correct", correctLink.contains(FILE_ID));
    }
    catch (Exception e) {
      Logger.warn("getFileLink error", e);
      fail("Error getting file link");
    }

    String incorrectLink = null;
    try {
      incorrectLink = gDrive.getFileLink(INCORRECT_FILE);
    }
    catch (Exception e) {
      assertNull("Incorrect file link", incorrectLink);
    }
    Logger.info("testGetFileLink done");
  }
}
