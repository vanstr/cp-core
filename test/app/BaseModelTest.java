package app;

import com.avaje.ebean.Ebean;
import commons.PasswordService;
import commons.SystemProperty;
import models.PlayListEntity;
import models.SongEntity;
import models.UserEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import play.Logger;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestServer;

import java.util.HashMap;
import java.util.Map;


public class BaseModelTest {

    public static final int port = 3333;

    public static TestServer testServer;

    public static final String USER_ID = "1";
    public static UserEntity originUserEntity = null;
    public static SongEntity originSongEntity = null;

    @BeforeClass
    public static void startApp() {

        Map<String, String> settings = getSettings();
        FakeApplication app = Helpers.fakeApplication(settings);

        testServer = Helpers.testServer(port, app);

        Helpers.start(testServer);
        prepareDB();
        createUsers();

        originUserEntity = UserEntity.getUserById(1L);
        originSongEntity = createSong(originUserEntity.getId());
    }

    private static Map<String, String> getSettings() {
        String dbPass = ((System.getenv("MYSQL_PASSWORD") != null ) ? System.getenv("MYSQL_PASSWORD") : "123");
        String dbUser = ((System.getenv("MYSQL_USER") != null ) ? System.getenv("MYSQL_USER") : "admin");
        String dbName = ((System.getenv("CP_MYSQL_DB_NAME") != null ) ? System.getenv("CP_MYSQL_DB_NAME") : "cloud_player_test");

        Logger.debug("dbName :" + dbName + " dbUser: " + dbUser + " dbPass: " + dbPass);

        Map<String, String> settings = new HashMap<String, String>();
        settings.put("db.default.url", "jdbc:mysql://localhost:3306/" + dbName + "?characterEncoding=UTF-8");
        settings.put("db.default.user", dbUser);
        settings.put("db.default.password", dbPass);
        settings.put("db.default.jndiName", "DefaultDS");
        return settings;
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

    private static void createUsers() {

        UserEntity gDriveUser = new UserEntity();
        gDriveUser.setId(2L);
        gDriveUser.setLogin("gdrive");
        gDriveUser.setPassword(PasswordService.encrypt("123456"));
        gDriveUser.setDriveAccessToken("ya29.hADeLWkw9ImDLr7p7hivANfWYhI8fJcfNBESB9pBJ9y3S5VyhyuJdQLY");
        gDriveUser.setDriveRefreshToken("1/sTej2wr_j-D3XYL0yVkrWoyBNuRyZn9N7qlMWZRnuPk");
        gDriveUser.setGoogleEmail("cp.cloudplayer@gmail.com");
        gDriveUser.setDriveTokenExpires(1403469638767L);
        gDriveUser.save();

        UserEntity dropboxUserEntry = new UserEntity();
        dropboxUserEntry.setId(Long.parseLong(USER_ID));
        dropboxUserEntry.setDropboxAccessKey("BAus-dLEjW8AAAAAAAAAAVDysztTsSGkiwlJV7Fm6lvHYxbp0-QdBsyE_Hb_7dYd");
        dropboxUserEntry.setDropboxUid("192670402");
        dropboxUserEntry.setDriveAccessToken("7hlztwsgm4v8l2f");
        dropboxUserEntry.setDriveRefreshToken("C6jC5Vm8aiRDiNwy");
        dropboxUserEntry.setLogin("dropbox");
        dropboxUserEntry.setPassword(PasswordService.encrypt("123"));
        dropboxUserEntry.save();

    }

    private static void prepareDB(){
        Ebean.delete(SongEntity.find.all());
        Ebean.delete(PlayListEntity.find.all());
        Ebean.delete(UserEntity.find.all());
    }

    @AfterClass
    public static void stopApp() {
        Helpers.stop(testServer);
    }
}