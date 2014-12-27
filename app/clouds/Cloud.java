package clouds;

import structures.OAuth2UserData;
import structures.Song;

import java.io.File;
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
     * Upload file to cloud
     * @param fullDestPath         - path with file name where file should be saved
     * @param inputFile            - file
     * @return boolean, true on success
     */
    public Boolean uploadFile(String fullDestPath, File inputFile);

    public String refreshToken(String refreshToken);

    public OAuth2UserData retrieveAccessToken(String code, String redirectUrl);

}
