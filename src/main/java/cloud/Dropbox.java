package cloud;

import com.dropbox.core.*;
import commons.CloudFile;
import commons.HttpWorker;
import commons.Initializator;
import ejb.ContentBeanRemote;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * UserEntity: vanstr
 * Date: 13.29.6
 * Time: 20:55
 * Class represent basic cloud.Dropbox API functionality
 * Like: getFileList, getFileLink
 */
public class Dropbox {

    final static Logger logger = LoggerFactory.getLogger(Dropbox.class);

    // Define application params
    private static final String APP_KEY = Initializator.getLocalProperties().getProperty("dropbox.app.key");
    private static final String APP_SECRET = Initializator.getLocalProperties().getProperty("dropbox.app.secret");
    private static String REDIRECT_URI = Initializator.getLocalProperties().getProperty("dropbox.redirect.uri");
    private static final String GRANT_TYPE_REFRESH = "refresh_token";
    private static final String GRANT_TYPE_AUTHORIZATION = "authorization_code";

    private static final String EXCEPTION_EMPTY_ACCESS_TOKENS = "EXCEPTION_EMPTY_ACCESS_TOKENS";
    private static final String EXCEPTION_EMPTY_REQUEST_TOKENS = "EXCEPTION_EMPTY_REQUEST_TOKENS";
    private static final String EXCEPTION_UNDEFINED_DIR = "EXCEPTION_UNDEFINED_DIR";

    private String accessToken;
    private DbxRequestConfig config = new DbxRequestConfig(
            "JavaTutorial/1.0", Locale.getDefault().toString());
    private DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
    private DbxClient client;

    /**
     * Start session to likn user account with CloudMusic
     */
    public Dropbox() throws Exception {

//        session = new WebAuthSession(appKeys, ACCESS_TYPE);
//        authInfo = session.getAuthInfo();
//
//        logger.debug("instance created");
    }

    public Dropbox(String accessToken) throws Exception {
          this.accessToken = accessToken;
          this.client = new DbxClient(config, accessToken);
//        if (accessTokenKey == null || accessTokenSecret == null) {
//            logger.info("EXCEPTION_EMPTY_ACCESS_TOKENS");
//            throw new Exception(EXCEPTION_EMPTY_ACCESS_TOKENS);
//        }
//
//        AccessTokenPair accessTokenPair = new AccessTokenPair(accessTokenKey, accessTokenSecret);
//        session = new WebAuthSession(appKeys, ACCESS_TYPE);
//        session.setAccessTokenPair(accessTokenPair);
//        api = new DropboxAPI<WebAuthSession>(session);
//
//        logger.debug("debug instance with access key created");

    }

    /**
     * Get file link for downloading
     * @return  file download link
     */
    public String getFileLink(String filePath) throws Exception {
        return client.createTemporaryDirectUrl(filePath).url;
    }

    /**
     * @param folderPath - in which folder look up
     * @param requestedFileTypes  - if file type == NULL return all list, ex: folder, files, mp3, txt
     *
     * @return    array of file
     */
    public List<CloudFile> getFileList(String folderPath, List<String> requestedFileTypes) throws Exception {

        ArrayList<CloudFile> files = new ArrayList<CloudFile>();

        for(String requestedType : requestedFileTypes){
            List<DbxEntry> entryList = client.searchFileAndFolderNames(folderPath, "." + requestedType);
            for(DbxEntry entry : entryList){

                if(CloudFile.checkFileType(entry.asFile().name, requestedFileTypes)){
                    DbxUrlWithExpiration urlWithExpiration = client.createTemporaryDirectUrl(entry.path);
                    CloudFile cloudFile = new CloudFile(ContentBeanRemote.DROPBOX_CLOUD_ID, entry.path,
                            urlWithExpiration.url, urlWithExpiration.expires.getTime());
                    files.add(cloudFile);
                }
            }
        }

//        for(String fileType : requestedFileTypes){
//            List<Entry> dropboxEntries = api.search(folderPath, "." + fileType, 0, false);
//            if(dropboxEntries != null){
//                for(Entry dropboxEntry : dropboxEntries){
//                    if(CloudFile.checkFileType(dropboxEntry.fileName(), requestedFileTypes)){
//                        //TODO maybe url, id?
//                        files.add(new String[]{ContentBeanRemote.DROPBOX_CLOUD_ID.toString()
//                                , dropboxEntry.path, null, null});
//                    }
//                }
//            }
//        }

        return files;
    }

    public OAuth2UserData retrieveAccessToken(String code){
        Map<String, String> params = new HashMap<String, String>();
        params.put("code", code);
        params.put("client_id", APP_KEY);
        params.put("client_secret", APP_SECRET);
        params.put("grant_type", GRANT_TYPE_AUTHORIZATION);
        params.put("redirect_uri", REDIRECT_URI);
        JSONObject object = HttpWorker.sendPostRequest("https://api.dropbox.com/1/oauth2/token", params);
        OAuth2UserData oAuth2UserData = OAuth2UserData.parseDropboxData(object);
        return oAuth2UserData;
    }

//    public void retrieveAndSetAccessToken(String code){
//        OAuth2UserData oAuth2UserData = this.retrieveAccessToken(code);
//        this.accessToken = oAuth2UserData.getAccessToken();
//    }
}