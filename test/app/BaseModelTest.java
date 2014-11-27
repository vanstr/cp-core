package app;

import com.avaje.ebean.Ebean;
import commons.SystemProperty;
import models.PlayListEntity;
import models.SongEntity;
import models.UserEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestServer;

import java.util.HashMap;
import java.util.Map;


public class BaseModelTest {

    public static final int port = 3333;

    public static TestServer testServer;

    public static UserEntity originUserEntity = null;
    public static SongEntity originSongEntity = null;

    @BeforeClass
    public static void startApp() {

        String dbPass = ((System.getenv("MYSQL_PASSWORD") != null ) ? System.getenv("MYSQL_PASSWORD") : "123");
        String dbUser = ((System.getenv("MYSQL_USER") != null ) ? System.getenv("MYSQL_USER") : "admin");
        String dbName = ((System.getenv("CP_MYSQL_DB_NAME") != null ) ? System.getenv("CP_MYSQL_DB_NAME") : "cloud_player_test");


        Map<String, String> settings = new HashMap<String, String>();
        settings.put("db.default.url", "jdbc:mysql://localhost:3306/" + dbName + "?characterEncoding=UTF-8");
        settings.put("db.default.user", dbUser);
        settings.put("db.default.password", dbPass);
        settings.put("db.default.jndiName", "DefaultDS");
        FakeApplication app = Helpers.fakeApplication(settings);

        testServer = Helpers.testServer(port, app);

        Helpers.start(testServer);
        prepareDB();
        originUserEntity = createUsers();
        originSongEntity = createSong(originUserEntity.getId());
    }

    private static SongEntity createSong(long id) {
        SongEntity songEntity = new SongEntity();
        songEntity.setUser(originUserEntity);
        songEntity.setCloudId(SystemProperty.DROPBOX_CLOUD_ID);
        songEntity.setFileId("Shots.mp3");
        songEntity.setFileName("Shots.mp3");
        songEntity.setFileSize(0L);
        songEntity.setMetadataTitle("Song title");
        songEntity.save();
        return songEntity;
    }

    private static UserEntity createUsers() {
//        Ebean.delete(PlayListEntity.find.all());
        UserEntity dropboxUser = new UserEntity();
        dropboxUser.setId(1L);
        dropboxUser.setLogin("dropbox");
        dropboxUser.setPassword("123456");
        dropboxUser.setDropboxAccessKey("uDdND44fHTAAAAAAAAAADG8zqeAjiSUyJz949D7c00gXz9rQPgh51yv-cnmLJlHW");
        dropboxUser.setDropboxUid("192670402");
        dropboxUser.save();

        UserEntity gDriveUser = new UserEntity();
        gDriveUser.setId(2L);
        gDriveUser.setLogin("gdrive");
        gDriveUser.setPassword("123456");
        gDriveUser.setDriveAccessToken("ya29.hADeLWkw9ImDLr7p7hivANfWYhI8fJcfNBESB9pBJ9y3S5VyhyuJdQLY");
        gDriveUser.setDriveRefreshToken("1/sTej2wr_j-D3XYL0yVkrWoyBNuRyZn9N7qlMWZRnuPk");
        gDriveUser.setGoogleEmail("cp.cloudplayer@gmail.com");
        gDriveUser.setDriveTokenExpires(1403469638767L);
        gDriveUser.save();

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setId(3L);
        newUserEntity.setDropboxAccessKey("BAus-dLEjW8AAAAAAAAAAVDysztTsSGkiwlJV7Fm6lvHYxbp0-QdBsyE_Hb_7dYd");
        newUserEntity.setDropboxUid("192670402");
        newUserEntity.setDriveAccessToken("7hlztwsgm4v8l2f");
        newUserEntity.setDriveRefreshToken("C6jC5Vm8aiRDiNwy");
        newUserEntity.setLogin("test");
        newUserEntity.setPassword("123");
        newUserEntity.save();

        return newUserEntity;
    }

    private static void prepareDB(){
        Ebean.delete(SongEntity.find.all());
        Ebean.delete(PlayListEntity.find.all());
        Ebean.delete(UserEntity.find.all());
    }

    @AfterClass
    public static void stopApp() {
        // delete originXXX
//    originSong.delete();


        //   originUser.delete();

        Helpers.stop(testServer);
    }
}