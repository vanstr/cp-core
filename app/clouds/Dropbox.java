package clouds;

import clouds.oauth.OAuth2Communicator;
import clouds.oauth.OAuth2UserData;
import com.dropbox.core.*;
import commons.CloudFile;
import commons.SystemProperty;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import structure.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Dropbox extends OAuth2Communicator implements Cloud {

    private static final String GRANT_TYPE_AUTHORIZATION = "authorization_code";

    private DbxClient client;

    public Dropbox() {
    }

    public Dropbox(String accessToken) throws Exception {
        DbxRequestConfig config = new DbxRequestConfig("Cloud_Player", Locale.getDefault().toString());
        this.client = new DbxClient(config, accessToken);
    }

    @Override
    public String getFileLink(String filePath) {
        String res = null;
        try {
            res = client.createTemporaryDirectUrl(filePath).url;
        } catch (DbxException e) {
            Logger.error("Exception in getFileLink", e);
        }
        return res;
    }


    @Override
    public List<Song> getFileList(String folderPath, List<String> requestedFileTypes) {

        List<Song> files = new ArrayList<Song>();

        try {
            for (String requestedType : requestedFileTypes) {

                List<DbxEntry> dbxEntries = client.searchFileAndFolderNames(folderPath, "." + requestedType);

                for (DbxEntry entry : dbxEntries) {

                    if (entry.isFile() && CloudFile.checkFileType(entry.asFile().name, requestedFileTypes)) {
                        DbxUrlWithExpiration urlWithExpiration = client.createTemporaryDirectUrl(entry.path);

                        files.add(new Song(SystemProperty.DROPBOX_CLOUD_ID,
                                        entry.path,
                                        getFileNameFromFilePath(entry.path),
                                        urlWithExpiration.url,
                                        urlWithExpiration.expires.getTime())
                        );
                    }
                }
            }
        } catch (DbxException e) {
            Logger.error("Exception in getFileList", e);
        }

        return files;
    }

    @Override
    public OAuth2UserData retrieveAccessToken(String code, String redirectUrl) {
        JSONObject object = super.retrieveAccessToken(code, SystemProperty.DROPBOX_APP_KEY,
                SystemProperty.DROPBOX_APP_SECRET, GRANT_TYPE_AUTHORIZATION,
                redirectUrl, null, SystemProperty.DROPBOX_TOKEN_URL);
        OAuth2UserData oAuth2UserData = null;
        try {
            oAuth2UserData = parseDropboxData(object);
        } catch (JSONException e) {
            Logger.error("Exception in retrieveAccessToken", e);
        }
        return oAuth2UserData;
    }

    @Override
    public String refreshToken(String refreshToken) {
        return null;
    }

    private static OAuth2UserData parseDropboxData(JSONObject jsonObject) throws JSONException {
        OAuth2UserData oAuth2UserData = new OAuth2UserData();
        oAuth2UserData.setAccessToken(jsonObject.getString("access_token"));
        oAuth2UserData.setUniqueCloudId(jsonObject.getString("uid"));
        return oAuth2UserData;
    }

    private String getFileNameFromFilePath(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
    }
}