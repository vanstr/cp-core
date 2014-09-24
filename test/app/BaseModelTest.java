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


  public static UserEntity originUserEntity = null;
  public static SongEntity originSongEntity = null;

  @BeforeClass
  public static void startApp() {

    FakeApplication app = Helpers.fakeApplication(Helpers.inMemoryDatabase());

    testServer = Helpers.testServer(port, app);

    Helpers.start(testServer);


    originUserEntity = createUser();
    originSongEntity = createSong(originUserEntity.id);
  }

  private static SongEntity createSong(long id) {
    SongEntity songEntity = new SongEntity();
    songEntity.userEntity = originUserEntity;
    songEntity.cloudId = SystemProperty.DROPBOX_CLOUD_ID;
    songEntity.fileName = "Shots.mp3";
    songEntity.fileSize = 0;
    songEntity.metadataTitle = "Song title";
    songEntity.save();
    return songEntity;
  }

  private static UserEntity createUser() {
    UserEntity newUserEntity = new UserEntity();
    newUserEntity.dropboxAccessKey = "BAus-dLEjW8AAAAAAAAAAVDysztTsSGkiwlJV7Fm6lvHYxbp0-QdBsyE_Hb_7dYd";
    newUserEntity.dropboxUid = "192670402";
    newUserEntity.driveAccessToken = "7hlztwsgm4v8l2f";
    newUserEntity.driveRefreshToken = "C6jC5Vm8aiRDiNwy";
    newUserEntity.login = "test";
    newUserEntity.password = "123";
    newUserEntity.save();

    return newUserEntity;
  }

  @AfterClass
  public static void stopApp() {
    // delete originXXX
//    originSong.delete();


 //   originUser.delete();

    Helpers.stop(testServer);
  }
}