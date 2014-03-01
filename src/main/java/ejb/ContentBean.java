package ejb;

import cloud.DriveFileFetcher;
import cloud.Dropbox;
import cloud.DropboxFileFetcher;
import cloud.GDrive;
import commons.FileFetcher;
import commons.SongMetadataPopulation;
import persistence.UserEntity;
import persistence.utility.UserManager;
import structure.PlayList;

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


    public List<String[]> getFiles(String folderPath, Long userId) {

        FileFetcher dropboxFetcher = new DropboxFileFetcher(folderPath, userId);
        FileFetcher driveFetcher = new DriveFileFetcher(folderPath, userId);
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
        if (dropboxFetcher.getFiles() != null) {
            files.addAll(dropboxFetcher.getFiles());
        }
        if (driveFetcher.getFiles() != null) {
            files.addAll(driveFetcher.getFiles());
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
            } else if (DRIVE_CLOUD_ID.equals(cloudId)) {
                GDrive gDrive = new GDrive(user.getDriveAccessToken(), user.getDriveRefreshToken());
                file = gDrive.getFileLink(driveFileId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        manager.finalize();

        return file;

    }

    @Override
    public PlayList getPlayList(Long userId) {

        List<String[]> data = getFiles("/", userId);
        PlayList playList = SongMetadataPopulation.populate(data, userId);

        return playList;
    }
}
