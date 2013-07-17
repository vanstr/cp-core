import cloud.Dropbox;
import persistence.UserEntity;
import persistence.UserManager;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * UserEntity: vanstr
 * Date: 13.6.7
 * Time: 21:45
 * To change this template use File | Settings | File Templates.
 */

@Stateless
@Remote(ContentBeanRemote.class)
public class ContentBean implements ContentBeanRemote {

    public List<String> getFiles(String folderPath, Boolean recursive, Long userId) {

        ArrayList<String> files = null;

        // if files from dropbox
        if (true) {

            UserManager manager = new UserManager();
            UserEntity user = manager.getUserById(userId);

            String accessTokenKey = user.getDropboxAccessKey();
            String accessTokenSecret = user.getDropboxAccessSecret();
            Dropbox drop = new Dropbox(accessTokenKey, accessTokenSecret);
            files = drop.getFileList(folderPath, recursive, "mp3");

        }

        // if files from GDrive
        return files;
    }

    public String getFileSrc(String path, Long userId) {

        String file = null;
        // if files from dropbox
        if (true) {
            UserManager manager = new UserManager();
            UserEntity user = manager.getUserById(userId);

            String accessTokenKey = user.getDropboxAccessKey();
            String accessTokenSecret = user.getDropboxAccessSecret();

            Dropbox drop = new Dropbox(accessTokenKey, accessTokenSecret);
            file = drop.getFileLink(path);
        }
        return file;

    }
}
