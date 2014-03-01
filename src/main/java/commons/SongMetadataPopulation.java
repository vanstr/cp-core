package commons;

import persistence.UserEntity;
import structure.PlayList;
import structure.Song;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.1.3
 * Time: 21:42
 * To change this template use File | Settings | File Templates.
 */
public class SongMetadataPopulation {

    public static PlayList populate(  List<String[]> data, long userId ){

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        PlayList playList = new PlayList();

        int size = data.size();
        for (int i = 0; i< size; i++){
            String[] item = data.get(i);

            Song song = new Song(userEntity, Long.parseLong(item[0]),item[1],item[2],item[3]);
            playList.add(song);
        }

        return playList;

    }
}
