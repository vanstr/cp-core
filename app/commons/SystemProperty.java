package commons;

import play.Play;

import java.io.IOException;
import java.util.Properties;


public class SystemProperty {

    public static String WEB_APP_HOST;
    public static String CORE_APP_HOST;
    public static String APP_KEY;
    public static String APP_SECRET;
    public static String INCORRECT_FILE_DROPBOX;
    public static String CORRECT_FILE_DROPBOX;
    public static String DROPBOX_APP_KEY;
    public static String DROPBOX_APP_SECRET;
    public static String DROPBOX_AUTH_URL;
    public static String DROPBOX_TOKEN_URL;
    public static String DROPBOX_REDIRECT_ADDED;
    public static String DROPBOX_REDIRECT_AUTHORISED;
    public static String DRIVE_CLIENT_ID;
    public static String DRIVE_CLIENT_SECRET;
    public static String DRIVE_REDIRECT_URI;
    public static String DRIVE_AUTH_URL;
    public static String DRIVE_EMAIL_URL;
    public static String DRIVE_EMAIL_SCOPE;
    public static String DRIVE_TOKEN_URL;
    public static String DRIVE_FILES_URL;
    public static String DRIVE_SCOPE;

    public static final Long DROPBOX_CLOUD_ID = 1L;
    public static final Long DRIVE_CLOUD_ID = 2L;

    private static Properties localProperties;


    void fetchProperties() {
        localProperties = new Properties();

        try {
            CORE_APP_HOST = Play.application().configuration().getString("core.app.host");
            WEB_APP_HOST = Play.application().configuration().getString("web.app.host");
            localProperties.load(SystemProperty.class.getClassLoader().getResourceAsStream("local.properties"));
            APP_KEY = localProperties.getProperty("app.key");
            APP_SECRET = localProperties.getProperty("app.secret");
            INCORRECT_FILE_DROPBOX = localProperties.getProperty("test.drive.incorrect_file");
            CORRECT_FILE_DROPBOX = localProperties.getProperty("test.drive.correct_file");
            DROPBOX_APP_KEY = localProperties.getProperty("dropbox.app.key");
            DROPBOX_APP_SECRET = localProperties.getProperty("dropbox.app.secret");
            DROPBOX_REDIRECT_AUTHORISED = CORE_APP_HOST + localProperties.getProperty("dropbox.redirect.authorised");
            DROPBOX_REDIRECT_ADDED = CORE_APP_HOST + localProperties.getProperty("dropbox.redirect.added");
            DROPBOX_TOKEN_URL = localProperties.getProperty("dropbox.token.url");
            DROPBOX_AUTH_URL = localProperties.getProperty("dropbox.auth.url");

            DRIVE_CLIENT_ID = localProperties.getProperty("drive.client.id");
            DRIVE_CLIENT_SECRET = localProperties.getProperty("drive.client.secret");
            DRIVE_REDIRECT_URI = CORE_APP_HOST + localProperties.getProperty("drive.redirect.path");
            DRIVE_EMAIL_URL = localProperties.getProperty("drive.email.url");
            DRIVE_AUTH_URL = localProperties.getProperty("drive.auth.url");
            DRIVE_EMAIL_SCOPE = localProperties.getProperty("drive.email.scope");
            DRIVE_TOKEN_URL = localProperties.getProperty("drive.token.url");
            DRIVE_FILES_URL = localProperties.getProperty("drive.files.url");
            DRIVE_SCOPE = localProperties.getProperty("drive.scope.url");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getLocalProperties() {
        if (localProperties == null) {
            SystemProperty initializator = new SystemProperty();
            initializator.fetchProperties();
            return initializator.getProperties();
        }
        return localProperties;
    }

    public Properties getProperties() {
        return localProperties;
    }
}