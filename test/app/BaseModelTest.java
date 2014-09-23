package app;

import commons.SystemProperty;
import models.Song;
import models.User;
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


  public static User originUser = null;
  public static Song originSong = null;

  @BeforeClass
  public static void startApp() {

    FakeApplication app = Helpers.fakeApplication(Helpers.inMemoryDatabase());

    testServer = Helpers.testServer(port, app);

    Helpers.start(testServer);


    originUser = createUser();
    originSong = createSong(originUser.id);
  }

  private static Song createSong(long id) {
    Song song = new Song();
    song.user = originUser;
    song.cloudId = SystemProperty.DROPBOX_CLOUD_ID;
    song.fileName = "Shots.mp3";
    song.fileSize = 0;
    song.metadataTitle = "Song title";
    song.save();
    return song;
  }

  private static User createUser() {
    User newUser = new User();
    newUser.dropboxAccessKey = "BAus-dLEjW8AAAAAAAAAAVDysztTsSGkiwlJV7Fm6lvHYxbp0-QdBsyE_Hb_7dYd";
    newUser.dropboxUid = "192670402";
    newUser.driveAccessToken = "7hlztwsgm4v8l2f";
    newUser.driveRefreshToken = "C6jC5Vm8aiRDiNwy";
    newUser.login = "test";
    newUser.password = "123";
    newUser.save();

    return newUser;
  }

  @AfterClass
  public static void stopApp() {
    // delete originXXX
//    originSong.delete();


 //   originUser.delete();

    Helpers.stop(testServer);
  }
}