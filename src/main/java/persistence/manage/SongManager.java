package persistence.manage;

import persistence.SongEntity;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.19.2
 * Time: 21:12
 * To change this template use File | Settings | File Templates.
 */
public class SongManager extends EntityManager<SongEntity> {

    public static final String table =  "SongEntity";


    public SongEntity getSongById(long id) {

        return getEntityById(SongEntity.class, id);
    }


    public boolean updateSong(final SongEntity song) {
        return updateEntity(song);

    }



    public List<SongEntity> getSongsByFields(Map<String, Object> fields) {

        return getEntitiesByFields(fields, table);

    }

    public boolean addSong(SongEntity song) {
        return addEntity(song);
    }


    public boolean deleteSongsByID(final List<Long> ids) {
        return deleteEntityByIDs(ids, table);
    }


}
