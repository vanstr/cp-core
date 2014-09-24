package clouds;

import commons.FileFetcher;
import commons.exceptions.UnauthorizedAccessException;
import models.UserEntity;
import structure.Song;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.25.2
 * Time: 19:23
 * To change this template use File | Settings | File Templates.
 */
public class DriveFileFetcher extends FileFetcher{

    public DriveFileFetcher(String folderPath, Long userId) {
        super(folderPath, userId);
    }

    public List<Song> getCloudFiles(String folderPath, Long userId){
        List<Song> files = null;
        GDrive gDrive = null;

        UserEntity userEntity = UserEntity.getUserById(userId);
        try{
            String driveAccessToken = userEntity.getDriveAccessToken();
            String driveRefreshToken = userEntity.getDriveRefreshToken();
            if(driveAccessToken != null && driveRefreshToken != null){
                gDrive = new GDrive(driveAccessToken, driveRefreshToken);
                files = gDrive.getFileList(folderPath, REQUIRED_FILE_TYPES).getSongs();
            }
        } catch (UnauthorizedAccessException e) {
            if("401".equals(e.getMessage())){
                gDrive.setAccessToken(gDrive.refreshToken(gDrive.getRefreshToken()));
                try {
                    files = gDrive.getFileList(folderPath, REQUIRED_FILE_TYPES).getSongs();
                    userEntity.setDriveAccessToken(gDrive.getAccessToken());
                    userEntity.update();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return files;
    }

}