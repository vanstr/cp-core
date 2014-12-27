package commons;

import structures.PlayList;

import java.util.Arrays;
import java.util.List;


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