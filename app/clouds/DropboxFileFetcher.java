package clouds;

import commons.FileFetcher;
import models.UserEntity;
import play.Logger;
import structures.PlayList;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.25.2
 * Time: 19:23
 * To change this template use File | Settings | File Templates.
 */
public class DropboxFileFetcher extends FileFetcher {

    public DropboxFileFetcher(String folderPath, Long userId) {
        super(folderPath, userId);
    }

    @Override
    public PlayList getCloudPlayList(String folderPath, Long userId) {
        playList = new PlayList();
        try {
            UserEntity userEntity = UserEntity.getUserById(userId);
            String accessTokenKey = userEntity.getDropboxAccessKey();

            if (accessTokenKey != null) {
                Dropbox drop = new Dropbox(accessTokenKey);
                playList.setSongs(drop.getFileList(folderPath, REQUIRED_FILE_TYPES));
            }
        } catch (Exception e) {
            Logger.error("Exception in getCloudPlayList", e);
        }
        return playList;
    }
}
