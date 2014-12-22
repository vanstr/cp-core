package commons;

import structure.PlayList;

/**
 * Created by alex on 12/21/14.
 */
public class PlayListHelper {
    public static PlayList mergePlayLists(PlayList playList1, PlayList playList2){
        PlayList playList = new PlayList();
        if(playList1 != null){
            playList.addSongs(playList1.getSongs());
            if(playList1.getNextPageToken() != null && !"".equals(playList1.getNextPageToken())){
                playList.setNextPageToken(playList1.getNextPageToken());
            }
        }
        if(playList2 != null){
            playList.addSongs(playList2.getSongs());
            if(playList2.getNextPageToken() != null && !"".equals(playList2.getNextPageToken())){
                playList.setNextPageToken(playList2.getNextPageToken());
            }
        }
        return playList;
    }
}
