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


        UserEntity user = UserEntity.getUserById(userId);
        try{
            String driveAccessToken = user.driveAccessToken;
            String driveRefreshToken = user.driveRefreshToken;
            if(driveAccessToken != null && driveRefreshToken != null){
                gDrive = new GDrive(driveAccessToken, driveRefreshToken);
                files = gDrive.getFileList(folderPath, REQUIRED_FILE_TYPES);
            }
        } catch (UnauthorizedAccessException e) {
            if("401".equals(e.getMessage())){
                gDrive.setAccessToken(gDrive.refreshToken(gDrive.getRefreshToken()));
                try {
                    files = gDrive.getFileList(folderPath, REQUIRED_FILE_TYPES);
                    user.driveAccessToken = gDrive.getAccessToken();
                    user.update();
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