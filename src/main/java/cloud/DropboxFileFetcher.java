package cloud;

import commons.FileFetcher;
import persistence.UserEntity;
import persistence.utility.UserManager;
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
        UserManager manager = new UserManager();
        try {
            UserEntity user = manager.getUserById(userId);

            String accessTokenKey = user.getDropboxAccessKey();
            String accessTokenSecret = user.getDropboxAccessSecret();
            if (accessTokenKey != null && accessTokenSecret != null) {
                Dropbox drop = new Dropbox(accessTokenKey, accessTokenSecret);
                files = drop.getFileList(folderPath, REQUIRED_FILE_TYPES);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            manager.finalize();
        }
        return files;
    }
}
