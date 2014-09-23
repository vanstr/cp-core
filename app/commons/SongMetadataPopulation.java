package commons;

import models.Song;
import models.User;
import structure.PlayList;
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

    public static PlayList populate(List<structure.Song> data, long userId) {

        User user = User.getUserById(userId);

        PlayList playList = new PlayList();

        int size = data.size();
        for (int i = 0; i < size; i++) {
            structure.Song song = data.get(i);

            Song songEntity = Song.getSongByHash(user, song.getCloudId(), song.getFileName());
            if (songEntity != null) {
                SongMetadata metadata = new SongMetadata(songEntity);
                song.setMetadata(metadata);
            }

            playList.add(song);
        }

        return playList;

    }
}
