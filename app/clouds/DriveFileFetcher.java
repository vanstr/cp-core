package clouds;

import commons.FileFetcher;
import commons.exceptions.UnauthorizedAccessException;
import models.UserEntity;
import play.Logger;
import structure.PlayList;

public class DriveFileFetcher extends FileFetcher{

    public DriveFileFetcher(String folderPath, Long userId) {
        super(folderPath, userId);
    }

    public PlayList getCloudPlayList(String folderPath, Long userId){
        GDrive gDrive = null;

        UserEntity userEntity = UserEntity.getUserById(userId);
        try{
            String driveAccessToken = userEntity.getDriveAccessToken();
            String driveRefreshToken = userEntity.getDriveRefreshToken();
            if(driveAccessToken != null && driveRefreshToken != null){
                gDrive = new GDrive(driveAccessToken, driveRefreshToken);
                this.playList = gDrive.getFileList(folderPath, REQUIRED_FILE_TYPES);
            }
        } catch (UnauthorizedAccessException e) {
            gDrive.setAccessToken(gDrive.refreshToken(gDrive.getRefreshToken()));
            try {
                this.playList = gDrive.getFileList(folderPath, REQUIRED_FILE_TYPES);
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

}