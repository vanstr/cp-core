package cloud;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxUrlWithExpiration;
import commons.CloudFile;
import commons.HttpWorker;
import commons.Initializator;
import ejb.ContentBeanRemote;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import structure.Song;

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
    private static String DROPBOX_TOKEN_URL = Initializator.getLocalProperties().getProperty("dropbox.token.url");
    private static final String GRANT_TYPE_REFRESH = "refresh_token";
    private static final String GRANT_TYPE_AUTHORIZATION = "authorization_code";

    private static final String EXCEPTION_EMPTY_ACCESS_TOKENS = "EXCEPTION_EMPTY_ACCESS_TOKENS";
    private static final String EXCEPTION_EMPTY_REQUEST_TOKENS = "EXCEPTION_EMPTY_REQUEST_TOKENS";
    private static final String EXCEPTION_UNDEFINED_DIR = "EXCEPTION_UNDEFINED_DIR";

    private String accessToken;
    private DbxRequestConfig config = new DbxRequestConfig(
            "Cloud_Player", Locale.getDefault().toString());
    private DbxClient client;

    public Dropbox(){}

    public Dropbox(String accessToken) throws Exception {
          this.accessToken = accessToken;
          this.client = new DbxClient(config, accessToken);
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
    public List<Song> getFileList(String folderPath, List<String> requestedFileTypes) throws Exception {

        ArrayList<Song> files = new ArrayList<Song>();

        for(String requestedType : requestedFileTypes){
            List<DbxEntry> entryList = client.searchFileAndFolderNames(folderPath, "." + requestedType);
            for(DbxEntry entry : entryList){

                if(entry.isFile() && CloudFile.checkFileType(entry.asFile().name, requestedFileTypes)){
                    DbxUrlWithExpiration urlWithExpiration = client.createTemporaryDirectUrl(entry.path);

                    files.add(new Song( ContentBeanRemote.DROPBOX_CLOUD_ID,
                            entry.path,
                            getFileNameFromFilePath(entry.path),
                            urlWithExpiration.url,
                            urlWithExpiration.expires.getTime())
                    );
                }
            }
        }

        return files;
    }

    public OAuth2UserData retrieveAccessToken(String code){
        Map<String, String> params = new HashMap<String, String>();
        params.put("code", code);
        params.put("client_id", APP_KEY);
        params.put("client_secret", APP_SECRET);
        params.put("grant_type", GRANT_TYPE_AUTHORIZATION);
        params.put("redirect_uri", REDIRECT_URI);
        JSONObject object = HttpWorker.sendPostRequest(DROPBOX_TOKEN_URL, params);
        OAuth2UserData oAuth2UserData = parseDropboxData(object);
        return oAuth2UserData;
    }

    public static OAuth2UserData parseDropboxData(JSONObject jsonObject){
        OAuth2UserData oAuth2UserData = new OAuth2UserData();
        oAuth2UserData.setAccessToken(jsonObject.getString("access_token"));
        oAuth2UserData.setUniqueCloudId(jsonObject.getString("uid"));
        return oAuth2UserData;
    }

    private String getFileNameFromFilePath(String filePath){
        return filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
    }
}