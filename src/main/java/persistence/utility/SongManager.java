package persistence.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.SongEntity;
import persistence.UserEntity;

import java.util.HashMap;
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
    final static Logger logger = LoggerFactory.getLogger(SongManager.class);
    public static final String table = "SongEntity";


    public SongEntity getSongById(long id) {
        return getEntityById(SongEntity.class, id);
    }


    public boolean updateSong(SongEntity song) {
        return updateEntity(song);
    }


    public List<SongEntity> getSongsByFields(Map<String, Object> fields) {
        return getEntitiesByFields(fields, table);
    }

    public boolean addSong(SongEntity song) {
        return addEntity(song);
    }


    public boolean deleteSongsByID(List<Long> ids) {
        return deleteEntityByIDs(ids, table);
    }


    public SongEntity getSongByHash(UserEntity user, long cloudId, String fileName) {

        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("cloudId", cloudId);
        fieldMap.put("fileName", fileName);
        fieldMap.put("user", user);
        List<SongEntity> list = getEntitiesByFields(fieldMap, table);

        SongEntity songEntity = null;
        if (list != null) {
            if (list.size() == 1) {
                songEntity = list.get(0);
            } else {
                logger.warn("incorrect return value list.size()=" + list.size());
            }
        } else {
            logger.info("Returned list=" + null);
        }

        return songEntity;
    }
}
