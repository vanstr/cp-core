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

    class FileFetcher implements Runnable{

        protected ContentBean bean;
        protected String folderPath;
        protected Boolean recursive;
        protected Long userId;
        protected List<String[]> files;

        public FileFetcher(ContentBean bean, String folderPath, Boolean recursive, Long userId){
            this.bean = bean;
            this.folderPath = folderPath;
            this.recursive = recursive;
            this.userId = userId;
        }

        public void run(){}

        public List<String[]> getFiles() {
            return files;
        }
    }

    class DropboxFileFetcher extends FileFetcher implements Runnable{

        public DropboxFileFetcher(ContentBean bean, String folderPath, Boolean recursive, Long userId) {
            super(bean, folderPath, recursive, userId);
        }

        @Override
        public void run(){
            files = bean.getDropboxFiles(folderPath, recursive, userId);
        }
    }

    class DriveFileFetcher extends FileFetcher implements Runnable{

        public DriveFileFetcher(ContentBean bean, String folderPath, Boolean recursive, Long userId) {
            super(bean, folderPath, recursive, userId);
        }

        @Override
        public void run(){
            files = bean.getDriveFiles(folderPath, recursive, userId);
        }
    }

    private List<String> fileTypes = Arrays.asList("mp3", "wav", "ogg");

    public List<String[]> getFiles(String folderPath, Boolean recursive, Long userId) {

        DropboxFileFetcher dropboxFetcher = new DropboxFileFetcher(this, folderPath, recursive, userId);
        DriveFileFetcher driveFetcher = new DriveFileFetcher(this, folderPath, recursive, userId);
        Thread dropboxThread = new Thread(dropboxFetcher);
        Thread driveThread = new Thread(driveFetcher);
        dropboxThread.start();
        driveThread.start();
        try {
            dropboxThread.join();
            driveThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<String[]> files = new ArrayList<String[]>();
        if(dropboxFetcher.getFiles() != null){
            files.addAll(dropboxFetcher.getFiles());
        }
        if(driveFetcher.getFiles() != null){
            files.addAll(driveFetcher.getFiles());
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
            if(accessTokenKey != null && accessTokenSecret != null){
                Dropbox drop = new Dropbox(accessTokenKey, accessTokenSecret);
                files = drop.getFileList(folderPath, recursive, fileTypes);
            }
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
            String driveAccessToken = user.getDriveAccessToken();
            String driveRefreshToken = user.getDriveRefreshToken();
            if(driveAccessToken != null && driveRefreshToken != null){
                gDrive = new GDrive(driveAccessToken, driveRefreshToken);
                files = gDrive.getFileList(folderPath, recursive, fileTypes);
            }
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
