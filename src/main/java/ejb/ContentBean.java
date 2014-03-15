package ejb;

import cloud.Dropbox;
import cloud.GDrive;
import com.sun.servicetag.UnauthorizedAccessException;
import commons.CloudFile;
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
        protected Long userId;
        protected List<CloudFile> files;

        public FileFetcher(ContentBean bean, String folderPath, Long userId){
            this.bean = bean;
            this.folderPath = folderPath;
            this.userId = userId;
        }

        public void run(){}

        public List<CloudFile> getFiles() {
            return files;
        }
    }

    class DropboxFileFetcher extends FileFetcher implements Runnable{

        public DropboxFileFetcher(ContentBean bean, String folderPath, Long userId) {
            super(bean, folderPath, userId);
        }

        @Override
        public void run(){
            files = bean.getDropboxFiles(folderPath, userId);
        }
    }

    class DriveFileFetcher extends FileFetcher implements Runnable{

        public DriveFileFetcher(ContentBean bean, String folderPath, Long userId) {
            super(bean, folderPath, userId);
        }

        @Override
        public void run(){
            files = bean.getDriveFiles(folderPath, userId);
        }
    }

    private List<String> fileTypes = Arrays.asList("mp3", "wav", "ogg");

    public List<CloudFile> getFiles(String folderPath, Long userId) {

        DropboxFileFetcher dropboxFetcher = new DropboxFileFetcher(this, folderPath, userId);
        DriveFileFetcher driveFetcher = new DriveFileFetcher(this, folderPath, userId);
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

        List<CloudFile> files = new ArrayList<CloudFile>();
        if(dropboxFetcher.getFiles() != null){
            files.addAll(dropboxFetcher.getFiles());
        }
        if(driveFetcher.getFiles() != null){
            files.addAll(driveFetcher.getFiles());
        }

        return files;
    }

    public List<CloudFile> getDropboxFiles(String folderPath, Long userId) {
        List<CloudFile> files = null;
        UserManager manager = new UserManager();
        try {
            UserEntity user = manager.getUserById(userId);

            String accessTokenKey = user.getDropboxAccessKey();
            if(accessTokenKey != null){
                Dropbox drop = new Dropbox(accessTokenKey);
                files = drop.getFileList(folderPath, fileTypes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            manager.finalize();
        }
        return files;
    }

    public List<CloudFile> getDriveFiles(String folderPath, Long userId){
        List<CloudFile> files = null;
        GDrive gDrive = null;
        UserEntity user = null;
        UserManager manager = new UserManager();
        try{
            user = manager.getUserById(userId);
            String driveAccessToken = user.getDriveAccessToken();
            String driveRefreshToken = user.getDriveRefreshToken();
            Long tokenExpires = user.getDriveTokenExpires();
            if(driveAccessToken != null && driveRefreshToken != null){
                gDrive = new GDrive(driveAccessToken, driveRefreshToken, tokenExpires);
                files = gDrive.getFileList(folderPath, fileTypes);
            }
        } catch (UnauthorizedAccessException e) {
            if("401".equals(e.getMessage())){
                gDrive.setAccessToken(gDrive.refreshToken(gDrive.getRefreshToken()));
                try {
                    files = gDrive.getFileList(folderPath, fileTypes);
                    user.setDriveAccessToken(gDrive.getAccessToken());
                    user.setDriveTokenExpires(gDrive.getTokenExpires());
                    manager.updateUser(user);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            manager.finalize();
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

                Dropbox drop = new Dropbox(accessTokenKey);
                file = drop.getFileLink(path);
            }else if(DRIVE_CLOUD_ID.equals(cloudId)){
                GDrive gDrive = new GDrive(user.getDriveAccessToken(), user.getDriveRefreshToken(), user.getDriveTokenExpires());
                file = gDrive.getFileLink(driveFileId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            manager.finalize();
        }

        return file;

    }
}
