package clouds;

import commons.FileFetcher;
import models.UserEntity;
import structure.Song;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.25.2
 * Time: 19:23
 * To change this template use File | Settings | File Templates.
 */
public class DropboxFileFetcher extends FileFetcher {

    public DropboxFileFetcher(String folderPath, Long userId) {
        super(folderPath, userId);
    }

    public List<Song> getCloudFiles(String folderPath, Long userId) {
        List<Song> files = null;
        try {
            UserEntity userEntity = UserEntity.getUserById(userId);
            String accessTokenKey = userEntity.dropboxAccessKey;

            if (accessTokenKey != null) {
                Dropbox drop = new Dropbox(accessTokenKey);
                files = drop.getFileList(folderPath, REQUIRED_FILE_TYPES);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
}
