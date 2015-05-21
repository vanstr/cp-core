package clouds;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxUrlWithExpiration;
import com.dropbox.core.DbxWriteMode;
import commons.CloudFile;
import commons.HttpWorker;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import structures.OAuth2UserData;
import structures.Song;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static commons.SystemProperty.*;

public class Dropbox implements Cloud {

    private static final String GRANT_TYPE_AUTHORIZATION = "authorization_code";
    private DbxClient client;

    public Dropbox() {
    }

    public Dropbox(String accessToken){
        DbxRequestConfig config = new DbxRequestConfig("Cloud_Player", Locale.getDefault().toString());
        client = new DbxClient(config, accessToken);
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

                        files.add(new Song(DROPBOX_CLOUD_ID,
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

    private int getRemoteFileSize(URL link){
        int size = 0;
        try {
            URLConnection conn = link.openConnection();
            size = conn.getContentLength();
            if(size < 0) {
                Logger.debug("Could not determine file size.");
            } else {
                Logger.debug(link.getFile() + " size: " + size);
            }
            conn.getInputStream().close();
        }
        catch(Exception e) {
            Logger.error("Error " + e);
        }
        return size;

    }

    @Override
    public Boolean uploadFileByUrl(String fullDestPath, URL link) {

        Boolean res = false;

        try {
            InputStream inputStream = new BufferedInputStream(link.openStream());
            int fileSize = getRemoteFileSize(link);

            DbxEntry.File uploadedFile = client.uploadFile(fullDestPath, DbxWriteMode.add(), fileSize, inputStream);
            Logger.debug("Uploaded: " + uploadedFile.toString());
            res = true;
        } catch (Exception e) {
            Logger.error("Error in uploadFileByUrl: " + e);
        }
        return res;
    }



    @Override
    public OAuth2UserData retrieveAccessToken(String code, String redirectUrl) {
        JSONObject object = HttpWorker.retrieveAccessToken(code, DROPBOX_APP_KEY,
                DROPBOX_APP_SECRET, GRANT_TYPE_AUTHORIZATION,
                redirectUrl, null, DROPBOX_TOKEN_URL);
        OAuth2UserData oAuth2UserData = null;
        try {
            oAuth2UserData = parseDropboxData(object);
        } catch (JSONException e) {
            Logger.error("Exception in retrieveAccessToken", e);
        }
        return oAuth2UserData;
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