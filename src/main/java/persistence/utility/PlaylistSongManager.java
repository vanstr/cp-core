package persistence.utility;

import persistence.PlaylistSongEntity;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 7/21/14
 * Time: 11:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistSongManager extends EntityManager<PlaylistSongEntity> {

    public static final String table = PlaylistSongEntity.class.getName();

    public PlaylistSongManager() {
        super(table);
    }
}
