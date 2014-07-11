package structure;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.1.3
 * Time: 22:03
 * To change this template use File | Settings | File Templates.
 */
public class PlayList extends ArrayList<Song> implements Serializable{

    private long id;
    private String name;

    public PlayList(){}

    public PlayList(long id, String name){
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
