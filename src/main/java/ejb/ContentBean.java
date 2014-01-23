package ejb;

import cloud.Dropbox;
import cloud.GDrive;
import com.sun.servicetag.UnauthorizedAccessException;
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

    public List<String[]> getFiles(String folderPath, Boolean recursive, Long userId) {
        //TODO threads
        List<String[]> files = new ArrayList<String[]>();
        List<String[]> dropboxFiles = this.getDropboxFiles(folderPath, recursive, userId);
        if(dropboxFiles != null){
            files.addAll(dropboxFiles);
        }
        List<String[]> driveFiles = this.getDriveFiles(folderPath, recursive, userId);
        if(driveFiles != null){
            files.addAll(driveFiles);
        }
        return files;
    }

    public List<String[]> getDropboxFiles(String folderPath, Boolean recursive, Long userId) {
        List<String[]> files = null;
        try {
            UserManager manager = new UserManager();
            UserEntity user = manager.getUserById(userId);

            String accessTokenKey = user.getDropboxAccessKey();
            String accessTokenSecret = user.getDropboxAccessSecret();
            Dropbox drop = new Dropbox(accessTokenKey, accessTokenSecret);

            files = drop.getFileList(folderPath, recursive, fileTypes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    public List<String[]> getDriveFiles(String folderPath, Boolean recursive, Long userId){
        List<String[]> files = null;
        GDrive gDrive = null;
        UserEntity user = null;
        UserManager manager = null;
        try{
            manager = new UserManager();
            user = manager.getUserById(userId);
            gDrive = new GDrive(user.getDriveAccessToken(), user.getDriveRefreshToken());
            files = gDrive.getFileList(folderPath, recursive, fileTypes);
        } catch (UnauthorizedAccessException e) {
            if("401".equals(e.getMessage())){
                gDrive.setAccessToken(gDrive.refreshToken(gDrive.getRefreshToken()));
                try {
                    files = gDrive.getFileList(folderPath, recursive, fileTypes);
                    user.setDriveAccessToken(gDrive.getAccessToken());
                    manager.updateUser(user);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(manager != null){
                manager.finalize();
            }
        }
        return files;
    }

    public String getFileSrc(Integer cloudId, String path, Long userId, String driveFileId) {
        String file = null;
        UserManager manager = new UserManager();
        UserEntity user = manager.getUserById(userId);
        try {
            if (DROPBOX_CLOUD_ID.equals(cloudId)) {
                String accessTokenKey = user.getDropboxAccessKey();
                String accessTokenSecret = user.getDropboxAccessSecret();

                Dropbox drop = new Dropbox(accessTokenKey, accessTokenSecret);
                file = drop.getFileLink(path);
            }else if(DRIVE_CLOUD_ID.equals(cloudId)){
                GDrive gDrive = new GDrive(user.getDriveAccessToken(), user.getDriveRefreshToken());
                file = gDrive.getFileLink(driveFileId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;

    }
}
