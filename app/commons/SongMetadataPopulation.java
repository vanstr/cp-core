package commons;

import models.SongEntity;
import models.UserEntity;
import structure.PlayList;
import structure.Song;
import structure.SongMetadata;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.1.3
 * Time: 21:42
 * To change this template use File | Settings | File Templates.
 */
public class SongMetadataPopulation {

    public static void populate(PlayList playList, long userId) {

        UserEntity userEntity = UserEntity.getUserById(userId);

        if(playList != null) {
            int size = playList.getSongs().size();
            for (int i = 0; i < size; i++) {
                Song song = playList.getSongs().get(i);

                SongEntity songEntity = SongEntity.getSongByHash(userEntity, song.getCloudId(), song.getFileId());
                if (songEntity != null) {
                    SongMetadata metadata = new SongMetadata(songEntity);
                    song.setMetadata(metadata);
                }
            }
        }
    }
}
