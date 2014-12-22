package clouds;

import commons.FileFetcher;
import commons.exceptions.UnauthorizedAccessException;
import models.UserEntity;
import play.Logger;
import structure.PlayList;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.25.2
 * Time: 19:23
 * To change this template use File | Settings | File Templates.
 */
public class DriveFileFetcher extends FileFetcher{

    private String nextPageToken;

    public DriveFileFetcher(String nextPageToken, String folderPath, Long userId) {
        super(folderPath, userId);
        this.nextPageToken = nextPageToken;
    }

    public PlayList getCloudPlayList(String folderPath, Long userId){
        GDrive gDrive = null;

        UserEntity userEntity = UserEntity.getUserById(userId);
        try{
            String driveAccessToken = userEntity.getDriveAccessToken();
            String driveRefreshToken = userEntity.getDriveRefreshToken();
            if(driveAccessToken != null && driveRefreshToken != null){
                gDrive = new GDrive(driveAccessToken, driveRefreshToken);
                this.playList = gDrive.getFileList(this.nextPageToken, folderPath, REQUIRED_FILE_TYPES);
            }
        } catch (UnauthorizedAccessException e) {
            gDrive.setAccessToken(gDrive.refreshToken(gDrive.getRefreshToken()));
            try {
                this.playList = gDrive.getFileList(nextPageToken, folderPath, REQUIRED_FILE_TYPES);
                userEntity.setDriveAccessToken(gDrive.getAccessToken());
                userEntity.update();
            } catch (Exception e1) {
                Logger.error("Exception in getCloudPlayList", e1);
            }
        } catch (Exception e){
            Logger.error("Exception in getCloudPlayList", e);
        }
        return this.playList;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
}