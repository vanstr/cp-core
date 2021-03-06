package controllers;

import clouds.DriveFileFetcher;
import clouds.Dropbox;
import clouds.DropboxFileFetcher;
import clouds.GDrive;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import commons.FileFetcher;
import commons.PlayListHelper;
import commons.SongMetadataPopulation;
import commons.SystemProperty;
import controllers.commons.BaseController;
import controllers.commons.Secured;
import models.PlayListEntity;
import models.SongEntity;
import models.UserEntity;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import structure.PlayList;
import structure.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Security.Authenticated(Secured.class)
public class ContentApi extends BaseController {

    public static Result getFileSrc(Long cloudId, String fileId) {
        String file = null;
        try {
            UserEntity userEntity = getUserFromSession();
            if (SystemProperty.DROPBOX_CLOUD_ID.equals(cloudId)) {
                String accessTokenKey = userEntity.getDropboxAccessKey();
                Dropbox drop = new Dropbox(accessTokenKey);

                Logger.info(fileId);
                file = drop.getFileLink(fileId);
            } else if (SystemProperty.DRIVE_CLOUD_ID.equals(cloudId)) {
                GDrive gDrive = new GDrive(userEntity.getDriveAccessToken(), userEntity.getDriveRefreshToken());
                file = gDrive.getFileLink(fileId);
            }
        } catch (Exception e) {
            Logger.error("Exception in getFileSrc", e);
        }

        return returnInJsonOk(file);
    }

    public static Result getPlayList() {
        Logger.debug("getPlayList");
        Long userId = Long.parseLong(session("userId"));
        PlayList result = getPlayList("/", userId);
        SongMetadataPopulation.populate(result, userId);

        return returnInJsonOk(result);
    }

    public static Result getPlayListById(Long playListId) {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("id", playListId);
        fields.put("user_id", Long.parseLong(session("userId")));
        List<PlayListEntity> list = PlayListEntity.getPlayListsByFields(fields);

        PlayList playList = null;
        if(list != null && !list.isEmpty()) {
            PlayListEntity playListEntity = list.get(0);
            playList = new PlayList(playListId, playListEntity.getName());
            for (SongEntity songEntity : playListEntity.getSongs()) {
                playList.add(new Song(songEntity));
            }
        }

        return returnInJsonOk(playList);
    }

    public static Result saveSongMetadata() {
        JsonNode songNode = request().body().asJson();
        Song song = Json.fromJson(songNode, Song.class);
        UserEntity userEntity = getUserFromSession();

        // get song by id
        Logger.debug("name: " + song.getFileName() + "id: " + song.getFileId() + " cId: " + song.getCloudId() + "userId" + userEntity.getId());
        SongEntity songEntity = SongEntity.getSongByHash(userEntity, song.getCloudId(), song.getFileId());

        if (songEntity == null) {
            Logger.debug("Create new SongEntry with metadata");
            songEntity = new SongEntity(song);
            songEntity.setUser(userEntity);
            songEntity.save();
        } else {
            Logger.debug("Update metadata of existing new SongEntry");
            songEntity.setMetadata(song);
            songEntity.update();
        }

        return returnInJsonOk(songEntity.getId());
    }

    private static PlayList getPlayList(String folderPath, Long userId) {
        FileFetcher dropboxFetcher = new DropboxFileFetcher(folderPath, userId);
        FileFetcher driveFetcher = new DriveFileFetcher(folderPath, userId);
        Thread dropboxThread = new Thread(dropboxFetcher);
        Thread driveThread = new Thread(driveFetcher);
        dropboxThread.start();
        driveThread.start();
        try {
            dropboxThread.join();
            driveThread.join();
        } catch (InterruptedException e) {
            Logger.error("Exception in getPlayList", e);
        }

        PlayList playList = PlayListHelper.mergePlayLists(dropboxFetcher.getPlayList(), driveFetcher.getPlayList());

        return playList;
    }




    public static Result addPlayList() {
        JsonNode songNode = request().body().asJson();
        PlayList playList = Json.fromJson(songNode, PlayList.class);
        UserEntity user = getUserFromSession();

        Set<SongEntity> songs = new HashSet<SongEntity>();
        if(playList.getSongs() != null){
            List<Song> songsToAdd = new ArrayList<Song>(playList.getSongs());

            songs.addAll(addExistingSongs(songsToAdd, user));
            songs.addAll(addNewSongs(songsToAdd, user));
        }

        PlayListEntity playListEntity = new PlayListEntity(user, playList.getName());
        playListEntity.addSongEntities(songs);

        playListEntity.save();

        return returnInJsonOk(playListEntity.getId());
    }

    public static Result getPlayLists() {
        Long userId = Long.parseLong(session("userId"));
        List<PlayListEntity> entities = Ebean.createQuery(PlayListEntity.class)
                .where().eq("user_id", userId).findList();
        List<PlayList> playLists = new ArrayList<PlayList>();
        if(entities != null){
            for(PlayListEntity entity : entities){
                playLists.add(new PlayList(entity));
            }
        }
        return returnInJsonOk(playLists);
    }

    public static Result deletePlayList(Long playListId) {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("id", playListId);
        fields.put("user_id", Long.parseLong(session("userId")));
        List<PlayListEntity> list = PlayListEntity.getPlayListsByFields(fields);
        if(fields != null && !fields.isEmpty() && !list.isEmpty()) {
            PlayListEntity playListEntity = list.get(0);
            Ebean.delete(playListEntity);
        } else {
            return badRequest();
        }

        return ok();
    }

    public static Result addSongsToPlayList(){
        //TODO move JSON parsing to separate class/methods
        UserEntity userEntity = getUserFromSession();
        JsonNode node = request().body().asJson();
        Long playListId = node.findValue("playListId").asLong();
        JsonNode songsNode = node.findValue("songs");
        PlayListEntity playListEntity = PlayListEntity.getPlayListById(playListId);
        if(playListEntity != null && songsNode.isArray()) {
            List<SongEntity> songEntities = new ArrayList<SongEntity>();
            for (JsonNode songNode : songsNode) {
                String fileId = songNode.findValue("fileId").asText();
                Long cloudId = songNode.findValue("cloudId").asLong();
                SongEntity songEntity = SongEntity.getSongByHash(userEntity, cloudId, fileId);
                if(songEntity == null){
                    songEntity = new SongEntity(userEntity, cloudId, fileId, fileId);
                    songEntity.save();
                }
                songEntities.add(songEntity);
            }
            playListEntity.addSongEntities(songEntities);
            Ebean.save(playListEntity);
            return ok();
        }

        return badRequest();
    }

    public static Result removeSongFromPlayList(){
        UserEntity userEntity = getUserFromSession();
        JsonNode node = request().body().asJson();
        Long playListId = node.findValue("playListId").asLong();
        String fileId = node.findValue("fileId").asText();
        Long cloudId = node.findValue("cloudId").asLong();
        PlayListEntity playListEntity = PlayListEntity.getPlayListById(playListId);
        if(playListEntity != null){
            SongEntity songEntity = SongEntity.getSongByHash(userEntity, cloudId, fileId);
            if(songEntity != null){
                playListEntity.removeSongEntity(songEntity);
                Ebean.save(playListEntity);
                return ok();
            }
        }

        return badRequest();
    }

    private static Set<SongEntity> addExistingSongs(List<Song> songList, UserEntity user){
        Set<SongEntity> songs = new HashSet<SongEntity>();
        Iterator<Song> iterator = songList.iterator();
        while(iterator.hasNext()){
            Song currentSong = iterator.next();
            SongEntity songEntity = SongEntity.getSongByHash(user, currentSong.getCloudId(),
                    currentSong.getFileId());
            if(songEntity != null){
                songs.add(songEntity);
                iterator.remove();
            }
        }

        return songs;
    }

    private static Set<SongEntity> addNewSongs(List<Song> songList, UserEntity user){
        Set<SongEntity> songs = new HashSet<SongEntity>();

        for(int i = 0; i < songList.size(); i++){
            SongEntity songEntity = new SongEntity(songList.get(i));
            songEntity.setUser(user);
            songEntity.save();
            songs.add(songEntity);
        }

        return songs;
    }

}
