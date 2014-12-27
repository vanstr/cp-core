package clouds;

import structures.OAuth2UserData;
import structures.Song;

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

    public String refreshToken(String refreshToken);

    public OAuth2UserData retrieveAccessToken(String code, String redirectUrl);

}
