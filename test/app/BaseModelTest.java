package app;

import commons.SystemProperty;
import models.SongEntity;
import models.UserEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestServer;

/**
 * Created by imi on 22.08.2014..
 */
public class BaseModelTest {
  //public static FakeApplication app;

  public static TestServer testServer;
  public static int port = 3333;
  public static String testServerHost = "http://localhost:" + port;


  public static UserEntity originUser = null;
  public static SongEntity originSong = null;

  @BeforeClass
  public static void startApp() {

    FakeApplication app = Helpers.fakeApplication(Helpers.inMemoryDatabase());

    testServer = Helpers.testServer(port, app);

    Helpers.start(testServer);


    originUser = createUser();
    originSong = createSong(originUser.id);
  }

  private static SongEntity createSong(long id) {
    originSong = new SongEntity();
    originSong.user = originUser;
    originSong.cloudId = SystemProperty.DROPBOX_CLOUD_ID;
    originSong.fileName = "Shots.mp3";
    originSong.fileSize = 0;
    originSong.save();
    return originSong;
  }

  private static UserEntity createUser() {
    UserEntity newUser = new UserEntity();
    newUser.dropboxAccessKey = "BAus-dLEjW8AAAAAAAAAAVDysztTsSGkiwlJV7Fm6lvHYxbp0-QdBsyE_Hb_7dYd";
    newUser.dropboxUid = "192670402";
    newUser.login = "test";
    newUser.password = "123";
    newUser.save();

    return newUser;
  }

  @AfterClass
  public static void stopApp() {
    // delete originXXX
    originSong.delete();
    originUser.delete();

    Helpers.stop(testServer);
  }
}