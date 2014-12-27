package clouds;

import com.dropbox.core.*;
import commons.CloudFile;
import commons.SystemProperty;
import play.Logger;
import structures.OAuth2UserData;
import structures.Song;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Dropbox implements Cloud {

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
    public Boolean uploadFile(String fullDestPath, File inputFile) {
        inputFile = new File("working-draft.txt");
        fullDestPath = "/magnum-opus.txt";

        Boolean res = false;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(inputFile);
            DbxEntry.File uploadedFile = client.uploadFile(fullDestPath, DbxWriteMode.add(), inputFile.length(), inputStream);
            Logger.debug("Uploaded: " + uploadedFile.toString());
            res = true;
        } catch (Exception e) {
            Logger.error("Error in uploadFile: " + e);
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                Logger.error("Error close inputStream in uploadFile: " + e);
            }
        }
        return res;
    }

    @Override
    public OAuth2UserData retrieveAccessToken(String code, String redirectUrl) {

        OAuth2UserData oAuth2UserData = new OAuth2UserData();
        try {
            oAuth2UserData.setAccessToken(client.getAccessToken());
            oAuth2UserData.setUniqueCloudId(((Long) client.getAccountInfo().userId).toString());
        } catch (DbxException e) {
            Logger.error("Error retrieveAccessToken" + e);
            oAuth2UserData = null;
        }

        return oAuth2UserData;
    }

    @Override
    public String refreshToken(String refreshToken) {
        return null;
    }


    private String getFileNameFromFilePath(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
    }
}