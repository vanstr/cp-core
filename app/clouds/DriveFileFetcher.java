package clouds;

import commons.FileFetcher;
import models.UserEntity;
import play.Logger;
import structures.PlayList;

public class DriveFileFetcher extends FileFetcher{

    public DriveFileFetcher(String folderPath, Long userId) {
        super(folderPath, userId);
    }

    @Override
    public PlayList getCloudPlayList(String folderPath, Long userId){
        playList = new PlayList();
        UserEntity userEntity = UserEntity.getUserById(userId);
        try{
            String driveRefreshToken = userEntity.getDriveRefreshToken();
            if(driveRefreshToken != null){
                Cloud gDrive = new GDrive(driveRefreshToken);
                playList.setSongs(gDrive.getFileList(folderPath, REQUIRED_FILE_TYPES));
            }
        } catch (Exception e){
            Logger.error("Exception in getCloudPlayList", e);
        }
        return playList;
    }

}