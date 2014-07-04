package cloud;

import cloud.oauth.OAuth2Communicator;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxUrlWithExpiration;
import commons.CloudFile;
import commons.SystemProperty;
import ejb.ContentBeanRemote;
import org.json.JSONObject;
import structure.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * UserEntity: vanstr
 * Date: 13.29.6
 * Time: 20:55
 * Class represent basic cloud.Dropbox API functionality
 * Like: getFileList, getFileLink
 */
public class Dropbox extends OAuth2Communicator {

    private static final String GRANT_TYPE_AUTHORIZATION = "authorization_code";

    private DbxRequestConfig config = new DbxRequestConfig(
            "Cloud_Player", Locale.getDefault().toString());
    private DbxClient client;

    public Dropbox(){}

    public Dropbox(String accessToken) throws Exception {
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

    @Override
    public OAuth2UserData retrieveAccessToken(String code){
        JSONObject object = super.retrieveAccessToken(code, SystemProperty.DROPBOX_APP_KEY,
                SystemProperty.DROPBOX_APP_SECRET, GRANT_TYPE_AUTHORIZATION,
                SystemProperty.DROPBOX_REDIRECT_URI, null, SystemProperty.DROPBOX_TOKEN_URL);
        OAuth2UserData oAuth2UserData = parseDropboxData(object);
        return oAuth2UserData;
    }

    @Override
    public String refreshToken(String refreshToken) {
        return null;
    }

    private static OAuth2UserData parseDropboxData(JSONObject jsonObject){
        OAuth2UserData oAuth2UserData = new OAuth2UserData();
        oAuth2UserData.setAccessToken(jsonObject.getString("access_token"));
        oAuth2UserData.setUniqueCloudId(jsonObject.getString("uid"));
        return oAuth2UserData;
    }

    private String getFileNameFromFilePath(String filePath){
        return filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
    }
}