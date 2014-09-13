package commons;

import models.SongEntity;
import models.UserEntity;
import structure.PlayList;
import structure.Song;
import structure.SongMetadata;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.1.3
 * Time: 21:42
 * To change this template use File | Settings | File Templates.
 */
public class SongMetadataPopulation {

    public static PlayList populate(List<Song> data, long userId) {

        UserEntity userEntity = UserEntity.getUserById(userId);

        PlayList playList = new PlayList();

        int size = data.size();
        for (int i = 0; i < size; i++) {
            Song song = data.get(i);

            SongEntity songEntity = SongEntity.getSongByHash(userEntity, song.getCloudId(), song.getFileName());
            if (songEntity != null) {
                SongMetadata metadata = new SongMetadata(songEntity);
                song.setMetadata(metadata);
            }

            playList.add(song);
        }

        return playList;

    }
}
