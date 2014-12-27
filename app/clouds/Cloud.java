package clouds;

import structures.OAuth2UserData;
import structures.Song;

import java.net.URL;
import java.util.List;

public interface Cloud {
    /**
     * Get file link for downloading
     * @return  file download link
     */
    public String getFileLink(String fileId);

    /**
     * @param folderPath         - in which folder look up
     * @param requestedFileTypes - if file type == NULL return all list, ex: folder, files, mp3, txt
     * @return array of file
     */
    public List<Song> getFileList(String folderPath, List<String> requestedFileTypes);

    /**
     * Upload remote file to cloud
     * @param fullDestPath         - destination path and file name where file should be saved on cloud
     * @param link                 - URL link which should be uploaded
     * @return boolean, true on success
     */
    public Boolean uploadFileByUrl(String fullDestPath, URL link);

    public OAuth2UserData retrieveAccessToken(String code, String redirectUrl);

}
