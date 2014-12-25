package commons;

import structure.PlayList;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.25.2
 * Time: 19:22
 * To change this template use File | Settings | File Templates.
 */
//TODO: refactor
public abstract class FileFetcher implements Runnable{

    public static final List<String> REQUIRED_FILE_TYPES = Arrays.asList("mp3", "wav", "ogg");

    protected String folderPath;
    protected Long userId;
    protected PlayList playList;

    public FileFetcher(String folderPath, Long userId){
        this.folderPath = folderPath;
        this.userId = userId;
    }

    public PlayList getPlayList() {
        return playList;
    }

    @Override
    public void run(){
        playList = getCloudPlayList(folderPath, userId);
    }

    public abstract PlayList getCloudPlayList(String folderPath, Long userId);
}