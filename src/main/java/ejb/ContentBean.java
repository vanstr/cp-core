package ejb;

import cloud.Dropbox;
import cloud.GDrive;
import persistence.UserEntity;
import persistence.UserManager;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Arrays;
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

    private List<String> fileTypes = Arrays.asList("mp3", "wav", "ogg");

    public List<String> getFiles(String folderPath, Boolean recursive, Long userId) {

        ArrayList<String> files = null;

        try {
            // if files from dropbox
            if (true) {

                UserManager manager = new UserManager();
                UserEntity user = manager.getUserById(userId);

                String accessTokenKey = user.getDropboxAccessKey();
                String accessTokenSecret = user.getDropboxAccessSecret();
                Dropbox drop = new Dropbox(accessTokenKey, accessTokenSecret);

                files = drop.getFileList(folderPath, recursive, fileTypes);

            }
            // else if files from GDrive

        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }

    public List<String> getDriveFiles(String folderPath, Boolean recursive, Long userId){
        List<String> files = null;
        try{
            UserManager manager = new UserManager();
            UserEntity user = manager.getUserById(userId);
            String driveAccessToken = user.getDriveAccessToken();
            GDrive gDrive = new GDrive(driveAccessToken);
            List<String> list = gDrive.getFileList(folderPath, recursive, fileTypes);
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }

        return files;
    }

    public String getFileSrc(String path, Long userId) {

        String file = null;

        try {
            // if files from dropbox
            if (true) {
                UserManager manager = new UserManager();
                UserEntity user = manager.getUserById(userId);

                String accessTokenKey = user.getDropboxAccessKey();
                String accessTokenSecret = user.getDropboxAccessSecret();

                Dropbox drop = new Dropbox(accessTokenKey, accessTokenSecret);
                file = drop.getFileLink(path);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;

    }
}
